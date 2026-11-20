#!/usr/bin/env bash
# 운영 DB를 덤프해 같은 MySQL 서버의 eventor_dev로 복제한다(docs/migration.md 2절).
# 덤프 파일은 ~/eventor/backups/ 에 남겨 운영 백업으로도 쓴다.
#
# 사용법(eventor-back 폴더에서):
#   scripts/clone-prod-to-dev.sh            # eventor_dev가 비어 있을 때만 복제한다(없으면 만든다)
#   scripts/clone-prod-to-dev.sh --reset    # eventor_dev의 테이블을 모두 지우고 다시 복제한다(개발 DB 변경분은 사라진다)
#
# 접속 정보는 src/main/resources/application.env 에서 읽는다(git 제외 파일).
#   DB_HOST, DB_PORT, PROD_DB_NAME, DEV_DB_NAME, MYSQL_USERNAME, MYSQL_PASSWORD
set -euo pipefail

cd "$(dirname "$0")/.."
ENV_FILE=src/main/resources/application.env
[ -f "$ENV_FILE" ] || { echo "$ENV_FILE 이 없습니다"; exit 1; }
# 값에 공백이 있을 수 있어(메일 앱 비밀번호 등) source 하지 않고 필요한 키만 읽는다
env_get() { grep -E "^$1=" "$ENV_FILE" | head -1 | cut -d= -f2-; }
for k in DB_HOST DB_PORT PROD_DB_NAME DEV_DB_NAME MYSQL_USERNAME MYSQL_PASSWORD; do
  v=$(env_get "$k"); [ -n "$v" ] && printf -v "$k" '%s' "$v"
done

: "${DB_HOST:?DB_HOST 필요}" "${PROD_DB_NAME:?PROD_DB_NAME 필요}" "${MYSQL_USERNAME:?}" "${MYSQL_PASSWORD:?}"
DB_PORT=${DB_PORT:-3306}
DEV_DB_NAME=${DEV_DB_NAME:-eventor_dev}

if [ "$PROD_DB_NAME" = "$DEV_DB_NAME" ]; then
  echo "운영 DB와 개발 DB 이름이 같습니다: $PROD_DB_NAME"; exit 1
fi

# 비밀번호가 프로세스 목록에 보이지 않게 임시 옵션 파일로 넘긴다
CNF=$(mktemp); trap 'rm -f "$CNF"' EXIT; chmod 600 "$CNF"
printf '[client]\nhost=%s\nport=%s\nuser=%s\npassword=%s\ndefault-character-set=utf8mb4\n' \
  "$DB_HOST" "$DB_PORT" "$MYSQL_USERNAME" "$MYSQL_PASSWORD" > "$CNF"
MYSQL() { mysql --defaults-extra-file="$CNF" "$@"; }

EXISTS=$(MYSQL -N -e "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name='$DEV_DB_NAME'")
TABLES=$(MYSQL -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$DEV_DB_NAME'")
if [ "$TABLES" != "0" ] && [ "${1:-}" != "--reset" ]; then
  echo "$DEV_DB_NAME 에 테이블이 이미 $TABLES 개 있습니다. 비우고 다시 복제하려면 --reset"; exit 1
fi

BACKUP_DIR="$HOME/eventor/backups"; mkdir -p "$BACKUP_DIR"
DUMP="$BACKUP_DIR/${PROD_DB_NAME}-$(date +%Y%m%d-%H%M%S).sql.gz"

echo "1) 운영 DB($PROD_DB_NAME) 덤프 → $DUMP"
# --single-transaction: 운영 서비스를 멈추지 않고 일관된 시점으로 덤프(InnoDB)
mysqldump --defaults-extra-file="$CNF" --single-transaction --quick --routines --triggers \
  --no-tablespaces --hex-blob "$PROD_DB_NAME" | gzip > "$DUMP"

CHARSET=$(MYSQL -N -e "SELECT default_character_set_name FROM information_schema.schemata WHERE schema_name='$PROD_DB_NAME'")
COLLATE=$(MYSQL -N -e "SELECT default_collation_name FROM information_schema.schemata WHERE schema_name='$PROD_DB_NAME'")

if [ "$EXISTS" = "0" ]; then
  echo "2) $DEV_DB_NAME 생성($CHARSET / $COLLATE)"
  MYSQL -e "CREATE DATABASE \`$DEV_DB_NAME\` CHARACTER SET $CHARSET COLLATE $COLLATE;"
elif [ "$TABLES" != "0" ]; then
  # DB 자체(권한·문자셋)는 두고 테이블만 지운다
  echo "2) $DEV_DB_NAME 의 기존 테이블 $TABLES 개 삭제"
  DROPS=$(MYSQL -N -e "SELECT CONCAT('DROP TABLE IF EXISTS \`', table_name, '\`;') FROM information_schema.tables WHERE table_schema='$DEV_DB_NAME'")
  MYSQL "$DEV_DB_NAME" -e "SET FOREIGN_KEY_CHECKS=0; $DROPS SET FOREIGN_KEY_CHECKS=1;"
else
  echo "2) $DEV_DB_NAME 이 비어 있어 그대로 사용"
fi

echo "3) 덤프 적재"
gunzip -c "$DUMP" | MYSQL "$DEV_DB_NAME"

echo "4) 확인(테이블별 행 수)"
for t in $(MYSQL -N -e "SELECT table_name FROM information_schema.tables WHERE table_schema='$PROD_DB_NAME' AND table_type='BASE TABLE' ORDER BY table_name"); do
  a=$(MYSQL -N -e "SELECT COUNT(*) FROM \`$PROD_DB_NAME\`.\`$t\`")
  b=$(MYSQL -N -e "SELECT COUNT(*) FROM \`$DEV_DB_NAME\`.\`$t\`")
  printf '%-28s %10s %10s %s\n' "$t" "$a" "$b" "$([ "$a" = "$b" ] && echo OK || echo '차이(복제 중 운영에 쓰기 발생 가능)')"
done
echo "완료"

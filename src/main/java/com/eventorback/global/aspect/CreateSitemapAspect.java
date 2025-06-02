package com.eventorback.global.aspect;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eventorback.post.domain.dto.response.CreatePostResponse;

@Aspect
@Component
public class CreateSitemapAspect {

	@Value("${upload.domainUrl}")
	private String domainUrl;

	@Value("${upload.path}")
	private String uploadPath;

	@AfterReturning(value = "execution(* com.eventorback.post.service.impl.PostServiceImpl.createPost(..))", returning = "result")
	public void sitemap(JoinPoint joinPoint, Object result) {
		Object[] args = joinPoint.getArgs();

		if (args.length > 0 && !(boolean)args[2] && result instanceof CreatePostResponse(Long postId)) {
			// 현재 시간 ISO 8601 형식으로
			String now = OffsetDateTime.now(ZoneId.of("Asia/Seoul"))
				.truncatedTo(ChronoUnit.SECONDS)
				.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

			String newEntry = String.format("""
				<url>
				  <loc>%s/posts/%d</loc>
				  <lastmod>%s</lastmod>
				</url>
				""", domainUrl, postId, now);

			File sitemapFile = new File(uploadPath + "/postimage/sitemap/sitemap.xml");
			if (!sitemapFile.exists())
				return;

			try {
				// sitemap.xml 로드
				String content = Files.readString(sitemapFile.toPath(), StandardCharsets.UTF_8);

				// </urlset> 태그 앞에 새 항목 삽입
				int insertIndex = content.lastIndexOf("</urlset>");
				if (insertIndex == -1)
					return;

				String updated = content.substring(0, insertIndex)
					+ newEntry + "\n"
					+ content.substring(insertIndex);

				// 파일 덮어쓰기
				Files.writeString(sitemapFile.toPath(), updated, StandardCharsets.UTF_8);

			} catch (IOException e) {
				e.printStackTrace(); // 또는 로그 처리
			}
		}
	}

}



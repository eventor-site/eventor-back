package com.eventorback.oauth.config;

import java.util.Arrays;
import java.util.Map;

import com.eventorback.oauth.dto.UserProfile;

public enum OauthAttributes {
	KAKAO("kakao") {
		@Override
		public UserProfile of(Map<String, Object> attributes) {
			Map<String, Object> response = (Map<String, Object>)attributes.get("kakao_account");

			// // 생일 "MM-DD" 형식과 출생 연도를 가져오기
			// String birthdayStr = (String)response.get("birthday"); // 예: "10-05"
			// String birthyearStr = (String)response.get("birthyear"); // 예: "1999"
			//
			// // "-" 제거 후 연도와 결합하여 "YYYYMMDD" 형식으로 변환
			// String birth = birthyearStr + birthdayStr.replace("-", ""); // 예: "19991005"

			return UserProfile.builder()
				.oauthId(String.valueOf(attributes.get("id")))
				.oauthType("kakao")
				.email((String)response.get("email"))
				// .name((String)response.get("name"))
				// .birth(birth)
				// .gender((String)response.get("gender"))
				// .phone((String)response.get("phone_number"))
				.build();
		}
	},
	NAVER("naver") {
		@Override
		public UserProfile of(Map<String, Object> attributes) {
			Map<String, Object> response = (Map<String, Object>)attributes.get("response");

			// // 생일 "MM-DD" 형식과 출생 연도를 가져오기
			// String birthdayStr = (String)response.get("birthday"); // 예: "10-05"
			// String birthyearStr = (String)response.get("birthyear"); // 예: "1999"
			//
			// // "-" 제거 후 연도와 결합하여 "YYYYMMDD" 형식으로 변환
			// String birth = birthyearStr + birthdayStr.replace("-", ""); // 예: "19991005"

			return UserProfile.builder()
				.oauthId((String)response.get("id"))
				.oauthType("naver")
				.email((String)response.get("email"))
				// .name((String)response.get("name"))
				// .birth(birth)
				// .gender((String)response.get("gender"))
				// .phone((String)response.get("mobile"))
				.build();
		}
	},
	GOOGLE("google") {
		@Override
		public UserProfile of(Map<String, Object> attributes) {
			return UserProfile.builder()
				.oauthId(String.valueOf(attributes.get("sub")))
				.oauthType("google")
				.email((String)attributes.get("email"))
				// .name((String)attributes.get("name"))
				.build();
		}
	},
	GITHUB("github") {
		@Override
		public UserProfile of(Map<String, Object> attributes) {
			return UserProfile.builder()
				.oauthId(String.valueOf(attributes.get("id")))
				.oauthType("github")
				.email((String)attributes.get("email"))
				.name((String)attributes.get("name"))
				.build();
		}
	};

	private final String providerName;

	OauthAttributes(String name) {
		this.providerName = name;
	}

	public static UserProfile extract(String providerName, Map<String, Object> attributes) {
		return Arrays.stream(values())
			.filter(provider -> providerName.equals(provider.providerName))
			.findFirst()
			.orElseThrow(IllegalArgumentException::new)
			.of(attributes);
	}

	public abstract UserProfile of(Map<String, Object> attributes);
}

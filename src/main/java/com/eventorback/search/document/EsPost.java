package com.eventorback.search.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

import com.eventorback.image.domain.entity.Image;
import com.eventorback.post.domain.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "post", writeTypeHint = WriteTypeHint.FALSE)
public class EsPost {

	@Id
	@Field(type = FieldType.Keyword)
	private Long postId;

	@Field(type = FieldType.Keyword)
	private String categoryName;

	@Field(type = FieldType.Keyword)
	private String statusName;

	@Field(type = FieldType.Keyword)
	private String writer;

	@Field(type = FieldType.Keyword)
	private String writerGrade;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String title;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String content;

	@Field(type = FieldType.Integer)
	private Long recommendationCount;

	@Field(type = FieldType.Integer)
	private Long viewCount;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS||epoch_millis")
	private LocalDateTime createdAt;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String productName;

	@Field(type = FieldType.Keyword)
	private String shoppingMall;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS||epoch_millis")
	private LocalDateTime startTime;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS||epoch_millis")
	private LocalDateTime endTime;

	@Field(type = FieldType.Keyword)
	private String endType;

	@Field(type = FieldType.Keyword)
	private String imageUrl;

	@Field(type = FieldType.Keyword)
	private String imageType;

	public static EsPost fromEntity(Post post, Image image) {
		String imageUrl = image != null ? image.getUrl() : null;
		String imageType = image != null ? image.getType() : null;

		EsPostBuilder esPostBuilder = EsPost.builder();
		if (post.getEvent() != null) {
			esPostBuilder.startTime(post.getEvent().getStartTime());
			esPostBuilder.endTime(post.getEvent().getEndTime());
			esPostBuilder.endType(post.getEvent().getEndType());
		}

		if (post.getHotDeal() != null) {
			esPostBuilder.productName(post.getHotDeal().getProductName());
			esPostBuilder.shoppingMall(post.getHotDeal().getShoppingMall());
		}

		return esPostBuilder
			.postId(post.getPostId())
			.categoryName(post.getCategory().getName())
			.statusName(post.getStatus().getName())
			.writer(post.getWriter())
			.writerGrade(post.getWriterGrade())
			.title(post.getTitle())
			.content(post.getContent())
			.recommendationCount(post.getRecommendationCount())
			.viewCount(post.getViewCount())
			.createdAt(post.getCreatedAt())
			.imageUrl(imageUrl)
			.imageType(imageType)
			.build();
	}

	public void increaseViewCount() {
		this.viewCount++;
	}

	public void updatePostStatus(String statusName) {
		this.statusName = statusName;
	}

	public void recommendPost() {
		this.recommendationCount++;
	}

	public void disrecommendPost() {
		this.recommendationCount--;
	}

	public void updateImage(Image image) {
		this.imageUrl = image != null ? image.getUrl() : null;
		this.imageType = image != null ? image.getType() : null;
	}

	public void finishEvent() {
		this.endTime = LocalDateTime.now();
	}

}

package com.eventorback.hotdealpost.domain.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.entity.Post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "hotdeal_posts")
public class HotDealPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hotdeal_post_id")
	private Long hotDealPostId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id")
	@OnDelete(action = OnDeleteAction.CASCADE) // DB 에서 자동 삭제 처리
	private Post post;

	@Column(name = "link")
	private String link;

	@Column(name = "shopping_mall")
	private String shoppingMall;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "price")
	private Long price;

	@Builder
	public HotDealPost(Post post, String link, String shoppingMall, String productName, Long price) {
		this.post = post;
		this.link = link;
		this.shoppingMall = shoppingMall;
		this.productName = productName;
		this.price = price;
	}

	public static HotDealPost toEntity(Post post, CreatePostRequest request) {
		return HotDealPost.builder()
			.post(post)
			.link(request.link())
			.shoppingMall(request.shoppingMall())
			.productName(request.productName())
			.price(request.price())
			.build();
	}

	public void update(UpdatePostRequest request) {
		this.link = request.link();
		this.shoppingMall = request.shoppingMall();
		this.productName = request.productName();
		this.price = request.price();
	}
}

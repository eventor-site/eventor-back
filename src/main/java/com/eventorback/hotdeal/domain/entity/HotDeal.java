package com.eventorback.hotdeal.domain.entity;

import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "hotdeals")
public class HotDeal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hotdeal_id")
	private Long hotDealPostId;

	@Column(name = "link", length = 1000)
	private String link;

	@Column(name = "shopping_mall")
	private String shoppingMall;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "price")
	private Long price;

	@Builder
	public HotDeal(String link, String shoppingMall, String productName, Long price) {
		this.link = link;
		this.shoppingMall = shoppingMall;
		this.productName = productName;
		this.price = price;
	}

	public static HotDeal toEntity(CreatePostRequest request) {
		return HotDeal.builder()
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

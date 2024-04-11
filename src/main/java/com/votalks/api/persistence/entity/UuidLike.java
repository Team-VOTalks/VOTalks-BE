package com.votalks.api.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tbl_uuid_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UuidLike {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uuid")
	private Uuid uuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "like_id")
	private Like like;

	@Enumerated(EnumType.STRING)
	@Column(name = "like_type", nullable = false)
	private LikeType likeType;

	private UuidLike(Uuid uuid, Like like, LikeType likeType) {
		this.uuid = uuid;
		this.like = like;
		this.likeType = likeType;
	}

	public static UuidLike create(Uuid uuid, Like like, LikeType likeType) {
		if (likeType.isLike()) {
			like.pressLike();
		}

		if (likeType.isDislike()) {
			like.pressDisLike();
		}

		return new UuidLike(uuid, like, likeType);
	}

	public void likeType() {
		this.likeType = LikeType.LIKE;
	}

	public void dislikeType() {
		this.likeType = LikeType.DISLIKE;
	}
}

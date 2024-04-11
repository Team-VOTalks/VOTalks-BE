package com.votalks.api.persistence.entity;

import static com.votalks.global.common.util.GlobalConstant.*;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tbl_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "like_count", nullable = false)
	@ColumnDefault("0")
	private int likeCount;

	@Column(name = "dislike_count", nullable = false)
	@ColumnDefault("0")
	private int dislikeCount;

	private Like(int likeCount, int dislikeCount) {
		this.likeCount = likeCount;
		this.dislikeCount = dislikeCount;
	}

	public void pressLike() {
		this.likeCount++;
	}

	public void pressDisLike() {
		this.dislikeCount++;
	}

	public void cancelLike() {
		this.likeCount--;
	}

	public void cancelDislike() {
		this.dislikeCount--;
	}

	public static Like create() {
		return new Like(INITIAL_NUMBER, INITIAL_NUMBER);
	}
}

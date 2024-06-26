package com.votalks.api.persistence.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.votalks.api.dto.comment.CommentCreateDto;
import com.votalks.api.dto.comment.CommentReadDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tbl_comment", indexes = {
	@Index(name = "idx_comment_created_at", columnList = "created_at DESC"),
	@Index(name = "idx_comment_popular_score", columnList = "popular_score DESC"),
	@Index(name = "idx_comment_total_reply_count", columnList = "total_reply_count DESC")
})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "content", length = 50, nullable = false)
	private String content;

	@Column(name = "user_number")
	@ColumnDefault("0")
	private int userNumber;

	@Column(name = "total_reply_count", nullable = false)
	@ColumnDefault("0")
	private int totalReplyCount;

	@Column(name = "popular_score", nullable = false)
	@ColumnDefault("0")
	private int popularScore;

	@CreatedDate
	@Column(name = "create_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vote_id")
	private Vote vote;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uuid")
	private Uuid uuid;

	@OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "like_id")
	private Like like;

	@Builder
	private Comment(
		String content,
		int userNumber,
		Vote vote,
		Uuid uuid,
		Like like
	) {
		this.content = content;
		this.userNumber = userNumber;
		this.vote = vote;
		this.uuid = uuid;
		this.like = like;
	}

	public static Comment create(
		CommentCreateDto dto,
		Uuid uuid,
		Vote vote,
		Like like,
		int userNumber
	) {
		return Comment.builder()
			.content(dto.content())
			.uuid(uuid)
			.like(like)
			.userNumber(userNumber)
			.vote(vote)
			.build();
	}

	public static CommentReadDto toReadDto(Comment comment, String likeType, int totalReplyCount) {
		return CommentReadDto.builder()
			.commentId(comment.getId())
			.userIndex(comment.getUserNumber())
			.content(comment.getContent())
			.likeCount(comment.getLike().getLikeCount())
			.createAt(comment.getCreatedAt())
			.dislikeCount(comment.getLike().getDislikeCount())
			.likeType(likeType)
			.totalReplyCount(totalReplyCount)
			.build();
	}

	public void plus() {
		this.totalReplyCount++;
	}

	public void judge(int score) {
		this.popularScore = score;
	}
}

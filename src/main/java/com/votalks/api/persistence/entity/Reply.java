package com.votalks.api.persistence.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.votalks.api.dto.reply.ReplyCreateDto;
import com.votalks.api.dto.reply.ReplyReadDto;

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
@Table(name = "tbl_reply", indexes = {
	@Index(name = "idx_reply_created_at", columnList = "created_at DESC")
})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "content", length = 50, nullable = false)
	private String content;

	@Column(name = "user_number")
	@ColumnDefault("0")
	private int userNumber;

	@CreatedDate
	@Column(name = "create_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id")
	private Comment comment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uuid")
	private Uuid uuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vote_id")
	private Vote vote;

	@OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "like_id")
	private Like like;

	@Builder
	private Reply(
		String content,
		int userNumber,
		Comment comment,
		Vote vote,
		Uuid uuid,
		Like like
	) {
		this.content = content;
		this.userNumber = userNumber;
		this.comment = comment;
		this.vote = vote;
		this.uuid = uuid;
		this.like = like;
	}

	public static Reply create(
		ReplyCreateDto dto,
		Uuid uuid,
		Vote vote,
		Like like,
		Comment comment,
		int userNumber
	) {
		return Reply.builder()
			.content(dto.content())
			.uuid(uuid)
			.like(like)
			.comment(comment)
			.userNumber(userNumber)
			.vote(vote)
			.build();
	}

	public static ReplyReadDto toReplyReadDto(Reply reply, String likeType) {
		return ReplyReadDto.builder()
			.replyId(reply.getId())
			.index(reply.getUserNumber())
			.content(reply.getContent())
			.likeCount(reply.getLike().getLikeCount())
			.createAt(reply.getCreatedAt())
			.dislikeCount(reply.getLike().getDislikeCount())
			.likeType(likeType)
			.build();
	}
}

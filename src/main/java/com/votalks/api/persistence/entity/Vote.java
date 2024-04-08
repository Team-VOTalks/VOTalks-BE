package com.votalks.api.persistence.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.dto.vote.VoteOptionWithCountDto;
import com.votalks.api.dto.vote.VoteReadDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tbl_vote")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", length = 100, nullable = false)
	private String title;

	@Column(name = "description", length = 300)
	private String description;

	@CreatedDate
	@Column(name = "create_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "select_count", nullable = false)
	private int selectCount;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uuid")
	private Uuid uuid;

	@Builder
	private Vote(
		String title,
		String description,
		LocalDateTime createdAt,
		int selectCount,
		Category category,
		Uuid uuid
	) {
		this.title = title;
		this.description = description;
		this.createdAt = createdAt;
		this.selectCount = selectCount;
		this.category = category;
		this.uuid = uuid;
	}

	public static Vote create(VoteCreateDto dto, Uuid uuid) {
		return Vote.builder()
			.title(dto.title())
			.description(dto.description())
			.selectCount(dto.selectCount())
			.category(Category.valueOf(dto.category()))
			.uuid(uuid)
			.build();
	}

	public static VoteReadDto toVoteReadDto(
		Vote vote,
		int totalVoteCount,
		List<VoteOptionWithCountDto> voteOptionWithCounts,
		int totalCommentCount
	) {
		return VoteReadDto.builder()
			.voteId(vote.id)
			.title(vote.title)
			.category(vote.category.getName())
			.createAt(vote.createdAt)
			.totalVoteCount(totalVoteCount)
			.description(vote.description)
			.voteOptionWithCounts(voteOptionWithCounts)
			.totalCommentCount(totalCommentCount)
			.build();
	}
}

package com.votalks.api.persistence.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.dto.vote.VoteReadDto;
import com.votalks.api.dto.voteOption.VoteOptionReadDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tbl_vote", indexes = {
	@Index(name = "idx_category_create_at", columnList = "category, create_at DESC")
})
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
		Category category,
		Uuid uuid
	) {
		this.title = title;
		this.description = description;
		this.category = category;
		this.uuid = uuid;
	}

	public static Vote create(VoteCreateDto dto, Uuid uuid) {
		return Vote.builder()
			.title(dto.title())
			.description(dto.description())
			.category(Category.fromValue(dto.category()))
			.uuid(uuid)
			.build();
	}

	public VoteReadDto toVoteReadDto(
		int totalVoteCount,
		List<VoteOptionReadDto> voteOption,
		int totalCommentCount
	) {
		return VoteReadDto.builder()
			.voteId(this.id)
			.title(this.title)
			.category(this.category.getName())
			.createAt(this.createdAt)
			.totalVoteCount(totalVoteCount)
			.description(this.description)
			.voteOption(voteOption)
			.totalCommentCount(totalCommentCount)
			.build();
	}
}

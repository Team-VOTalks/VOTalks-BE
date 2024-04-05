package com.votalks.api.persistence.entity;

import java.time.LocalDateTime;

import com.votalks.api.dto.vote.VoteCreateDto;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "vote")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", length = 50, nullable = false)
	private String title;

	@Column(name = "description", length = 80)
	private String description;

	@Column(name = "create_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "select_count", nullable = false)
	private int selectCount;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uuid")
	private Uuid uuid;

	@PrePersist
	public void prePersist() {
		if (this.selectCount == 0) {
			this.selectCount = 1;
		}
	}

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

	public static Vote create(VoteCreateDto dto, LocalDateTime localDateTime, Uuid uuid) {
		return new Vote(
			dto.title(),
			dto.description(),
			localDateTime,
			dto.selectCount(),
			Category.valueOf(dto.category()),
			uuid);
	}
}

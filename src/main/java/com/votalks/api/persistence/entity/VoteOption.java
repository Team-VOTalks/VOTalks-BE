package com.votalks.api.persistence.entity;

import com.votalks.api.dto.voteOption.VoteOptionReadDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "tbl_vote_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteOption {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "content", nullable = false, length = 50)
	private String content;

	@Column(name = "vote_count", nullable = false)
	private int voteCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vote_id")
	private Vote vote;

	private VoteOption(String content, Vote vote) {
		this.content = content;
		this.vote = vote;
	}

	public static VoteOption create(String optionValue, Vote vote) {
		return new VoteOption(optionValue, vote);
	}

	public void select() {
		this.voteCount++;
	}

	public VoteOptionReadDto read(Long id) {
		boolean check = id.equals(this.getId());
		return VoteOptionReadDto.builder()
			.id(this.id)
			.title(this.content)
			.count(this.voteCount)
			.isCheck(check)
			.build();
	}

	public VoteOptionReadDto read(boolean isSelected) {
		return VoteOptionReadDto.builder()
			.id(this.id)
			.title(this.content)
			.count(this.voteCount)
			.isCheck(isSelected)
			.build();
	}
}



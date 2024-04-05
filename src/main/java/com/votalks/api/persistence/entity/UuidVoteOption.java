package com.votalks.api.persistence.entity;

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
@Table(name = "uuid_vote_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UuidVoteOption {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uuid")
	private Uuid uuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vote_option_id")
	private VoteOption voteOption;

	private UuidVoteOption(Uuid uuid, VoteOption voteOption) {
		this.uuid = uuid;
		this.voteOption = voteOption;
	}

	public static UuidVoteOption generated(Uuid uuid, VoteOption voteOption) {
		return new UuidVoteOption(uuid, voteOption);
	}
}

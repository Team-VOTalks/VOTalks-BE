package com.votalks.api.persistence;

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
<<<<<<< HEAD
@Table(name = "uuid_vote_option")
=======
@Table(name = "uuid_vote-option")
>>>>>>> 64f53192a7ca5b881f668fa6734aa3c3a6b386a8
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
}

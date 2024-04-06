package com.votalks.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.dto.vote.VoteTakeDto;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidVoteOption;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.entity.VoteOption;
import com.votalks.api.persistence.repository.UuidRepository;
import com.votalks.api.persistence.repository.UuidVoteOptionRepository;
import com.votalks.api.persistence.repository.VoteOptionRepository;
import com.votalks.api.persistence.repository.VoteRepository;
import com.votalks.global.error.exception.BadRequestException;
import com.votalks.global.error.exception.NotFoundException;
import com.votalks.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {
	private final VoteRepository voteRepository;
	private final VoteOptionRepository voteOptionRepository;
	private final UuidVoteOptionRepository uuidVoteOptionRepository;
	private final UuidRepository uuidRepository;

	public void create(VoteCreateDto dto) {
		final Uuid uuid = getOrCreate(dto.uuid());
		final Vote vote = Vote.create(dto, LocalDateTime.now(), uuid);
		final List<VoteOption> voteOptions = dto.voteOptions().stream()
			.map(optionValue -> VoteOption.create(optionValue, vote))
			.toList();

		voteRepository.save(vote);
		voteOptionRepository.saveAll(voteOptions);
		//test
	}

	public void select(VoteTakeDto dto, Long id) {
		final Uuid uuid = getOrCreate(dto.uuid());
		final Vote vote = voteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));
		final VoteOption voteOption = voteOptionRepository.findById(dto.voteOptionId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_OPTION_FOUND));
		final int limit = uuidVoteOptionRepository.countByUuidAndVoteOption(uuid, voteOption);

		validateBelogToVote(voteOption, vote);
		validateAlreadyVoted(uuid, voteOption);
		validateLimit(vote, limit);
		voteOption.select();

		final UuidVoteOption uuidVoteOption = UuidVoteOption.create(uuid, voteOption);
		uuidVoteOptionRepository.save(uuidVoteOption);
	}

	private void validateBelogToVote(VoteOption voteOption, Vote vote) {
		if (!voteOption.getVote().equals(vote)) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}
	}

	private void validateLimit(Vote vote, int limit) {
		if (vote.getSelectCount() == limit) {
			throw new BadRequestException(ErrorCode.VOTE_LIMIT_REACHED);
		}
	}

	private void validateAlreadyVoted(Uuid uuid, VoteOption voteOption) {
		if (uuidVoteOptionRepository.existsByUuidAndVoteOption(uuid, voteOption)) {
			throw new BadRequestException(ErrorCode.FAIL_ALREADY_VOTE);
		}
	}

	private Uuid getOrCreate(String uuid) {
		if (uuid == null) {
			return uuidRepository.save(Uuid.create(UUID.randomUUID()));
		}
		return uuidRepository.findById(Uuid.fromString(uuid))
			.orElseGet(() -> uuidRepository.save(Uuid.create(Uuid.fromString(uuid))));
	}
}

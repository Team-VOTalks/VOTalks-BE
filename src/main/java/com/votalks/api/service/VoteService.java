package com.votalks.api.service;

import static java.util.function.Predicate.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.dto.vote.VoteReadDto;
import com.votalks.api.dto.vote.VoteTakeDto;
import com.votalks.api.persistence.entity.Category;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidVoteOption;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.entity.VoteOption;
import com.votalks.api.persistence.repository.CommentRepository;
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
	private final CommentRepository commentRepository;

	public void create(VoteCreateDto dto) {
		final Uuid uuid = getOrCreate(dto.uuid());
		final Vote vote = Vote.create(dto, uuid);
		final List<VoteOption> voteOptions = dto.voteOptions().stream()
			.map(optionValue -> VoteOption.create(optionValue, vote))
			.toList();

		voteRepository.save(vote);
		voteOptionRepository.saveAll(voteOptions);
	}

	public void select(VoteTakeDto dto, Long id) {
		final Uuid uuid = getOrCreate(dto.uuid());
		final Vote vote = voteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));
		final VoteOption voteOption = voteOptionRepository.findById(dto.voteOptionId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_OPTION_FOUND));
		final int limit = uuidVoteOptionRepository.countByUuidAndVoteOption(uuid, voteOption);

		validateBelongToVote(voteOption, vote);
		validateAlreadyVoted(uuid, voteOption);
		validateLimit(vote, limit);
		voteOption.select();

		final UuidVoteOption uuidVoteOption = UuidVoteOption.create(uuid, voteOption);
		uuidVoteOptionRepository.save(uuidVoteOption);
	}

	@Transactional(readOnly = true)
	public VoteReadDto read(Long id) {
		final Vote vote = voteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));

		Map<String, Integer> voteOptionsWithCounts = voteOptionRepository.findAllByVote(vote)
			.stream()
			.collect(Collectors.toMap(
				VoteOption::getContent,
				VoteOption::getVoteCount
			));

		int totalVoteCount = voteOptionsWithCounts
			.values()
			.stream()
			.mapToInt(Integer::intValue)
			.sum();

		int totalCommentCount = commentRepository.countByVote(vote);

		return Vote.read(vote, totalVoteCount, voteOptionsWithCounts, totalCommentCount);
	}

	@Transactional(readOnly = true)
	public Page<VoteReadDto> readAll(int page, int size, String category) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<Vote> votes = getPagedVotesFilteredByCategory(category, pageable);

		return votes.map(vote -> {
			Map<String, Integer> voteOptionsWithCounts = voteOptionRepository.findAllByVote(vote)
				.stream()
				.collect(Collectors.toMap(
					VoteOption::getContent,
					VoteOption::getVoteCount
				));

			int totalVoteCount = voteOptionsWithCounts.values().stream().mapToInt(Integer::intValue).sum();
			int totalCommentCount = commentRepository.countByVote(vote);

			return Vote.read(vote, totalVoteCount, voteOptionsWithCounts, totalCommentCount);
		});
	}

	private Page<Vote> getPagedVotesFilteredByCategory(String category, Pageable pageable) {
		return Optional.ofNullable(category)
			.filter(not(String::isBlank))
			.filter(Category::contains)
			.map(c -> voteRepository.findAllByCategory(Category.valueOf(category), pageable))
			.orElseGet(() -> voteRepository.findAll(pageable));
	}

	private void validateBelongToVote(VoteOption voteOption, Vote vote) {
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

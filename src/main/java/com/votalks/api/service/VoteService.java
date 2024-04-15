package com.votalks.api.service;

import static java.util.function.Predicate.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votalks.api.dto.PageInfo;
import com.votalks.api.dto.PageResponse;
import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.dto.vote.VoteReadDto;
import com.votalks.api.dto.vote.VoteTakeDto;
import com.votalks.api.dto.voteOption.VoteOptionReadDto;
import com.votalks.api.persistence.entity.Category;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidVoteOption;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.entity.VoteOption;
import com.votalks.api.persistence.repository.CommentRepository;
import com.votalks.api.persistence.repository.UuidVoteOptionRepository;
import com.votalks.api.persistence.repository.VoteOptionRepository;
import com.votalks.api.persistence.repository.VoteRepository;
import com.votalks.global.error.exception.BadRequestException;
import com.votalks.global.error.exception.NotFoundException;
import com.votalks.global.error.model.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {
	private final VoteRepository voteRepository;
	private final VoteOptionRepository voteOptionRepository;
	private final UuidVoteOptionRepository uuidVoteOptionRepository;
	private final CommentRepository commentRepository;
	private final UuidService uuidService;

	public void create(
		VoteCreateDto dto,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		final Uuid uuid = uuidService.getOrCreate(request, response);
		final Vote vote = Vote.create(dto, uuid);
		final List<VoteOption> voteOptions = dto.voteOptions()
			.stream()
			.map(optionValue -> VoteOption.create(optionValue, vote))
			.toList();
		voteRepository.save(vote);
		voteOptionRepository.saveAll(voteOptions);
		uuidService.setHttpHeaders(response, uuid);
	}

	public List<VoteOptionReadDto> select(
		VoteTakeDto dto,
		Long id,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		final Uuid uuid = uuidService.getOrCreate(request, response);
		final VoteOption voteOption = voteOptionRepository.findByIdAndVote_Id(dto.voteOptionId(), id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_OPTION_FOUND));

		validateAlreadyVoted(uuid, voteOption);
		voteOption.select();

		final List<VoteOptionReadDto> voteOptionReadDto = voteOptionRepository.findAllByVote_Id(id)
			.stream()
			.map(vo -> vo.read(dto.voteOptionId()))
			.toList();
		final UuidVoteOption uuidVoteOption = UuidVoteOption.create(uuid, voteOption);

		uuidVoteOptionRepository.save(uuidVoteOption);
		uuidService.setHttpHeaders(response, uuid);

		return voteOptionReadDto;
	}

	@Transactional(readOnly = true)
	public VoteReadDto read(
		Long id,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		final Uuid uuid = uuidService.getOrCreate(request, response);
		final Vote vote = voteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));
		uuidService.setHttpHeaders(response, uuid);

		return getReadDto(vote, uuid);
	}

	@Transactional(readOnly = true)
	public PageResponse<VoteReadDto> readAll(
		int page,
		int size,
		String category,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		final Uuid uuid = uuidService.getOrCreate(request, response);
		final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		final Page<Vote> votes = getPagedVotesByCategory(category, pageable);
		final PageInfo pageInfo = new PageInfo(
			votes.getNumber(),
			votes.getTotalPages(),
			votes.isLast()
		);

		uuidService.setHttpHeaders(response, uuid);

		return new PageResponse<>(votes.map(vote -> getReadDto(vote, uuid)).stream().toList(), pageInfo);
	}

	private VoteReadDto getReadDto(Vote vote, Uuid uuid) {
		final List<VoteOption> selectedVoteOptions = uuidVoteOptionRepository.findAllVoteOptionsByUuidAndVoteId(uuid,
			vote);
		final Set<Long> selectedVoteOptionIds = selectedVoteOptions.stream()
			.map(VoteOption::getId)
			.collect(Collectors.toSet());
		final List<VoteOptionReadDto> voteOptionsWithCount = voteOptionRepository.findAllByVote(vote)
			.stream()
			.map(vo -> {
				boolean isSelected = selectedVoteOptionIds.contains(vo.getId());
				return vo.read(isSelected);
			})
			.toList();
		final int totalVoteCount = voteOptionsWithCount.stream()
			.mapToInt(VoteOptionReadDto::count)
			.sum();
		final int totalCommentCount = commentRepository.countByVote(vote);

		return vote.toVoteReadDto(totalVoteCount, voteOptionsWithCount, totalCommentCount);
	}

	private Page<Vote> getPagedVotesByCategory(String category, Pageable pageable) {
		return Optional.ofNullable(category)
			.filter(not(String::isBlank))
			.filter(Category::contains)
			.map(c -> voteRepository.findAllByCategory(Category.valueOf(category), pageable))
			.orElseGet(() -> voteRepository.findAll(pageable));
	}

	private void validateAlreadyVoted(Uuid uuid, VoteOption voteOption) {
		if (uuidVoteOptionRepository.existsByUuidAndVoteOption(uuid, voteOption)) {
			throw new BadRequestException(ErrorCode.FAIL_ALREADY_VOTE);
		}
	}
}

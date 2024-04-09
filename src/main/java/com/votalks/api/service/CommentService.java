package com.votalks.api.service;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.votalks.api.dto.comment.CommentCreateDto;
import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.repository.CommentRepository;
import com.votalks.api.persistence.repository.UuidRepository;
import com.votalks.api.persistence.repository.VoteRepository;
import com.votalks.global.error.exception.NotFoundException;
import com.votalks.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final VoteRepository voteRepository;
	private final UuidRepository uuidRepository;

	public void create(CommentCreateDto dto, Long id) {
		final Vote vote = voteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));
		final Uuid uuid = getOrCreate(dto.uuid());
		final int userNumber = determineUserNumber(vote, uuid);
		commentRepository.save(Comment.create(dto, uuid, vote, userNumber));
	}

	private int determineUserNumber(Vote vote, Uuid uuid) {
		if (vote.getUuid().equals(uuid)) {
			return 0;
		}
		if (commentRepository.existsByVoteAndUuid(vote, uuid)) {
			return commentRepository.findUserNumberByUuid(uuid);
		}
		return commentRepository.findMaxUserNumberByVote(vote).orElse(0) + 1;
	}

	private Uuid getOrCreate(String uuid) {
		if (StringUtils.isEmpty(uuid) || uuid.length() != 32) {
			return uuidRepository.save(Uuid.create(UUID.randomUUID()));
		}
		return uuidRepository.findById(Uuid.fromString(uuid))
			.orElseGet(() -> uuidRepository.save(Uuid.create(UUID.randomUUID())));
	}
}

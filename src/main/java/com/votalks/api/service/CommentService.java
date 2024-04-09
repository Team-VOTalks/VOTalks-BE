package com.votalks.api.service;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.votalks.api.dto.comment.CommentCreateDto;
import com.votalks.api.dto.comment.CommentReadDto;
import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Like;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.repository.CommentRepository;
import com.votalks.api.persistence.repository.LikeRepository;
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
	private final LikeRepository likeRepository;

	public void create(CommentCreateDto dto, Long id) {
		final Vote vote = voteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));
		final Uuid uuid = getOrCreate(dto.uuid());
		final int userNumber = determineUserNumber(vote, uuid);
		final Like like = Like.create();
		likeRepository.save(like);
		commentRepository.save(Comment.create(dto, uuid, vote, like, userNumber));
	}

	public Page<CommentReadDto> read(Long id, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		final Vote vote = voteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));
		return commentRepository.findAllByVote(vote, pageable)
			.map(
				comment -> Comment.toCommentReadDto(
					comment,
					comment.getLike().getLikeCount(),
					comment.getLike().getDislikeCount(),
					comment.getReply().size()
				));
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

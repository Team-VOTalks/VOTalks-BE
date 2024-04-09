package com.votalks.api.service;

import static com.votalks.global.common.util.GlobalConstant.*;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votalks.api.dto.comment.CommentCreateDto;
import com.votalks.api.dto.comment.CommentReadDto;
import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Like;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidLike;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.repository.CommentRepository;
import com.votalks.api.persistence.repository.LikeRepository;
import com.votalks.api.persistence.repository.UuidLikeRepository;
import com.votalks.api.persistence.repository.UuidRepository;
import com.votalks.api.persistence.repository.VoteRepository;
import com.votalks.global.error.exception.BadRequestException;
import com.votalks.global.error.exception.NotFoundException;
import com.votalks.global.error.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final VoteRepository voteRepository;
	private final UuidRepository uuidRepository;
	private final LikeRepository likeRepository;
	private final UuidLikeRepository uuidLikeRepository;

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

	public void like(Long voteId, Long commentId, String userUuid) {
		final Vote vote = voteRepository.findById(voteId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));
		final Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_COMMENT_FOUND));
		validateBelongToVote(comment, vote);
		final Uuid uuid = getOrCreate(userUuid);
		final Like like = comment.getLike();

		if (isAlreadyPress(uuid, like)) {
			cancelLike(uuid, like);
			return;
		}

		final UuidLike uuidLike = UuidLike.create(uuid, like);
		like.pressLike();
		uuidLikeRepository.save(uuidLike);
	}

	private boolean isAlreadyPress(Uuid uuid, Like like) {
		return uuidLikeRepository.existsByUuidAndLike(uuid, like);
	}

	private void cancelLike(Uuid uuid, Like like) {
		like.cancelLike();
		uuidLikeRepository.deleteByUuidAndLike(uuid, like);
	}

	private static void validateBelongToVote(Comment comment, Vote vote) {
		if (!comment.getVote().equals(vote)) {
			throw new BadRequestException(ErrorCode.BAD_REQUEST);
		}
	}

	private int determineUserNumber(Vote vote, Uuid uuid) {
		if (vote.getUuid().equals(uuid)) {
			return AUTHOR;
		}
		if (commentRepository.existsByVoteAndUuid(vote, uuid)) {
			return commentRepository.findUserNumberByUuid(uuid);
		}
		return commentRepository.findMaxUserNumberByVote(vote).orElse(INITIAL_NUMBER) + NEXT_COMMENTER;
	}

	private Uuid getOrCreate(String uuid) {
		if (StringUtils.isEmpty(uuid) || uuid.length() != UUID_LENGTH_STANDARD) {
			return uuidRepository.save(Uuid.create(UUID.randomUUID()));
		}
		return uuidRepository.findById(Uuid.fromString(uuid))
			.orElseGet(() -> uuidRepository.save(Uuid.create(UUID.randomUUID())));
	}
}

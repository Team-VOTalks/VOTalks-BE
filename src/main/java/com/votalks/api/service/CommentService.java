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
import com.votalks.api.dto.comment.CommentLikeDto;
import com.votalks.api.dto.comment.CommentReadDto;
import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Like;
import com.votalks.api.persistence.entity.LikeType;
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
		final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		final Vote vote = voteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));

		return commentRepository.findAllByVote(vote, pageable)
			.map(Comment::toCommentReadDto);
	}

	public void like(Long voteId, Long commentId, CommentLikeDto dto) {
		final Vote vote = voteRepository.findById(voteId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));
		final Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_COMMENT_FOUND));
		validateBelongToVote(comment, vote);
		final Uuid uuid = getOrCreate(dto.uuid());
		final Like like = comment.getLike();
		final UuidLike uuidLike = uuidLikeRepository.findByUuidAndLike(uuid, like)
			.orElse(UuidLike.create(uuid, like));

		if (isAlreadyPress(uuid, like)) {
			validateCancelLike(like, dto.likeType(), uuidLike);
			validateChangeLike(like, dto.likeType(), uuidLike);
			return;
		}

		validatePressLike(dto, like, uuidLike);
		validatePressDislike(dto, like, uuidLike);
		uuidLikeRepository.save(uuidLike);
	}

	private void validatePressDislike(CommentLikeDto dto, Like like, UuidLike uuidLike) {
		if (dto.likeType().equals(DISLIKE)) {
			like.pressDisLike();
			uuidLike.dislikeType();
		}
	}

	private void validatePressLike(CommentLikeDto dto, Like like, UuidLike uuidLike) {
		if (dto.likeType().equals(LIKE)) {
			like.pressLike();
			uuidLike.likeType();
		}
	}

	private boolean isAlreadyPress(Uuid uuid, Like like) {
		return uuidLikeRepository.existsByUuidAndLike(uuid, like);
	}

	private void validateCancelLike(Like like, String likeType, UuidLike uuidLike) {
		if (likeType.equals(LIKE) && uuidLike.getLikeType().equals(LikeType.LIKE)) {
			like.cancelLike();
			uuidLikeRepository.delete(uuidLike);
		}
		
		if (likeType.equals(DISLIKE) && uuidLike.getLikeType().equals(LikeType.DISLIKE)) {
			like.cancelDislike();
			uuidLikeRepository.delete(uuidLike);
		}
	}

	/**
	 * 이미 싫어요를 눌렀는데, 좋아요를 누르는 경우
	 * 이미 좋아요를 눌렀는데, 싫어요를 누르는 경우
	 */
	private void validateChangeLike(Like like, String likeType, UuidLike uuidLike) {
		if (uuidLike.getLikeType().equals(LikeType.DISLIKE) && likeType.equals(LIKE)) {
			like.cancelDislike();
			like.pressLike();
			uuidLike.likeType();
		}

		if (uuidLike.getLikeType().equals(LikeType.LIKE) && likeType.equals(DISLIKE)) {
			like.cancelLike();
			like.pressDisLike();
			uuidLike.dislikeType();
		}
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

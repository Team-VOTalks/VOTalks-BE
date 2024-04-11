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
		final Comment comment = Comment.create(dto, uuid, vote, like, userNumber);

		likeRepository.save(like);
		commentRepository.save(comment);
	}

	public Page<CommentReadDto> read(Long id, int page, int size) {
		final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		final Vote vote = voteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));

		return commentRepository.findAllByVote(vote, pageable)
			.map(Comment::toCommentReadDto);
	}

	public void like(Long voteId, Long commentId, CommentLikeDto dto) {
		final Comment comment = commentRepository.findByIdAndVote_Id(commentId, voteId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_COMMENT_FOUND));
		final Uuid uuid = getOrCreate(dto.uuid());
		final Like like = comment.getLike();
		final LikeType likeType = LikeType.from(dto.likeType());

		uuidLikeRepository.findByUuidAndLike(uuid, like)
			.ifPresentOrElse(
				uuidLike -> validateCancelLike(like, likeType, uuidLike),
				() -> {
					UuidLike uuidLike = UuidLike.create(uuid, like, likeType);
					uuidLikeRepository.save(uuidLike);
				});
	}

	private void validateCancelLike(Like like, LikeType likeType, UuidLike uuidLike) {
		if (likeType.isLike() && uuidLike.getLikeType().isLike()) {
			like.cancelLike();
			uuidLikeRepository.delete(uuidLike);
			return;
		}

		if (likeType.isDislike() && uuidLike.getLikeType().isDislike()) {
			like.cancelDislike();
			uuidLikeRepository.delete(uuidLike);
			return;
		}

		if (uuidLike.getLikeType().isDislike() && likeType.isLike()) {
			like.cancelDislike();
			like.pressLike();
			uuidLike.likeType();
			return;
		}

		if (uuidLike.getLikeType().isLike() && likeType.isDislike()) {
			like.cancelLike();
			like.pressDisLike();
			uuidLike.dislikeType();
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

package com.votalks.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votalks.api.dto.like.LikeCreateDto;
import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Like;
import com.votalks.api.persistence.entity.LikeType;
import com.votalks.api.persistence.entity.Reply;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidLike;
import com.votalks.api.persistence.repository.CommentRepository;
import com.votalks.api.persistence.repository.ReplyRepository;
import com.votalks.api.persistence.repository.UuidLikeRepository;
import com.votalks.global.error.exception.NotFoundException;
import com.votalks.global.error.model.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
	private final CommentRepository commentRepository;
	private final UuidLikeRepository uuidLikeRepository;
	private final UuidService uuidService;
	private final ReplyRepository replyRepository;

	public void like(
		Long voteId,
		Long commentId,
		LikeCreateDto dto,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		final Comment comment = commentRepository.findByIdAndVote_Id(commentId, voteId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_COMMENT_FOUND));
		final Uuid uuid = uuidService.getOrCreate(request, response);
		final Like like = comment.getLike();
		final LikeType likeType = LikeType.from(dto.likeType());

		uuidLikeRepository.findByUuidAndLike(uuid, like)
			.ifPresentOrElse(
				uuidLike -> {
					validateCancelLike(like, likeType, uuidLike);
					updatePopularScore(comment);
				},
				() -> {
					UuidLike uuidLike = UuidLike.create(uuid, like, likeType);
					uuidLikeRepository.save(uuidLike);
					updatePopularScore(comment);
				});

		uuidService.setHttpHeaders(response, uuid);
	}

	public void like(Long voteId,
		Long commentId,
		Long replyId,
		LikeCreateDto dto,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		final Reply reply = replyRepository.findByIdAndVote_IdAndComment_Id(replyId, voteId, commentId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_REPLY_FOUND));

		final Uuid uuid = uuidService.getOrCreate(request, response);
		final Like like = reply.getLike();
		final LikeType likeType = LikeType.from(dto.likeType());

		uuidLikeRepository.findByUuidAndLike(uuid, like)
			.ifPresentOrElse(
				uuidLike -> validateCancelLike(like, likeType, uuidLike),
				() -> {
					UuidLike uuidLike = UuidLike.create(uuid, like, likeType);
					uuidLikeRepository.save(uuidLike);
				});

		uuidService.setHttpHeaders(response, uuid);
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

	private void updatePopularScore(Comment comment) {
		Like like = comment.getLike();
		int totalLikeCount = like.getLikeCount();
		int totalDislikeCount = like.getDislikeCount();
		int popularityScore = (totalLikeCount + totalDislikeCount) + (totalLikeCount - totalDislikeCount);
		comment.judge(popularityScore);
	}
}

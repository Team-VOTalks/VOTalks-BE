package com.votalks.api.service;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votalks.api.dto.comment.CommentLikeDto;
import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Like;
import com.votalks.api.persistence.entity.LikeType;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidLike;
import com.votalks.api.persistence.repository.CommentRepository;
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

	//필요
	public HttpHeaders like(Long voteId, Long commentId, CommentLikeDto dto, HttpServletRequest request,
		HttpServletResponse response) {
		final Comment comment = commentRepository.findByIdAndVote_Id(commentId, voteId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_COMMENT_FOUND));
		final Uuid uuid = uuidService.getOrCreate(request, response);
		final Like like = comment.getLike();
		final LikeType likeType = LikeType.from(dto.likeType());

		uuidLikeRepository.findByUuidAndLike(uuid, like)
			.ifPresentOrElse(
				uuidLike -> validateCancelLike(like, likeType, uuidLike),
				() -> {
					UuidLike uuidLike = UuidLike.create(uuid, like, likeType);
					uuidLikeRepository.save(uuidLike);
				});

		return uuidService.getHttpHeaders(uuid);
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
}

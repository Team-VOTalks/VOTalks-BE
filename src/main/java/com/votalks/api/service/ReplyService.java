package com.votalks.api.service;

import static com.votalks.global.common.util.GlobalConstant.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votalks.api.dto.reply.ReplyCreateDto;
import com.votalks.api.dto.reply.ReplyReadDto;
import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Like;
import com.votalks.api.persistence.entity.LikeType;
import com.votalks.api.persistence.entity.Reply;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidLike;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.repository.CommentRepository;
import com.votalks.api.persistence.repository.LikeRepository;
import com.votalks.api.persistence.repository.ReplyRepository;
import com.votalks.api.persistence.repository.UuidLikeRepository;
import com.votalks.api.persistence.repository.VoteRepository;
import com.votalks.global.error.exception.NotFoundException;
import com.votalks.global.error.model.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {
	private final CommentRepository commentRepository;
	private final VoteRepository voteRepository;
	private final LikeRepository likeRepository;
	private final UuidService uuidService;
	private final ReplyRepository replyRepository;
	private final UuidLikeRepository uuidLikeRepository;

	public void create(
		ReplyCreateDto dto,
		Long voteId,
		Long commentId,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		final Vote vote = voteRepository.findById(voteId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));
		final Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_COMMENT_FOUND));
		final Uuid uuid = uuidService.getOrCreate(request, response);
		final int userNumber = determineUserNumber(vote, uuid);
		final Like like = Like.create();
		final Reply reply = Reply.create(dto, uuid, vote, like, comment, userNumber);

		likeRepository.save(like);
		replyRepository.save(reply);

		uuidService.setHttpHeaders(response, uuid);
	}

	public Page<ReplyReadDto> read(
		Long voteId,
		Long commentId,
		int page,
		int size,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		final Comment comment = commentRepository.findByIdAndVote_Id(commentId, voteId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_COMMENT_FOUND));
		final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		final Uuid uuid = uuidService.getOrCreate(request, response);
		final Page<Reply> replies = replyRepository.findAllByComment(comment, pageable);
		final List<UuidLike> uuidLikes = uuidLikeRepository.findByUuidAndVoteIdWithReplies(uuid, voteId);

		Map<Long, LikeType> likedCommentIdToTypeMap = uuidLikes.stream()
			.collect(Collectors.toMap(
				uuidLike -> uuidLike.getLike().getId(),  // 댓글의 ID를 가져오는 방법
				UuidLike::getLikeType,  // 좋아요의 타입
				(existing, replacement) -> existing  // 충돌 시 기존 값을 유지
			));

		return replies.map(reply -> {
			LikeType likeType = likedCommentIdToTypeMap.getOrDefault(reply.getId(), LikeType.NONE);
			return Reply.toReplyReadDto(reply, likeType.getValue());
		});
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
}

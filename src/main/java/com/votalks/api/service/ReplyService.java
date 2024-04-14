package com.votalks.api.service;

import static com.votalks.global.common.util.GlobalConstant.*;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votalks.api.dto.reply.ReplyCreateDto;
import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Like;
import com.votalks.api.persistence.entity.Reply;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.repository.CommentRepository;
import com.votalks.api.persistence.repository.LikeRepository;
import com.votalks.api.persistence.repository.ReplyRepository;
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

	public HttpHeaders create(
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

		return uuidService.getHttpHeaders(uuid);
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

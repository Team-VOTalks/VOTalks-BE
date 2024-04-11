package com.votalks.api.service;

import static com.votalks.global.common.util.GlobalConstant.*;

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
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.repository.CommentRepository;
import com.votalks.api.persistence.repository.LikeRepository;
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
	private final LikeRepository likeRepository;
	private final UuidService uuidService;

	public void create(CommentCreateDto dto, Long id) {
		final Vote vote = voteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_VOTE_FOUND));
		final Uuid uuid = uuidService.getOrCreate(dto.uuid());
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

package com.votalks.api.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
	int countByComment(Comment comment);

	// Optional<Comment> findByIdAndVote_Id(Long commentId, Long voteId);
	Page<Reply> findAllByComment(Comment comment, Pageable pageable);

	Optional<Reply> findByIdAndVote_IdAndComment_Id(Long id, Long voteId, Long commentId);
}

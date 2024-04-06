package com.votalks.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Vote;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	int countByVote(Vote vote);
}

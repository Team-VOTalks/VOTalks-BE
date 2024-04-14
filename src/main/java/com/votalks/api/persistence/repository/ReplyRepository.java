package com.votalks.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
	int countByComment(Comment comment);
}

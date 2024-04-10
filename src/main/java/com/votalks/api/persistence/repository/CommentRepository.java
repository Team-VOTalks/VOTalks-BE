package com.votalks.api.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.votalks.api.persistence.entity.Comment;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.Vote;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	int countByVote(Vote vote);

	boolean existsByVoteAndUuid(Vote vote, Uuid uuid);

	@Query("SELECT c.userNumber FROM Comment c WHERE c.uuid = :uuid")
	int findUserNumberByUuid(@Param("uuid") Uuid uuid);

	@Query("SELECT MAX(c.userNumber) FROM Comment c WHERE c.vote = :vote")
	Optional<Integer> findMaxUserNumberByVote(@Param("vote") Vote vote);

	Page<Comment> findAllByVote(Vote vote, Pageable pageable);
}

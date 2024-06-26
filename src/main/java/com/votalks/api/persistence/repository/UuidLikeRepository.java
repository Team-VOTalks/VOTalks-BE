package com.votalks.api.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.votalks.api.persistence.entity.Like;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidLike;
import com.votalks.api.persistence.entity.Vote;

public interface UuidLikeRepository extends JpaRepository<UuidLike, Long> {
	@Query("SELECT ul FROM UuidLike ul WHERE ul.uuid = :uuid AND ul.like = :like")
	Optional<UuidLike> findByUuidAndLike(@Param("uuid") Uuid uuid, @Param("like") Like like);

	/**
	 * 댓글에 있는 like와, uuidLike에 있는 like가 일치하고, 투표에 속해있는 댓글이어야 하며, uuid가 일치한 uuidLike를 가져온다.
	 */
	@Query("SELECT ul FROM UuidLike ul JOIN Comment c ON ul.like.id = c.like.id WHERE c.vote = :vote AND ul.uuid = :uuid")
	List<UuidLike> findByUuidAndVote(Uuid uuid, Vote vote);

	/**
	 * 댓글과 투표에 속해 있는 댓글에 대한 답변(reply)에서 like와 uuidLike가 일치하며, uuid가 일치하는 uuidLike를 가져온다.
	 */
	@Query("SELECT ul FROM UuidLike ul JOIN Reply r ON ul.like.id = r.comment.like.id WHERE r.vote.id = :voteId AND r.comment.vote.id = :voteId AND ul.uuid = :uuid")
	List<UuidLike> findByUuidAndVoteIdWithReplies(Uuid uuid, Long voteId);

	List<UuidLike> findByUuid(Uuid uuid);
}

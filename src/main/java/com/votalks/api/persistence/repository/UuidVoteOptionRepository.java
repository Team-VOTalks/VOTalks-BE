package com.votalks.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidVoteOption;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.entity.VoteOption;

public interface UuidVoteOptionRepository extends JpaRepository<UuidVoteOption, Long> {
	boolean existsByUuidAndVoteOption(Uuid uuid, VoteOption voteOption);

	// 특정 Vote 내에서 Uuid가 선택한 VoteOption들의 개수를 반환하는 메서드
	@Query("SELECT COUNT(uvo) FROM UuidVoteOption uvo WHERE uvo.uuid = :uuid AND uvo.voteOption.vote = :vote")
	int countByUuidAndVoteOptionVote(@Param("uuid") Uuid uuid, @Param("vote") Vote vote);
}

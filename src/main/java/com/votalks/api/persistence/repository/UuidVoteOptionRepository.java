package com.votalks.api.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidVoteOption;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.entity.VoteOption;

public interface UuidVoteOptionRepository extends JpaRepository<UuidVoteOption, Long> {
	boolean existsByUuidAndVoteOption(Uuid uuid, VoteOption voteOption);

	//voteOption에 연결된 vote와 uuid를 통해 voteOption 찾기.
	@Query("SELECT uvo.voteOption FROM UuidVoteOption uvo WHERE uvo.uuid = :uuid AND uvo.voteOption.vote = :vote")
	List<VoteOption> findAllVoteOptionsByUuidAndVoteId(@Param("uuid") Uuid uuid, @Param("vote") Vote vote);
}

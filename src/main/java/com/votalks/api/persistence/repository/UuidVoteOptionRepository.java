package com.votalks.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidVoteOption;
import com.votalks.api.persistence.entity.VoteOption;

public interface UuidVoteOptionRepository extends JpaRepository<UuidVoteOption, Long> {
	boolean existsByUuidAndVoteOption(Uuid uuid, VoteOption voteOption);

	@Query("SELECT COUNT(uvo) FROM UuidVoteOption uvo WHERE uvo.uuid = :uuid AND uvo.voteOption = :voteOption")
	int countByUuidAndVoteOption(@Param("uuid") Uuid uuid, @Param("voteOption") VoteOption voteOption);
}

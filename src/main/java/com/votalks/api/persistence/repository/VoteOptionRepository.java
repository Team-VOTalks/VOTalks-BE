package com.votalks.api.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.entity.VoteOption;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
	List<VoteOption> findAllByVote(Vote vote);
}

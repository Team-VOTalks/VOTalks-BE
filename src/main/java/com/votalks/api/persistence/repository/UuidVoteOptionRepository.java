package com.votalks.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidVoteOption;

public interface UuidVoteOptionRepository extends JpaRepository<UuidVoteOption, Long> {
	boolean existsByUuid(Uuid uuid);
}

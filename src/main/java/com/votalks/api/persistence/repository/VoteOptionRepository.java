package com.votalks.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.votalks.api.persistence.entity.VoteOption;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
}

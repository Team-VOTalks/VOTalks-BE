package com.votalks.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.votalks.api.persistence.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}

package com.votalks.api.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.votalks.api.persistence.entity.Category;
import com.votalks.api.persistence.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
	Page<Vote> findAllByCategory(Category category, Pageable pageable);
}

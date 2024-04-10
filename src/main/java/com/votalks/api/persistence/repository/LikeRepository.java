package com.votalks.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.votalks.api.persistence.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
}

package com.votalks.api.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.votalks.api.persistence.entity.Like;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidLike;

public interface UuidLikeRepository extends JpaRepository<UuidLike, Long> {
	@Query("SELECT ul FROM UuidLike ul WHERE ul.uuid = :uuid AND ul.like = :like")
	Optional<UuidLike> findByUuidAndLike(@Param("uuid") Uuid uuid, @Param("like") Like like);
}

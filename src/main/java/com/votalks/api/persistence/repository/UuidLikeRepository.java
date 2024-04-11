package com.votalks.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.votalks.api.persistence.entity.Like;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidLike;

public interface UuidLikeRepository extends JpaRepository<UuidLike, Long> {

	// 존재 여부 확인
	@Query("SELECT COUNT(ul) > 0 FROM UuidLike ul WHERE ul.uuid = :uuid AND ul.like = :like")
	boolean existsByUuidAndLike(@Param("uuid") Uuid uuid, @Param("like") Like like);

	// 삭제
	@Modifying
	@Query("DELETE FROM UuidLike ul WHERE ul.uuid = :uuid AND ul.like = :like")
	void deleteByUuidAndLike(@Param("uuid") Uuid uuid, @Param("like") Like like);
}

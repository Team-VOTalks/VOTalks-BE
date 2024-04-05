package com.votalks.api.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.votalks.api.persistence.entity.Uuid;

public interface UuidRepository extends JpaRepository<Uuid, UUID> {
}

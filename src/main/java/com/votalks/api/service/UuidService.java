package com.votalks.api.service;

import static com.votalks.global.common.util.GlobalConstant.*;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.repository.UuidRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UuidService {
	private final UuidRepository uuidRepository;

	public Uuid getOrCreate(String uuid) {
		if (StringUtils.isEmpty(uuid) || uuid.length() != UUID_LENGTH_STANDARD) {
			return uuidRepository.save(Uuid.create(UUID.randomUUID()));
		}

		return uuidRepository.findById(Uuid.fromString(uuid))
			.orElseGet(() -> uuidRepository.save(Uuid.create(UUID.randomUUID())));
	}
}

package com.votalks.api.service;

import static com.votalks.api.persistence.entity.Uuid.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.entity.UuidVoteOption;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.entity.VoteOption;
import com.votalks.api.persistence.repository.UuidRepository;
import com.votalks.api.persistence.repository.UuidVoteOptionRepository;
import com.votalks.api.persistence.repository.VoteOptionRepository;
import com.votalks.api.persistence.repository.VoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {
	private final VoteRepository voteRepository;
	private final VoteOptionRepository voteOptionRepository;
	private final UuidVoteOptionRepository uuidVoteOptionRepository;
	private final UuidRepository uuidRepository;

	public void create(VoteCreateDto dto) {
		final Uuid uuid = generate(dto.uuid());
		final Vote vote = Vote.create(dto, LocalDateTime.now(), uuid);
		final List<VoteOption> voteOptions = dto.voteOptions().stream()
			.map(optionValue -> VoteOption.create(optionValue, vote))
			.toList();
		final List<UuidVoteOption> uuidVoteOptions = voteOptions.stream()
			.map(optionValue -> UuidVoteOption.generated(uuid, optionValue))
			.toList();

		voteRepository.save(vote);
		voteOptionRepository.saveAll(voteOptions);
		uuidVoteOptionRepository.saveAll(uuidVoteOptions);
	}

	private Uuid generate(String uuid) {
		if (uuid == null) {
			return uuidRepository.save(Uuid.create(UUID.randomUUID()));
		}
		return uuidRepository.findById(fromString(uuid))
			.orElseGet(() -> uuidRepository.save(Uuid.create(fromString(uuid))));
	}
}

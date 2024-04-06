package com.votalks.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.dto.vote.VoteTakeDto;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.entity.VoteOption;
import com.votalks.api.persistence.repository.UuidRepository;
import com.votalks.api.persistence.repository.UuidVoteOptionRepository;
import com.votalks.api.persistence.repository.VoteOptionRepository;
import com.votalks.api.persistence.repository.VoteRepository;

@SpringBootTest
@AutoConfigureMockMvc
class VoteControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UuidRepository uuidRepository;

	@Autowired
	UuidVoteOptionRepository uuidVoteOptionRepository;

	@Autowired
	VoteRepository voteRepository;

	@Autowired
	VoteOptionRepository voteOptionRepository;

	@BeforeEach
	void setUp() {
		VoteCreateDto voteCreateDto = new VoteCreateDto(
			null,
			"테스트입니다.",
			"TECH",
			"테스트입니다",
			Arrays.asList("1번", "2번"),
			2);

		Vote vote = Vote.create(voteCreateDto, null);
		voteRepository.save(vote);

		final List<VoteOption> voteOptions = voteCreateDto.voteOptions().stream()
			.map(optionValue -> VoteOption.create(optionValue, vote))
			.toList();

		voteOptionRepository.saveAll(voteOptions);
	}

	@Test
	@DisplayName("POST - 투표를 성공정으로 생성한다. - void")
	void create_vote_success() throws Exception {
		// Given
		VoteCreateDto voteCreateDto = new VoteCreateDto(
			null,
			"테스트입니다.",
			"TECH",
			"테스트입니다",
			Arrays.asList("1번", "2번"),
			2);

		// When & Then
		this.mockMvc.perform(post("/api/v1/votes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(voteCreateDto)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST - 성공적으로 투표한다 - void")
	void select_success() throws Exception {
		// Given
		VoteTakeDto voteTakeDto = new VoteTakeDto(1L, null);

		// When & Then
		this.mockMvc.perform(post("/api/v1/votes/" + 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(voteTakeDto)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET - 특정 투표의 상세 정보를 성공적으로 가져온다. - VoteReadDto")
	void read_voteSuccess() throws Exception {
		// Given
		Vote savedVote = voteRepository.findAll().get(0);

		// When & Then
		this.mockMvc.perform(get("/api/v1/votes/{id}", savedVote.getId())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("테스트입니다."))
			.andExpect(jsonPath("$.category").value("기술"))
			.andExpect(jsonPath("$.createAt").exists())
			.andExpect(jsonPath("$.description").value("테스트입니다"))
			.andExpect(jsonPath("$.totalVoteCount").value(0))
			.andExpect(jsonPath("$.voteOptionsWithCount.['1번']").value(0))
			.andExpect(jsonPath("$.voteOptionsWithCount.['2번']").value(0))
			.andExpect(jsonPath("$.totalVoteCount").value(0));
	}
}

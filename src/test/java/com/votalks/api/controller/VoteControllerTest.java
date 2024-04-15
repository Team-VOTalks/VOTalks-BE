package com.votalks.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import com.votalks.api.dto.vote.VoteOptionWithCountDto;
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
		List<VoteCreateDto> voteCreateDtos = List.of(
			new VoteCreateDto("테스트1", "dev", "테스트 설명1", Arrays.asList("1번", "2번")),
			new VoteCreateDto("테스트2", "friend", "테스트 설명2", Arrays.asList("3번", "4번")),
			new VoteCreateDto("테스트3", "daily", "테스트 설명3", Arrays.asList("5번", "6번"))
		);

		for (VoteCreateDto voteCreateDto : voteCreateDtos) {
			Vote vote = Vote.create(voteCreateDto, null);
			vote = voteRepository.save(vote);

			Vote finalVote = vote;
			List<VoteOption> voteOptions = voteCreateDto.voteOptions().stream()
				.map(optionValue -> VoteOption.create(optionValue, finalVote))
				.collect(Collectors.toList());

			voteOptionRepository.saveAll(voteOptions);
		}
	}

	@Test
	@DisplayName("POST - 투표를 성공정으로 생성한다. - void")
	void create_vote_success() throws Exception {
		// Given
		VoteCreateDto voteCreateDto = new VoteCreateDto(
			"테스트입니다.",
			"dev",
			"테스트입니다",
			Arrays.asList("1번", "2번"));

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
		VoteTakeDto voteTakeDto = new VoteTakeDto(1L);

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
		VoteOptionWithCountDto voteOptionWithCountDto = new VoteOptionWithCountDto(savedVote.getId(),
			savedVote.getTitle(), 0, false);

		// When & Then
		this.mockMvc.perform(get("/api/v1/votes/{id}", savedVote.getId())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("테스트1"))
			.andExpect(jsonPath("$.category").value("개발"))
			.andExpect(jsonPath("$.createAt").exists())
			.andExpect(jsonPath("$.description").value("테스트 설명1"))
			.andExpect(jsonPath("$.totalVoteCount").value(0))
			.andExpect(jsonPath("$.voteOptionWithCounts[0].count").exists())
			.andExpect(jsonPath("$.voteOptionWithCounts[0].title").value("1번"));
	}

	@Test
	@DisplayName("GET - 첫 페이지의 3개의 값이 성공적으로 가져온다")
	void readAll_votes_Success() throws Exception {
		// When & Then
		this.mockMvc.perform(get("/api/v1/votes")
				.param("page", "0")
				.param("size", "3")
				.param("category", "dev")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").exists())
			.andExpect(jsonPath("$.content[0].title").exists())
			.andExpect(jsonPath("$.content[0].category").exists())
			.andExpect(jsonPath("$.content[0].createAt").exists())
			.andExpect(jsonPath("$.content[0].description").exists())
			.andExpect(jsonPath("$.content[0].totalVoteCount").isNumber())
			.andExpect(jsonPath("$.pageable.pageSize").value(3))
			.andExpect(jsonPath("$.pageable.pageNumber").value(0));
	}

}

package com.votalks.api.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.dto.vote.VoteTakeDto;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.repository.VoteRepository;
import com.votalks.api.service.VoteService;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class VoteControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	VoteService voteService;

	@Autowired
	VoteRepository voteRepository;

	@Test
	@DisplayName("POST - 투표를 성공정으로 생성한다. - void")
	void create_vote_success() throws Exception {
		// Given
		VoteCreateDto voteCreateDto = new VoteCreateDto(
			"테스트1", "daily", "테스트 입니다.", Arrays.asList("1번", "2번")
		);
		String json = objectMapper.writeValueAsString(voteCreateDto);

		// When & Then
		mockMvc.perform(post("/api/v1/votes")
				.contentType(APPLICATION_JSON)
				.content(json))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST - 성공적으로 투표한다 - void")
	void select_success() throws Exception {
		// Given

		VoteTakeDto voteTakeDto = new VoteTakeDto(1L);
		String json = objectMapper.writeValueAsString(voteTakeDto);

		// When & Then
		mockMvc.perform(post("/api/v1/votes/1")
				.contentType(APPLICATION_JSON)
				.content(json))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET - 페이징된 투표 조회 성공 - PageResponse<VoteReadDto>")
	void read_Paged_Comments_success() throws Exception {
		// When & Then
		mockMvc.perform(get("/api/v1/votes"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET - 투표 단건  조회 성공 - PageResponse<VoteReadDto>")
	void read_comment_success() throws Exception {
		// Given
		VoteCreateDto voteCreateDto = new VoteCreateDto(
			"테스트1", "daily", "테스트 입니다.", Arrays.asList("1번", "2번")
		);
		Vote vote = voteRepository.save(Vote.create(voteCreateDto, null));

		// When & Then
		mockMvc.perform(get("/api/v1/votes/" + vote.getId()))
			.andDo(print())
			.andExpect(status().isOk());
	}
}

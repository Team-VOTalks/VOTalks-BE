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
import com.votalks.api.dto.comment.CommentCreateDto;
import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.repository.VoteRepository;
import com.votalks.api.service.CommentService;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	CommentService commentService;

	@Autowired
	VoteRepository voteRepository;

	@Test
	@DisplayName("POST - 댓글을 성공적으로 생성한다. - void")
	void create_vote_success() throws Exception {
		// Given
		CommentCreateDto commentCreateDto = new CommentCreateDto("댓글");
		String json = objectMapper.writeValueAsString(commentCreateDto);

		// When & Then
		mockMvc.perform(post("/api/v1/votes/1/comments")
				.contentType(APPLICATION_JSON)
				.content(json))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET - 페이징된 댓글 조회 성공 - PageResponse<CommentReadDto>")
	void read_Paged_Comments_success() throws Exception {
		// Given
		VoteCreateDto voteCreateDto = new VoteCreateDto(
			"테스트1", "daily", "테스트 입니다.", Arrays.asList("1번", "2번")
		);
		Vote vote = voteRepository.save(Vote.create(voteCreateDto, null));

		// When & Then
		mockMvc.perform(get("/api/v1/votes/" + vote.getId() + "/comments"))
			.andDo(print())
			.andExpect(status().isOk());
	}
}

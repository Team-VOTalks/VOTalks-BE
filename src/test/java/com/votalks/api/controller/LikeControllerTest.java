package com.votalks.api.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.votalks.api.dto.like.LikeCreateDto;
import com.votalks.api.service.LikeService;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class LikeControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	LikeService likeService;

	@Test
	@DisplayName("POST - 댓글에 성공적으로 좋아요를 누른다. - void")
	void create_like_comment_success() throws Exception {
		// Given
		LikeCreateDto likeCreateDto = new LikeCreateDto("like");
		String json = objectMapper.writeValueAsString(likeCreateDto);

		// When & Then
		mockMvc.perform(post("/api/v1/votes/1/comments/1/like-type")
				.contentType(APPLICATION_JSON)
				.content(json))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST - 답글에 성공적으로 좋아요를 누른다. - void")
	void create_like_reply_success() throws Exception {
		// Given
		LikeCreateDto likeCreateDto = new LikeCreateDto("like");
		String json = objectMapper.writeValueAsString(likeCreateDto);

		// When & Then
		mockMvc.perform(post("/api/v1/votes/1/comments/1/replies/1/like-type")
				.contentType(APPLICATION_JSON)
				.content(json))
			.andExpect(status().isOk());
	}
}

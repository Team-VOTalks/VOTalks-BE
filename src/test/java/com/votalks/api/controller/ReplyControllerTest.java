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
import com.votalks.api.dto.reply.ReplyCreateDto;
import com.votalks.api.service.ReplyService;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ReplyControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ReplyService replyService;

	@Test
	@DisplayName("POST - 답글을 성공적으로 생성한다. - void")
	void create_reply_success() throws Exception {
		// Given
		ReplyCreateDto replyCreateDto = new ReplyCreateDto("like");
		String json = objectMapper.writeValueAsString(replyCreateDto);

		// When & Then
		mockMvc.perform(post("/api/v1/votes/1/comments/1")
				.contentType(APPLICATION_JSON)
				.content(json))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET - 답글을 성공적으로 조회한다. - PageResponse<ReplyDto>")
	void read_reply_success() throws Exception {
		// When & Then
		mockMvc.perform(get("/api/v1/votes/1/comments/1"))
			.andExpect(status().isOk());
	}
}

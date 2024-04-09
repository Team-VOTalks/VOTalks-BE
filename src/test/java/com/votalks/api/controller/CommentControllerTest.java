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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.votalks.api.dto.comment.CommentCreateDto;
import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.persistence.entity.Vote;
import com.votalks.api.persistence.entity.VoteOption;
import com.votalks.api.persistence.repository.CommentRepository;
import com.votalks.api.persistence.repository.UuidRepository;
import com.votalks.api.persistence.repository.UuidVoteOptionRepository;
import com.votalks.api.persistence.repository.VoteOptionRepository;
import com.votalks.api.persistence.repository.VoteRepository;
import com.votalks.api.service.CommentService;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {
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

	@Autowired
	CommentRepository commentRepository;

	@MockBean
	CommentService commentService;

	@BeforeEach
	void setUp() {
		VoteCreateDto voteCreateDto = new VoteCreateDto(null, "테스트입니다.", "DEV", "테스트입니다", Arrays.asList("1번", "2번"), 2);

		Vote vote = Vote.create(voteCreateDto, null);
		vote = voteRepository.save(vote);

		Vote finalVote = vote;
		List<VoteOption> voteOptions = voteCreateDto.voteOptions()
			.stream()
			.map(optionValue -> VoteOption.create(optionValue, finalVote))
			.collect(Collectors.toList());

		voteOptionRepository.saveAll(voteOptions);
	}

	@Test
	@DisplayName("POST - 댓글을 성공적으로 생성한다. - void")
	void create_vote_success() throws Exception {
		CommentCreateDto commentCreateDto = new CommentCreateDto("테스트", null);

		// When & Then
		this.mockMvc.perform(post("/api/v1/comments/" + 1L).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(commentCreateDto))).andExpect(status().isOk());
	}
}
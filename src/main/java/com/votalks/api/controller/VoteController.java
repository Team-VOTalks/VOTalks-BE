package com.votalks.api.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.dto.vote.VoteTakeDto;
import com.votalks.api.service.VoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
public class VoteController {
	private final VoteService voteService;

	@PostMapping
	public void create(@RequestBody @Valid VoteCreateDto dto) {
		voteService.create(dto);
	}

	@PostMapping("/{id}")
	public void selectVote(@RequestBody @Valid VoteTakeDto dto, @PathVariable Long id) {
		voteService.selectVote(dto, id);
	}
}

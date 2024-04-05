package com.votalks.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.service.VoteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vote")
@RequiredArgsConstructor
public class VoteController {
	private final VoteService voteService;

	@PostMapping
	public void create(@RequestBody VoteCreateDto dto) {
		voteService.create(dto);
	}
}
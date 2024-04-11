package com.votalks.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.dto.vote.VoteReadDto;
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

	@PostMapping("/{vote-id}")
	public void select(@RequestBody @Valid VoteTakeDto dto, @PathVariable(name = "vote-id") Long id) {
		voteService.select(dto, id);
	}

	@GetMapping("/{vote-id}")
	public VoteReadDto read(@PathVariable(name = "vote-id") Long id) {
		return voteService.read(id);
	}

	@GetMapping
	public Page<VoteReadDto> readAll(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String category
	) {
		return voteService.readAll(page, size, category);
	}
}

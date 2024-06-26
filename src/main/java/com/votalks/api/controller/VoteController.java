package com.votalks.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.votalks.api.dto.PageResponse;
import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.dto.vote.VoteReadDto;
import com.votalks.api.dto.vote.VoteTakeDto;
import com.votalks.api.dto.voteOption.VoteOptionReadDto;
import com.votalks.api.service.VoteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
public class VoteController {
	private final VoteService voteService;

	@PostMapping
	public void create(
		@RequestBody @Valid VoteCreateDto dto,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		voteService.create(dto, request, response);
	}

	@PostMapping("/{vote-id}")
	public List<VoteOptionReadDto> select(
		@RequestBody @Valid VoteTakeDto dto,
		@PathVariable(name = "vote-id") Long id,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		return voteService.select(dto, id, request, response);
	}

	@GetMapping("/{vote-id}")
	public VoteReadDto read(
		@PathVariable(name = "vote-id") Long id,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		return voteService.read(id, request, response);
	}

	@GetMapping
	public PageResponse<VoteReadDto> readAll(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String category,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		return voteService.readAll(page, size, category, request, response);
	}
}

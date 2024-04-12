package com.votalks.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.votalks.api.dto.vote.VoteCreateDto;
import com.votalks.api.dto.vote.VoteOptionWithCountDto;
import com.votalks.api.dto.vote.VoteReadDto;
import com.votalks.api.dto.vote.VoteResponse;
import com.votalks.api.dto.vote.VoteTakeDto;
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
	public HttpHeaders create(
		@RequestBody @Valid VoteCreateDto dto,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		return voteService.create(dto, request, response);
	}

	@PostMapping("/{vote-id}")
	public ResponseEntity<List<VoteOptionWithCountDto>> select(
		@RequestBody @Valid VoteTakeDto dto,
		@PathVariable(name = "vote-id") Long id,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		VoteResponse<List<VoteOptionWithCountDto>> voteResponse = voteService.select(dto, id, request, response);
		HttpHeaders headers = voteResponse.httpHeaders();
		List<VoteOptionWithCountDto> voteData = voteResponse.data();

		return ResponseEntity.ok()
			.headers(headers)
			.body(voteData);
	}

	@GetMapping("/{vote-id}")
	public ResponseEntity<VoteReadDto> read(
		@PathVariable(name = "vote-id") Long id,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		VoteResponse<VoteReadDto> voteResponse = voteService.read(id, request, response);
		HttpHeaders headers = voteResponse.httpHeaders();
		VoteReadDto voteData = voteResponse.data();

		return ResponseEntity.ok()
			.headers(headers)
			.body(voteData);
	}

	@GetMapping
	public ResponseEntity<Page<VoteReadDto>> readAll(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String category,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		VoteResponse<Page<VoteReadDto>> voteResponse = voteService.readAll(page, size, category, request, response);
		HttpHeaders headers = voteResponse.httpHeaders();
		Page<VoteReadDto> voteData = voteResponse.data();

		return ResponseEntity.ok()
			.headers(headers)
			.body(voteData);
	}
}

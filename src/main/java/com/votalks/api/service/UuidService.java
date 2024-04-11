package com.votalks.api.service;

import static com.votalks.global.common.util.GlobalConstant.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votalks.api.persistence.entity.Uuid;
import com.votalks.api.persistence.repository.UuidRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UuidService {
	private final UuidRepository uuidRepository;

	public Uuid getOrCreate(HttpServletRequest request, HttpServletResponse response) {
		String uuidValue = null;
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			// "X-VOTalks-Authorization" 이름을 가진 쿠키를 찾습니다.
			Optional<Cookie> uuidCookie = Arrays.stream(cookies)
				.filter(c -> c.getName().equals(UUID_COOKIE))
				.findFirst();

			if (uuidCookie.isPresent()) {
				uuidValue = uuidCookie.get().getValue(); //X-VOTalks-Authorization 값 가져오기
			}
		}

		// 쿠키 값 검증 (null 체크 및 길이 검증)
		if (StringUtils.isEmpty(uuidValue) || uuidValue.length() != UUID_LENGTH_STANDARD) {
			Uuid newUuid = uuidRepository.save(Uuid.create(UUID.randomUUID()));

			// 새로운 쿠키 생성 및 응답에 추가
			Cookie newCookie = new Cookie(UUID_COOKIE, newUuid.toString().replace("-", ""));
			newCookie.setPath("/");
			newCookie.setHttpOnly(true);
			response.addCookie(newCookie);

			return newUuid;
		}

		return uuidRepository.findById(Uuid.fromString(uuidValue))
			.orElseGet(() -> {
				Uuid newUuid = Uuid.create(UUID.randomUUID());
				Uuid saveUuid = uuidRepository.save(newUuid);

				Cookie newCookie = new Cookie(UUID_COOKIE, saveUuid.toString().replace("-", ""));
				newCookie.setPath("/");
				newCookie.setHttpOnly(true);
				response.addCookie(newCookie);

				return saveUuid;
			});
	}

	public HttpHeaders getHttpHeaders(Uuid uuid) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(UUID_COOKIE, uuid.fromUuid());
		return responseHeaders;
	}
}

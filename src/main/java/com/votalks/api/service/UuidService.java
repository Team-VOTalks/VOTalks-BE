package com.votalks.api.service;

import static com.votalks.global.common.util.GlobalConstant.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
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
		Cookie[] cookies = request.getCookies();
		String uuidValue = getCookie(cookies);

		if (StringUtils.isEmpty(uuidValue) || uuidValue.length() != UUID_LENGTH_STANDARD) {
			Uuid newUuid = uuidRepository.save(Uuid.create(UUID.randomUUID()));
			createNewCookie(response, newUuid);

			return newUuid;
		}

		return uuidRepository.findById(Uuid.fromString(uuidValue))
			.orElseGet(() -> {
				Uuid newUuid = Uuid.create(UUID.randomUUID());
				Uuid saveUuid = uuidRepository.save(newUuid);
				createNewCookie(response, saveUuid);

				return saveUuid;
			});
	}

	private static String getCookie(Cookie[] cookies) {
		String uuidValue = null;

		if (cookies != null) {
			Optional<Cookie> uuidCookie = Arrays.stream(cookies)
				.filter(c -> c.getName().equals(UUID_COOKIE))
				.findFirst();

			if (uuidCookie.isPresent()) {
				uuidValue = uuidCookie.get().getValue();
			}
		}
		return uuidValue;
	}

	private static void createNewCookie(HttpServletResponse response, Uuid saveUuid) {
		Cookie newCookie = new Cookie(UUID_COOKIE, saveUuid.toString().replace("-", ""));
		newCookie.setPath("/");
		newCookie.setHttpOnly(true);
		response.addCookie(newCookie);
	}

	public void setHttpHeaders(HttpServletResponse response, Uuid uuid) {
		response.setHeader(UUID_COOKIE, uuid.fromUuid());
	}
}

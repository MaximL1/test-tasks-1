package com.tui.tui_task.controller;

import java.util.List;

import com.tui.tui_task.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AdminController {

	private final JwtTokenService jwtTokenService;

	@GetMapping("/token")
	public ResponseEntity<String> generateToken() {
		String token = jwtTokenService.generateToken("admin", List.of("ROLE_ADMIN"));
		return ResponseEntity.ok(token);
	}
}


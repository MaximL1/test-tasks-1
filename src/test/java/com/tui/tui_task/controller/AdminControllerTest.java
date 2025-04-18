package com.tui.tui_task.controller;

import java.util.List;

import com.tui.tui_task.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminController.class)
class AdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private JwtTokenService jwtTokenService;

	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void shouldGenerateTokenSuccessfully() throws Exception {
		String mockToken = "mocked-jwt-token";

		when(jwtTokenService.generateToken("admin", List.of("ROLE_ADMIN")))
				.thenReturn(mockToken);

		mockMvc.perform(get("/auth/token"))
			   .andExpect(status().isOk())
			   .andExpect(content().string(mockToken));

		verify(jwtTokenService).generateToken("admin", List.of("ROLE_ADMIN"));
	}
}

package com.tui.tui_task.security;

import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class JwtTokenService {

	private final JwtEncoder jwtEncoder;

	public JwtTokenService(JwtEncoder jwtEncoder) {
		this.jwtEncoder = jwtEncoder;
	}

	public String generateToken(String username, List<String> roles) {
		Instant now = Instant.now();

		JwtClaimsSet claims = JwtClaimsSet.builder()
										  .issuer("self")
										  .issuedAt(now)
										  .expiresAt(now.plus(1, ChronoUnit.HOURS))
										  .subject(username)
										  .claim("scope", String.join(" ", roles))
										  .build();

		JwsHeader header = JwsHeader.with(() -> "RS256").build();

		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}



}



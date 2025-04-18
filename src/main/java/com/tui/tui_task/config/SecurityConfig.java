package com.tui.tui_task.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Value("${jwt.public.key}")
	private Resource publicKeyResource;
	@Value("${jwt.private.key}")
	private Resource privateKeyResource;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/auth/token",
								"/swagger-ui/**",
								"/v3/api-docs/**",
								"/swagger-resources/**",
								"/webjars/**",
								"/actuator/**"
						).permitAll()
						.requestMatchers(HttpMethod.PUT, "/orders/*").permitAll()
						.requestMatchers(HttpMethod.POST, "/orders").permitAll()
						.anyRequest().authenticated()
				)
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
				);

		return http.build();
	}

	@Bean
	public JwtDecoder jwtDecoder() throws Exception {
		byte[] keyBytes;

		try (InputStream is = publicKeyResource.getInputStream()) {
			keyBytes = is.readAllBytes();
		}

		String publicKeyPEM = new String(keyBytes, StandardCharsets.UTF_8)
				.replace("-----BEGIN PUBLIC KEY-----", "")
				.replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s", "");

		byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);

		return NimbusJwtDecoder.withPublicKey((RSAPublicKey) publicKey).build();
	}

	@Bean
	public JwtEncoder jwtEncoder() {
		try (InputStream inputStream = privateKeyResource.getInputStream();
			 Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {

			String pem = scanner.useDelimiter("\\A").next();
			RSAKey rsaJWK = (RSAKey) JWK.parseFromPEMEncodedObjects(pem);

			JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new com.nimbusds.jose.jwk.JWKSet(rsaJWK));
			return new NimbusJwtEncoder(jwkSource);

		} catch (Exception e) {
			//Create someone custom exception for key
			throw new RuntimeException("Error loading RSA private key", e);
		}
	}

}

package com.tui.tui_task.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	private static final String SECURITY_SCHEMA_NAME = "bearerAuth";
	private final BuildProperties buildProperties;

	public SwaggerConfig(BuildProperties buildProperties) {this.buildProperties = buildProperties;}

	@Bean
	public OpenAPI apiInfo() {
		return new OpenAPI()
				.info(
					new Info()
						.title("Tui market API")
						.description("REST API for Tui market")
						.version(buildProperties.getVersion())
						.contact(new Contact().name("Maxim L.").url("https://github.com/MaximL1").email("lupascu.maxim@gmail.com"))
						.license(new License().name("Apache 2.0").url("https://opensource.org/licenses/Apache-2.0")))
				.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEMA_NAME))
				.components(
					new Components()
						.addSecuritySchemes(
							SECURITY_SCHEMA_NAME,
							new SecurityScheme()
								.name(SECURITY_SCHEMA_NAME)
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.description("Provide Token")
								.bearerFormat("JWT")));
	}
}

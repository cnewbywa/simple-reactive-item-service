package com.cnewbywa.item.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConf {

	@Bean
	SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
		http
			.csrf(CsrfSpec::disable)
			.authorizeExchange(authorize -> authorize
					.pathMatchers(HttpMethod.GET, "/items", "/items/paging", "/items/skip").permitAll()
					.pathMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**", "/webjars/swagger-ui/**").permitAll()
					.anyExchange().authenticated())
			.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));
		
		return http.build();
	}
}

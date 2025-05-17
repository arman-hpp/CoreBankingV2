package com.bank.core.configs.security;

import com.bank.core.configs.security.filters.JwtAuthenticationFilter;
import com.bank.core.configs.security.filters.RateLimitingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    public final JwtAuthenticationFilter jwtAuthenticationFilter;
    public final RateLimitingFilter rateLimitingFilter;
    private final Environment environment;

    public WebSecurityConfiguration(Environment environment,
                                    JwtAuthenticationFilter jwtAuthenticationFilter,
                                    RateLimitingFilter rateLimitingFilter) {
        this.environment = environment;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> {
                    if (environment.acceptsProfiles(Profiles.of("prod"))) {
                        requests
                                .requestMatchers("/error/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/auth/captcha").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register", "/api/auth/refresh_token").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    }
                    else {
                        requests
                                .requestMatchers("/h2/**").permitAll()
                                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()
                                .requestMatchers("/error/**").permitAll()
                                .requestMatchers("/api/dev/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/auth/captcha").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register", "/api/auth/refresh_token").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    }

                    requests.anyRequest().authenticated();
                });

        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            http.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);
        }
        http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            configuration.setAllowedOrigins(List.of("http://192.168.1.102:8091"));
        } else {
            configuration.setAllowedOrigins(List.of("http://localhost:8090", "http://127.0.0.1:8090"));
        }
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));
        configuration.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}
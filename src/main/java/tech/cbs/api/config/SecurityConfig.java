package tech.cbs.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/**", "/api/v1/authors/**", "/api/v1/tags/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/books/**", "/api/v1/authors/**", "/api/v1/tags/**").hasAuthority("SCOPE_write")
                        .requestMatchers("api/v1/users/**").authenticated()
                        .requestMatchers("/api/v1/admin").hasAuthority("SCOPE_admin")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}

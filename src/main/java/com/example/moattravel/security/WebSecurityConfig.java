package com.example.moattravel.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests((requests) -> requests
	            .requestMatchers("/css/**", "/images/**", "/js/**", "/", "/signup/**", "/houses/**").permitAll() // 全員アクセス可能
	            .requestMatchers("/admin/**").hasRole("ADMIN") // 管理者のみ
	            .requestMatchers("/user/**").hasRole("GENERAL") // 一般ユーザー（閲覧者）用
	            .anyRequest().authenticated()
	        )   .formLogin(form -> form
	        	    .loginPage("/login")
	        	    .loginProcessingUrl("/login")
	        	    .successHandler(new AuthenticationSuccessHandler() {
	        	        @Override
	        	        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	        	                                            Authentication authentication) throws IOException, ServletException {
	        	            boolean isAdmin = authentication.getAuthorities().stream()
	        	                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	        	            
	        	            if (isAdmin) {
	        	                response.sendRedirect("/admin/houses");
	        	            } else {
	        	                response.sendRedirect("/houses");
	        	            }
	        	        }
	        	    })
	        	    .permitAll()
	        	)
	        .logout(logout -> logout
	            .logoutSuccessUrl("/")
	            .permitAll()
	        );
	    
	    return http.build();
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
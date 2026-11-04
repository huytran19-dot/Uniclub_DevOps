package com.uniclub.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.uniclub.security.CustomAccessDeniedHandler;
import com.uniclub.security.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // 1. Endpoints công khai không yêu cầu token
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/vnpay/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()

                // 2. Cho phép khách xem sản phẩm, danh mục (Chỉ GET)
                .requestMatchers(HttpMethod.GET,
                    "/api/products/**",
                    "/api/categories/**",
                    "/api/brands/**",
                    "/api/variants/**",
                    "/api/sizes/**",
                    "/api/colors/**"
                ).permitAll()

                // 3. Nghiệp vụ Quản trị viên (Chỉ SysAdmin)
                .requestMatchers("/api/roles/**").hasRole("SysAdmin")
                .requestMatchers("/api/grn-headers/**", "/api/grn-details/**").hasRole("SysAdmin")
                .requestMatchers("/api/suppliers/**").hasRole("SysAdmin")
                .requestMatchers("/api/upload/**").hasRole("SysAdmin")
                .requestMatchers(HttpMethod.POST, "/api/products/**", "/api/categories/**", "/api/brands/**", "/api/variants/**").hasRole("SysAdmin")
                .requestMatchers(HttpMethod.PUT, "/api/products/**", "/api/categories/**", "/api/brands/**", "/api/variants/**").hasRole("SysAdmin")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**", "/api/categories/**", "/api/brands/**", "/api/variants/**").hasRole("SysAdmin")

                // 4. Mọi nghiệp vụ còn lại (Orders, Carts, Users) yêu cầu xác thực và phân quyền IDOR qua @PreAuthorize
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

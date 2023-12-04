package com.example.eSmartRecruit.config;


import com.example.eSmartRecruit.models.enumModel.Role;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(auth->auth

                                            .requestMatchers("/eSmartRecruit/register","/eSmartRecruit/auth","/eSmartRecruit/candidate/home","/eSmartRecruit/hello", "/eSmartRecruit/resetpassword","/eSmartRecruit/search/**")
                                            .permitAll() //let all request pass to the above URL, no authen yet
                                            .requestMatchers("/eSmartRecruit/candidate/**")
                                            .hasAnyAuthority(Role.Candidate.name())//only candidate can get access to the link above
                        .requestMatchers("/eSmartRecruit/interviewer/**")
                        .hasAnyAuthority(Role.Interviewer.name())//only for interviewer
                        .requestMatchers("/eSmartRecruit/admin/**")
                        .hasAnyAuthority(Role.Admin.name())//only for admin
                        .anyRequest()
                                            .authenticated())
                .sessionManagement(session -> session
                                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                            .authenticationProvider(authenticationProvider)
                                            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(out ->out.logoutUrl("eSmartRecruit/logout").invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID").permitAll())
        ;
        return httpSecurity.build();
    }
}

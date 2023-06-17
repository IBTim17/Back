package com.ib.Tim17_Back.security;

import com.ib.Tim17_Back.dtos.TokenDTO;
import com.ib.Tim17_Back.security.jwt.JwtAuthenticationEntryPoint;
import com.ib.Tim17_Back.security.jwt.JwtRequestFilter;
import com.ib.Tim17_Back.services.UserService;
import com.ib.Tim17_Back.services.interfaces.IUserService;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private JwtAuthenticationEntryPoint entryPoint;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("X-Auth-Token", "skip", "refreshToken", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000","http://localhost:8080", "http://localhost:5432"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("X-Auth-Token"));

        http
                .headers()
                .frameOptions()
                .disable()
                .and()
                .csrf()
                .disable()
                .cors()
                .configurationSource(request -> corsConfiguration)
                .and()
                .authorizeRequests()

                .antMatchers("/api/user/login").permitAll()
                .antMatchers("/api/user/resetPassword").permitAll()
                .antMatchers("/api/requests").permitAll()
                .antMatchers("/api/user/oauth").permitAll()
                .antMatchers("/api/user/handleOauth/**").permitAll()
                .antMatchers("/api/user/register").permitAll()
                .antMatchers("/api/user/recaptcha/**").permitAll()
                .antMatchers("/api/**").authenticated()
                         

                .and()
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint()
                        .and()
                        .authorizationEndpoint()
                        .baseUri("/oauth2/authorization")
                        .and()
                        .defaultSuccessUrl("/secured")
                        .successHandler((request, response, authentication) -> {
                            DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
                            String email = (String) user.getAttributes().get("email");
                            System.out.println("Email from oauth:" + email);
                            //TokenDTO token = userService.googleToken(email);
                            String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/api/user/handleOauth/" + email)
                                    .toUriString();
                            //String redirectUrl = "http://localhost:8080/api/user/handleOauth/" + email;
                            response.sendRedirect(redirectUrl);

                            //response.sendRedirect(redirectUrl);
                        }))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(entryPoint);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
}

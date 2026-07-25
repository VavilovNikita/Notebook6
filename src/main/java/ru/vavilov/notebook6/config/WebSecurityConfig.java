package ru.vavilov.notebook6.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.vavilov.notebook6.notebook.service.UserDetailService;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    /**
     * CodeSage/SubEditor/wordsTranslator are internal tooling, restricted to admins.
     * Anonymous and non-admin users get a 403 here instead of the usual login redirect,
     * so hitting these URLs directly doesn't hint at a login prompt for a tool they can't use.
     */
    private static final RequestMatcher ADMIN_ONLY_MODULES = new OrRequestMatcher(
            new AntPathRequestMatcher("/codesaga/**"),
            new AntPathRequestMatcher("/subtitles/**"),
            new AntPathRequestMatcher("/subeditor/**"),
            new AntPathRequestMatcher("/translator/**")
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationEntryPoint forbiddenEntryPoint = (request, response, authException) ->
                response.sendError(HttpServletResponse.SC_FORBIDDEN);

        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/translator/**"))
            .authorizeHttpRequests(request -> request
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/codesaga/**", "/subtitles/**", "/subeditor/**", "/translator/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/register",
                            "/error",
                            "/cam",
                            "/css/**",
                            "/images/**")
                        .permitAll()
                        .anyRequest().hasAnyRole("USER", "ADMIN"))
                .exceptionHandling(handling -> handling
                        .defaultAuthenticationEntryPointFor(forbiddenEntryPoint, ADMIN_ONLY_MODULES))
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform-login")
                        .defaultSuccessUrl("/notebook", true)
                        .failureUrl("/login?error")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll())
                .logout((logout) -> logout.logoutSuccessUrl("/login"));
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailService userDetailService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package ru.vavilov.notebook6.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.vavilov.notebook6.service.UserDetailService;

import java.util.Collections;

@Component
public class AuthProvider implements AuthenticationProvider {
    private final UserDetailService userDetailsService;

    @Autowired
    public AuthProvider(UserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        String password = authentication.getCredentials().toString();

        if (!password.equals(userDetails.getPassword()))
            throw new BadCredentialsException("Неверный пароль");
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                password,
                Collections.emptyList());//лист нужен для списка прав
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}

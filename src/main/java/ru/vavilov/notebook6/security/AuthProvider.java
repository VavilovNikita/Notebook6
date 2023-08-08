package ru.vavilov.notebook6.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.vavilov.notebook6.service.PersonDetailsService;

import java.util.Collections;

@Component
public class AuthProvider implements AuthenticationProvider {
    private final PersonDetailsService personDetailsService;

    @Autowired
    public AuthProvider(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        UserDetails personDetails = personDetailsService.loadUserByUsername(userName);
        String password = authentication.getCredentials().toString();

        if (!password.equals(personDetails.getPassword()))
            throw new BadCredentialsException("Неверный пароль");
        return new UsernamePasswordAuthenticationToken(
                personDetails,
                password,
                Collections.emptyList());//лист нужен для списка прав
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}

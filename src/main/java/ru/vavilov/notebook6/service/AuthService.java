package ru.vavilov.notebook6.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.vavilov.notebook6.entity.User;
import ru.vavilov.notebook6.security.UserDetail;

public class AuthService {

    public static User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetails = (UserDetail) authentication.getPrincipal();
        return userDetails.getUser();
    }
}

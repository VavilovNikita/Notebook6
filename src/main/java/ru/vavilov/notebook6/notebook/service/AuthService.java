package ru.vavilov.notebook6.notebook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.vavilov.notebook6.notebook.entity.User;
import ru.vavilov.notebook6.notebook.repository.UserRepository;
import ru.vavilov.notebook6.notebook.security.UserDetail;

@Component
public class AuthService {
    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetails = (UserDetail) authentication.getPrincipal();
        return userRepository.findById(userDetails.getUser().getId()).orElseGet(User::new);
    }
}

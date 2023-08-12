package ru.vavilov.notebook6.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.vavilov.notebook6.entity.User;
import ru.vavilov.notebook6.repository.UserRepository;
import ru.vavilov.notebook6.security.UserDetail;

import java.util.Optional;

@Component
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(UserDetail::new)
                .orElseThrow(() -> new UsernameNotFoundException("Не удается найти пользователя :" + username));
    }
}

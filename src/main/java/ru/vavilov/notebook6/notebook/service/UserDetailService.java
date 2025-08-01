package ru.vavilov.notebook6.notebook.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.vavilov.notebook6.notebook.security.UserDetail;
import ru.vavilov.notebook6.notebook.repository.UserRepository;

@Component
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(UserDetail::new)
                .orElseThrow(() -> new UsernameNotFoundException("Не удается найти пользователя :" + username));
    }
}

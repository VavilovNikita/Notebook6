package ru.vavilov.notebook6.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.entity.User;
import ru.vavilov.notebook6.repository.UserRepository;


@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminService {

    private final UserRepository userRepository;

    @Autowired
    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void saveUser(User user) {
        userRepository.save(user);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }


}

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

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }
    public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }
    public void saveUser(User user) {
        userRepository.save(user);
    }
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }


}

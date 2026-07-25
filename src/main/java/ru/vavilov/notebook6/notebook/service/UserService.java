package ru.vavilov.notebook6.notebook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.notebook.entity.User;
import ru.vavilov.notebook6.notebook.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleService = roleService;
    }

    public void saveUser(User user) {
        Optional<User> userFromDB = userRepository.findById(user.getId());
        if (userFromDB.isPresent()) {
            User existingUser = userFromDB.get();
            user.setRole(existingUser.getRole());
            if (user.getPassword() == null || user.getPassword().isBlank()
                    || user.getPassword().equals(existingUser.getPassword())) {
                user.setPassword(existingUser.getPassword());
            } else {
                user.setPassword(encoder.encode(user.getPassword()));
            }
        } else {
            user.setRole(roleService.getRoleById(1));
            user.setPassword(encoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

}

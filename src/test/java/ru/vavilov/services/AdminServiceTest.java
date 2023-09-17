package ru.vavilov.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.vavilov.notebook6.entity.User;
import ru.vavilov.notebook6.repository.UserRepository;
import ru.vavilov.notebook6.service.AdminService;

@SpringBootTest(classes = AdminService.class)
public class AdminServiceTest {
    @MockBean
    UserRepository userRepository;
    @Autowired
    private AdminService adminService;
    private final User user = new User();

    @Test
    public void findAll() {
        adminService.findAll();
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    void findById() {
        adminService.findById(0);
        Mockito.verify(userRepository, Mockito.times(1)).findById(0);
    }

    @Test
    void saveUser() {
        adminService.saveUser(user);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void deleteUser() {
        adminService.deleteUser(0);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(0);
    }
}

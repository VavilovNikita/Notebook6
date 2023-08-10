package ru.vavilov.notebook6.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.vavilov.notebook6.entity.User;
import ru.vavilov.notebook6.service.UserService;

@Component
public class UserValidator implements Validator {
    UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            errors.rejectValue("email", "","Этот email уже зарегестрирован");
        }
        try {
            user.getDateOfBirth().getTime();
        }catch (Throwable e){
            errors.rejectValue("dateOfBirth", "","неверный формат даты");
        }

    }
}

package ru.vavilov.notebook6.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.entity.Role;
import ru.vavilov.notebook6.repository.RoleRepository;

@Service
public class RoleService {
    RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    public Role getRoleById(int id){
        return roleRepository.findById(id).orElse(null);
    }
}

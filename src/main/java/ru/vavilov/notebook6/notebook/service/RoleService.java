package ru.vavilov.notebook6.notebook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vavilov.notebook6.notebook.entity.Role;
import ru.vavilov.notebook6.notebook.repository.RoleRepository;

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

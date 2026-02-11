package com.vinyl.VinylExchange.service;

import com.vinyl.VinylExchange.domain.entity.Role;
import com.vinyl.VinylExchange.repository.RoleRepository;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.enums.RoleName;
import com.vinyl.VinylExchange.exception.RoleNotFoundException;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {

        this.roleRepository = roleRepository;
    }

    public Role getRoleByName(RoleName name) {

        return roleRepository
                .findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
    }

    public Role saveRole(Role role) {

        return roleRepository.save(role);
    }

    public boolean existsByName(RoleName roleName) {

        return roleRepository.existsByName(roleName);
    }

}

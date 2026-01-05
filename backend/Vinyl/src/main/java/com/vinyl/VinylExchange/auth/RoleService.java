package com.vinyl.VinylExchange.auth;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.auth.enums.RoleName;
import com.vinyl.VinylExchange.shared.exception.RoleNotFoundException;

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

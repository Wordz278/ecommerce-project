package com.adrian.ecommerceproject.repository;
import java.util.Optional;

import com.adrian.ecommerceproject.models.ERole;
import com.adrian.ecommerceproject.models.Role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}

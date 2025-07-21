package dev.akshit.usermicromart.repositories;

import dev.akshit.usermicromart.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}

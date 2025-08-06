package dev.akshit.usermicromart.repositories;

import dev.akshit.usermicromart.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByNameIn(@Param("names") Set<String> names);

}

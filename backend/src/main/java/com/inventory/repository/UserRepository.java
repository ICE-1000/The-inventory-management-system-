package com.inventory.repository;

import com.inventory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.department WHERE u.username = :username")
    Optional<User> findByUsernameWithDepartment(@Param("username") String username);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.department WHERE u.id = :id")
    Optional<User> findByIdWithDepartment(@Param("id") UUID id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndIdNot(String username, UUID id);
    boolean existsByEmailAndIdNot(String email, UUID id);
}

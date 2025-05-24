package ru.pfr.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pfr.server.entity.UserEntity;
import java.util.Optional;
 
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
} 
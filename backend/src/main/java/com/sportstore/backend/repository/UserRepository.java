package com.sportstore.backend.repository;

import com.sportstore.backend.domain.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByUsername(String username);
  boolean existsByUsername(String username);
}

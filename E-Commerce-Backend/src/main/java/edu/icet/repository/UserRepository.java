package edu.icet.repository;

import edu.icet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User>findByEmail(String email);
}

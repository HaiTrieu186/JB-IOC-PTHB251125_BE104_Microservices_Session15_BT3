package re.edu.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import re.edu.identityservice.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}
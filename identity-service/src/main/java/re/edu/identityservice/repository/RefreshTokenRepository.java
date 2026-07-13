package re.edu.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import re.edu.identityservice.entity.RefreshToken;
import re.edu.identityservice.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByUser(User user);
}
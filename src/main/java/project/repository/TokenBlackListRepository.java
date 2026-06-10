package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.model.entity.RefreshToken;
import project.model.entity.TokenBlacklist;

@Repository
public interface TokenBlackListRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByTokenValue(String token);
}

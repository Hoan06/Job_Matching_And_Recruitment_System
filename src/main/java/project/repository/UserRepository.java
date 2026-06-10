package project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndActiveTrue(String email);
    Page<User> findAllByActiveTrue(Pageable pageable);
}

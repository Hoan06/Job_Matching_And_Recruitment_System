package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.model.entity.Application;
import project.model.entity.enum_type.ApplicationStatusEnum;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Application findByIdAndStatusNot(Long idApplication , ApplicationStatusEnum  applicationStatusEnum);
}

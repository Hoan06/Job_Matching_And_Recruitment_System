package project.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.entity.enum_type.ApplicationStatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;
    @Column(name = "cover_letter" , nullable = false)
    private String coverLetter;
    @Column(name = "cv_url" , nullable = false)
    private String cvUrl;
    @Column(name = "applied_at"  , nullable = false)
    private LocalDateTime appliedAt;
    private ApplicationStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_posting_id")
    private JobPosting jobPosting;
}

package project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.entity.enum_type.JobStatusEnum;

import java.util.List;

@Entity
@Table(name = "job_postings")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_posting_id")
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false , name = "salary_range")
    private String salaryRange;
    @Enumerated(EnumType.STRING)
    private JobStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "jobPosting")
    @JsonIgnore
    private List<Application> applications;
}

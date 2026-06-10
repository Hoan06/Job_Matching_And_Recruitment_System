package project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.entity.enum_type.RoleEnum;

import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(unique = true, nullable = false, length = 50)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    private boolean active;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<TokenBlacklist> tokenBlacklists;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Application> applications;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<JobPosting> jobPostings;
}

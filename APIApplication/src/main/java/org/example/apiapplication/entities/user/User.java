package org.example.apiapplication.entities.user;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.Assignment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String password;
    private String email;

    @Column(name = "forgot_password_token")
    private String forgotPasswordToken;

    @Column(name = "is_approved")
    private boolean isApproved;

    @Column(name = "refresh_token")
    private String refreshToken;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private UserInfo userInfo;

    @OneToMany(mappedBy = "userAssigned")
    private List<Assignment> assignments = new ArrayList<>();
}

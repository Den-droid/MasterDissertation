package org.apiapplication.entities.user;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.Group;
import org.apiapplication.entities.assignment.UserAssignment;

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
    private List<Role> roles;

    @OneToOne(mappedBy = "user")
    private UserInfo userInfo;

    @OneToMany(mappedBy = "user")
    private List<UserAssignment> userAssignments;

    @OneToMany(mappedBy = "user")
    private List<UserPermission> userPermissions;

    @ManyToMany(mappedBy = "students")
    private List<Group> groups;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.assignment.Function;
import org.apiapplication.entities.user.UserPermission;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "subjects")
@Data
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "subject")
    private List<Function> functions;

    @OneToMany(mappedBy = "subject")
    private List<UserPermission> userPermissions;

    @ManyToOne
    @JoinColumn(name = "university_id", referencedColumnName = "id")
    private University university;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Subject subject)) return false;
        return Objects.equals(getId(), subject.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}

package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;
import org.apiapplication.entities.maze.Maze;
import org.apiapplication.entities.user.UserInfo;
import org.apiapplication.entities.user.UserPermission;

import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "universities")
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "university")
    private List<Subject> subjects;

    @OneToMany(mappedBy = "university")
    private List<UserPermission> userPermissions;

    @OneToMany(mappedBy = "university")
    private List<UserInfo> userInfos;

    @OneToMany(mappedBy = "university")
    private List<DefaultAssignmentRestriction> defaultAssignmentRestrictions;

    @OneToMany(mappedBy = "university")
    private List<Maze> mazes;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof University that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}

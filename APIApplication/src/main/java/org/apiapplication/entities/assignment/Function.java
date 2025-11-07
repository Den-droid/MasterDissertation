package org.apiapplication.entities.assignment;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.user.UserPermission;

import java.util.List;

@Entity
@Table(name = "functions")
@Data
public class Function {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String text;
    @Column(name = "variables_count")
    private int variablesCount;

    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private Subject subject;

    @OneToMany(mappedBy = "function")
    private List<UserAssignment> userAssignments;

    @OneToMany(mappedBy = "function")
    private List<FunctionMinMaxValue> functionMinMaxValues;

    @OneToMany(mappedBy = "function")
    private List<UserPermission> userPermissions;

    @OneToMany(mappedBy = "function")
    private List<DefaultAssignmentRestriction> defaultAssignmentRestrictions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Function that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

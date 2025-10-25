package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "university", cascade = CascadeType.REMOVE)
    private List<DefaultAssignmentRestriction> defaultAssignmentRestrictions = new ArrayList<>();
}

package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;
import org.apiapplication.entities.assignment.Function;

import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "university_id", referencedColumnName = "id")
    private University university;

    @OneToMany(mappedBy = "subject")
    private List<DefaultAssignmentRestriction> defaultAssignmentRestrictions;

    @ManyToMany(mappedBy = "subjects")
    private List<Group> groups;
}

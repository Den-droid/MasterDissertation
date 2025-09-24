package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
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
    private List<Function> functions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "university_id", referencedColumnName = "id")
    private University university;
}

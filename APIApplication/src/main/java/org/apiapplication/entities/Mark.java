package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "marks")
@Data
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", referencedColumnName = "id")
    private Assignment assignment;

    private int mark;
    private String comment;
}

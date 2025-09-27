package org.apiapplication.entities.assignment;

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
    @JoinColumn(name = "user_assignment_id", referencedColumnName = "id")
    private UserAssignment userAssignment;

    private int mark;
    private String comment;
}

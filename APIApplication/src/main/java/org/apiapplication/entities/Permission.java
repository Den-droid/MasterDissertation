package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.user.User;

@Data
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "university_id", referencedColumnName = "id")
    private University university;

    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "function_id", referencedColumnName = "id")
    private Function function;

    @ManyToOne
    @JoinColumn(name = "assignment_id", referencedColumnName = "id")
    private Assignment assignment;
}

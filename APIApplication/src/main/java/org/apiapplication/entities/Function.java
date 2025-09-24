package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.enums.FunctionResultType;

@Entity
@Table(name = "functions")
@Data
public class Function {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String text;
    private String hint;

    @Column(name = "variables_count")
    private int variablesCount;

    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private Subject subject;

    private String correctValues;
    private FunctionResultType resultType;
}

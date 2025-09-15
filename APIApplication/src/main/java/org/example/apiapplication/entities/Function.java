package org.example.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.enums.FunctionResultType;

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
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    private String minValue;
    private String maxValue;
    private FunctionResultType resultType;
}

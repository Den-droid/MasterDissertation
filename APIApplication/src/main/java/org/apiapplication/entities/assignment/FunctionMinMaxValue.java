package org.apiapplication.entities.assignment;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.enums.FunctionResultType;

@Entity
@Table(name = "function_min_max_values")
@Data
public class FunctionMinMaxValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private double value;
    private FunctionResultType functionResultType;

    @ManyToOne
    @JoinColumn(name = "function_id", referencedColumnName = "id")
    private Function function;
}

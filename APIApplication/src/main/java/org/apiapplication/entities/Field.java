package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.enums.FieldType;

import java.util.List;

@Data
@Entity
@Table(name = "fields")
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String label;
    private String description;
    private FieldType type;

    @OneToMany(mappedBy = "field")
    private List<UrlField> urlFields;
}

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
    private boolean required;

    @Column(name = "query_param")
    private boolean queryParam;

    @Column(name = "body_param")
    private boolean bodyParam;

    @ManyToMany(mappedBy = "fields")
    private List<Url> urls;
}

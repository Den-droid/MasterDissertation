package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.enums.MethodType;

import java.util.List;

@Data
@Entity
@Table(name = "urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String url;
    private String description;
    private MethodType method;

    @ManyToMany
    @JoinTable(
            name = "url_fields",
            joinColumns = @JoinColumn(name = "url_id"),
            inverseJoinColumns = @JoinColumn(name = "field_id"))
    private List<Field> fields;
}

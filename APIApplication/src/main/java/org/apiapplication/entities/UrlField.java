package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "url_fields")
public class UrlField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private boolean required;
    private boolean multiple;

    @ManyToOne
    @JoinColumn(name = "url_id", referencedColumnName = "id")
    private Url url;

    @ManyToOne
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    private Field field;
}

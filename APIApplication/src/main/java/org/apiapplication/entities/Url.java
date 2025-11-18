package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.user.Role;
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

    @OneToMany(mappedBy = "url")
    private List<UrlField> urlFields;

    @ManyToMany
    @JoinTable(
            name = "url_roles",
            joinColumns = @JoinColumn(name = "url_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
}

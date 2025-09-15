package org.example.apiapplication.entities.user;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "groups")
@Data
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "group")
    private List<User> users;
}

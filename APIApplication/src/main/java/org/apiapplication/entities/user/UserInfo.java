package org.apiapplication.entities.user;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_info")
@Data
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;
}

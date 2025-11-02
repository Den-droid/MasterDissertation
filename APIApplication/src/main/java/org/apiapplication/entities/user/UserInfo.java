package org.apiapplication.entities.user;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.University;

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

    @Column(name = "api_key")
    private String apiKey;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "university_id", referencedColumnName = "id")
    private University university;
}

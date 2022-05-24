package com.teethcare.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", length = 72, nullable = false)
    @NotEmpty
    private String username;

    @Column(name = "password", length = 72, nullable = false)
    @NotEmpty
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonManagedReference
    private Role role;

    @NotEmpty
    @Column(name = "first_name",length = 40, nullable = false)
    private String fistName;

    @NotEmpty
    @Column(name = "last_name",length = 10, nullable = false)
    private String lastName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "avatar_image")
    private String avatarImage;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private String status;
}

package com.teethcare.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "manager")
@PrimaryKeyJoinColumn(name = "account_id")
public class Manager extends Account {
    @Email
    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToOne(mappedBy = "manager")
    private Clinic clinic;
}

package com.teethcare.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "booking_detail")
public class BookingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    public BookingDetail() {
    }
}

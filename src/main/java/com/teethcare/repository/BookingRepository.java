package com.teethcare.repository;

import com.teethcare.model.entity.Booking;
import com.teethcare.model.entity.Clinic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.sql.Timestamp;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {

    List<Booking> findBookingByPatientId(int id, Sort sort);
    List<Booking> findBookingByClinic(Clinic clinic, Sort sort);
    List<Booking> findBookingByDentistId(int id, Sort sort);
    List<Booking> findBookingByClinic(Clinic id);
    List<Booking> findBookingByStatusAndExaminationTimeAndDentistId(String status, Timestamp examinationTime, int dentistId);
    Page<Booking> findAll(Specification<Booking> bookingSpecification, Pageable pageable);
    Booking findBookingById(int id);
    List<Booking> findAllBookingByClinicIdAndDesiredCheckingTimeBetweenOrExaminationTimeBetween(int clinicId,
    Booking findBookingByPreBookingId(int id);
}

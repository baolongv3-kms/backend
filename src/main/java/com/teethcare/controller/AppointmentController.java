package com.teethcare.controller;

import com.teethcare.common.Constant;
import com.teethcare.common.EndpointConstant;
import com.teethcare.common.Status;
import com.teethcare.mapper.BookingMapper;
import com.teethcare.model.entity.Appointment;
import com.teethcare.model.request.AppointmentFilterRequest;
import com.teethcare.model.request.AppointmentRequest;
import com.teethcare.model.response.AppointmentResponse;
import com.teethcare.service.AppointmentService;
import com.teethcare.utils.PaginationAndSortFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = EndpointConstant.Appointment.APPOINTMENT_ENDPOINT)

public class AppointmentController {

    private final BookingMapper bookingMapper;
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAuthority(T(com.teethcare.common.Role).CUSTOMER_SERVICE)")
    public ResponseEntity<AppointmentResponse> add(@Valid @RequestBody AppointmentRequest appointmentRequest) {
        Appointment appointment = appointmentService.createAppointment(appointmentRequest);
        AppointmentResponse appointmentResponse = bookingMapper.mapAppointmentToAppointmentResponse(appointment);
        return new ResponseEntity<>(appointmentResponse, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority(T(com.teethcare.common.Role).CUSTOMER_SERVICE, T(com.teethcare.common.Role).DENTIST, T(com.teethcare.common.Role).MANAGER, T(com.teethcare.common.Role).PATIENT)")
    public ResponseEntity<Page<AppointmentResponse>> getAll(@RequestParam(name = "page", required = false, defaultValue = Constant.PAGINATION.DEFAULT_PAGE_NUMBER) int page,
                                                            @RequestParam(name = "size", required = false, defaultValue = Constant.PAGINATION.DEFAULT_PAGE_SIZE) int size,
                                                            @RequestParam(name = "sortBy", required = false, defaultValue = Constant.SORT.DEFAULT_SORT_BY) String field,
                                                            @RequestParam(name = "sortDir", required = false, defaultValue = Constant.SORT.DEFAULT_SORT_DIRECTION) String direction,
                                                            AppointmentFilterRequest appointmentFilterRequest,
                                                            @RequestHeader(AUTHORIZATION) String token) {
        Pageable pageable = PaginationAndSortFactory.getPagable(size, page, field, direction);
        Page<Appointment> appointments = appointmentService.findAllWithFilter(token.substring("Bearer ".length()), pageable, appointmentFilterRequest);
        Page<AppointmentResponse> appointmentResponses = appointments.map(bookingMapper::mapAppointmentToAppointmentResponse);
        return new ResponseEntity<>(appointmentResponses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority(T(com.teethcare.common.Role).CUSTOMER_SERVICE, T(com.teethcare.common.Role).DENTIST, T(com.teethcare.common.Role).MANAGER, T(com.teethcare.common.Role).PATIENT)")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable("id") int appointmentId) {
        Appointment appointment = appointmentService.findAppointmentById(appointmentId);
        return new ResponseEntity<>(bookingMapper.mapAppointmentToAppointmentResponse(appointment), HttpStatus.OK);
    }

//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority(T(com.teethcare.common.Role).CUSTOMER_SERVICE)")
//    public ResponseEntity<AppointmentResponse> getById(@PathVariable("id") int appointmentId) {
//        Appointment appointment = appointmentService.findById(appointmentId);
//        return new ResponseEntity<>(bookingMapper.mapAppointmentToAppointmentResponse(appointment), HttpStatus.OK);
//    }
}

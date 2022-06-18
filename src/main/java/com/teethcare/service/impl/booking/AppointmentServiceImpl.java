package com.teethcare.service.impl.booking;

import com.teethcare.common.Role;
import com.teethcare.common.Status;
import com.teethcare.config.security.JwtTokenUtil;
import com.teethcare.exception.BadRequestException;
import com.teethcare.exception.NotFoundException;
import com.teethcare.model.entity.*;
import com.teethcare.model.request.AppointmentFilterRequest;
import com.teethcare.model.request.AppointmentRequest;
import com.teethcare.model.request.AppointmentUpdateRequest;
import com.teethcare.repository.AppointmentRepository;
import com.teethcare.repository.ServiceRepository;
import com.teethcare.service.*;
import com.teethcare.utils.ConvertUtils;
import com.teethcare.utils.PaginationAndSortFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final BookingService bookingService;
    private final ServiceRepository serviceRepository;
    private final AppointmentRepository appointmentRepository;
    private final AccountService accountService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ManagerService managerService;
    private final ClinicService clinicService;
    private final DentistService dentistService;
    private final CSService csService;

    @Override
    @Transactional
    public Appointment createAppointment(String jwtToken, AppointmentRequest appointmentRequest) {
        Appointment appointment = new Appointment();
        String username = jwtTokenUtil.getUsernameFromJwt(jwtToken);
        Account account = accountService.getAccountByUsername(username);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        appointment.setCreateBookingDate(now);
        if (appointmentRequest.getAppointmentDate() - now.getTime() < 0) {
            throw new BadRequestException("Appointment Date invalid");
        }
        appointment.setAppointmentDate(ConvertUtils.getTimestamp(appointmentRequest.getAppointmentDate()));
        if (appointmentRequest.getExpirationAppointmentDate().compareTo(appointmentRequest.getAppointmentDate()) < 0) {
            throw new BadRequestException("Expiration Appointment Date invalid");
        }
        appointment.setExpireAppointmentDate(ConvertUtils.getTimestamp(appointmentRequest.getExpirationAppointmentDate()));

        Booking preBooking = bookingService.findBookingById(appointmentRequest.getPreBookingId());
        if (preBooking == null) {
            throw new NotFoundException("Booking ID" + appointmentRequest.getPreBookingId() + " not found!");
        }
        appointment.setPreBooking(preBooking);
        if (appointmentRequest.getServiceId() != null) {
            ServiceOfClinic service = serviceRepository.findByIdAndStatus(appointmentRequest.getServiceId(), Status.Service.ACTIVE.name());
            List<ServiceOfClinic> services = new ArrayList<>();
            services.add(service);
            appointment.setServices(services);
            appointment.setTotalPrice(service.getPrice());
        }

        appointment.setPatient(preBooking.getPatient());

        appointment.setCustomerService((CustomerService) account);

        appointment.setNote(appointment.getNote());

        appointment.setClinic(preBooking.getClinic());
        save(appointment);
        return appointment;
    }

    @Override
    public Appointment findAppointmentById(int id) {
        Appointment appointment = appointmentRepository.findByStatusInAndId(Status.Appointment.getNames(), id);
        if (appointment == null) {
            throw new NotFoundException("Appointment ID " + id + " not found!");
        }
        return appointment;
    }

    @Override
    public Page<Appointment> findAllWithFilter(String jwtToken, Pageable pageable, AppointmentFilterRequest appointmentFilterRequest) {
        String username = jwtTokenUtil.getUsernameFromJwt(jwtToken);
        Account account = accountService.getAccountByUsername(username);
        List<Appointment> appointments;
        switch (Role.valueOf(account.getRole().getName())) {
            case MANAGER:
                Manager manager = managerService.findById(account.getId());
                Clinic clinic = clinicService.getClinicByManager(manager);
                appointments = appointmentRepository.findAllByStatusInAndClinicId(Status.Appointment.getNames(), clinic.getId(), pageable.getSort());
                appointments = appointments.stream().filter(appointmentFilterRequest.getPredicate()).collect(Collectors.toList());
                break;

            case CUSTOMER_SERVICE:
                CustomerService customerService = csService.findById(account.getId());
                appointments = appointmentRepository.findAllByStatusInAndClinicId(Status.Appointment.getNames(), customerService.getClinic().getId(), pageable.getSort());
                appointments = appointments.stream().filter(appointmentFilterRequest.getPredicate()).collect(Collectors.toList());
                break;

            case DENTIST:
                Dentist dentist = dentistService.findById(account.getId());
                appointments = appointmentRepository.findAllByStatusInAndClinicId(Status.Appointment.getNames(), dentist.getClinic().getId(), pageable.getSort());
                appointments = appointments.stream().filter(appointmentFilterRequest.getPredicate()).collect(Collectors.toList());
                break;

            case PATIENT:
                appointments = appointmentRepository.findAllByStatusInAndPatientId(Status.Appointment.getNames(), account.getId(), pageable.getSort());
                appointments = appointments.stream().filter(appointmentFilterRequest.getPredicate()).collect(Collectors.toList());
                break;

            default:
                return null;
        }
        return PaginationAndSortFactory.convertToPage(appointments, pageable);
    }

    @Override
    public List<Appointment> findAll() {
        return null;
    }

    @Override
    public Appointment findById(int id) {
        return null;
    }


    @Override
    public void save(Appointment theEntity) {
        theEntity.setStatus(Status.Appointment.ACTIVE.name());
        appointmentRepository.save(theEntity);
    }

    @Override
    public void delete(int theId) {

    }

    @Override
    public void deleteByCSAndId(String jwtToken, int id) {
        String username = jwtTokenUtil.getUsernameFromJwt(jwtToken);
        Account account = accountService.getAccountByUsername(username);
        Appointment appointment = appointmentRepository.findByStatusIsAndCustomerServiceIdAndId(Status.Appointment.ACTIVE.name(), account.getId(), id);
        if (appointment == null) {
            throw new NotFoundException("Appointment ID " + id + " not found!");
        }
        appointment.setStatus(Status.Appointment.INACTIVE.name());
        update(appointment);
    }

    @Override
    public void update(Appointment theEntity) {
        appointmentRepository.save(theEntity);
    }

    public void updateByCSAndId(String jwtToken, int id, AppointmentUpdateRequest appointmentUpdateRequest) {
        String username = jwtTokenUtil.getUsernameFromJwt(jwtToken);
        Account account = accountService.getAccountByUsername(username);
        Appointment appointment = appointmentRepository.findByStatusIsAndCustomerServiceIdAndId(Status.Appointment.ACTIVE.name(), account.getId(), id);
        if (appointment == null) {
            throw new NotFoundException("Appointment ID " + id + " not found!");
        }
        if (appointmentUpdateRequest.getNote() != null) {
            appointment.setNote(appointmentUpdateRequest.getNote());
        }
        if (appointmentUpdateRequest.getAppointmentDate() != null) {
            appointment.setAppointmentDate(ConvertUtils.getTimestamp(appointmentUpdateRequest.getAppointmentDate()));
        }
        if (appointmentUpdateRequest.getExpirationAppointmentDate() != null) {
            appointment.setAppointmentDate(ConvertUtils.getTimestamp(appointmentUpdateRequest.getExpirationAppointmentDate()));
        }
        update(appointment);
    }
}

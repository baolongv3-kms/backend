package com.teethcare.service;

import com.teethcare.common.Status;
import com.teethcare.exception.IdNotFoundException;
import com.teethcare.model.entity.Clinic;
import com.teethcare.model.entity.Location;
import com.teethcare.model.entity.Manager;
import com.teethcare.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {
    private final ClinicRepository clinicRepository;

    @Override
    public List<Clinic> findAll() {
        return clinicRepository.findAll();
    }


    public List<Clinic> findAllActive(Pageable pageable) {
        return clinicRepository.getClinicByStatus(Status.ACTIVE.name(), pageable);
    }

    public Clinic getClinicByManager(Manager manager) {
        return clinicRepository.getClinicByManager(manager);
    }

    @Override
    public Clinic findById(int theId) {
        Optional<Clinic> result = clinicRepository.findById(theId);
        if (result.isEmpty()) {
            throw new IdNotFoundException("Clinic id " + theId + " not found!");
        }
        return result.get();
    }

    @Override
    public List<Clinic> searchAllActiveByName(String search, Pageable pageable) {
        return clinicRepository.findAllByNameContainingIgnoreCaseAndStatus(search, Status.ACTIVE.name(), pageable);
    }

    @Override
    public List<Clinic> findAllPendingClinic() {
        return clinicRepository.getClinicByStatus(Status.PENDING.name());
    }

    @Override
    public void save(Clinic clinic) {
        clinic.setStatus(Status.PENDING.name());
        clinicRepository.save(clinic);
    }

    @Override
    @Transactional
    public void saveWithManagerAndLocation(Clinic clinic, Manager manager, Location location) {
        clinic.setManager(manager);
        clinic.setLocation(location);
        clinic.setStatus(Status.PENDING.name());
        clinicRepository.save(clinic);
    }

    @Override
    public void delete(int id) {
        Optional<Clinic> clinicData = clinicRepository.findById(id);
        Clinic clinic = clinicData.get();
        clinic.setStatus(Status.INACTIVE.name());
        clinicRepository.save(clinic);
    }

}

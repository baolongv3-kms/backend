package com.teethcare.service;

import java.util.List;
import java.util.Optional;


public interface CRUDService<T> {
    List<T> findAll();

    Optional<T> findById(Integer id);

    void save(T theEntity);

    void deleteById(int theId);
}

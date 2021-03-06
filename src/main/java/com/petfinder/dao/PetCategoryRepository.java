package com.petfinder.dao;

import com.petfinder.domain.PetCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface PetCategoryRepository
        extends CrudRepository<PetCategory, Long> {

    @Override
    List<PetCategory> findAll();

    @Transactional
    List<PetCategory> findByName(String name);

    @Transactional
    List<PetCategory> findByNameContaining(String name);

    @Transactional
    Page<PetCategory> findByName(String name, Pageable pageable);
}

package com.petfinder.dao;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;

import com.petfinder.domain.Advertisement;
import com.petfinder.domain.Report;
import java.util.List;

public interface ReportRepository
        extends CrudRepository<Report, Long> {

    @Transactional
    List<Advertisement> findByAdvertisement(Advertisement advertisement); 
}

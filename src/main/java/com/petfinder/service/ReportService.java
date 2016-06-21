package com.petfinder.service;

import com.petfinder.dao.AdvertisementRepository;
import com.petfinder.dao.ReportRepository;
import com.petfinder.dao.UserRepository;
import com.petfinder.domain.Advertisement;
import com.petfinder.domain.Report;
import com.petfinder.domain.User;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Autowired
    UserService userService;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AdvertisementRepository advertisementRepository;

    @Transactional
    public Report addReport(Long idAdd) {
        Report report = null;
        User user = userRepository.findOneByLogin(userService.getLoggedUserName());
        Advertisement advertisement = advertisementRepository.findOne(idAdd);
        List<Advertisement> advertisementRaport = reportRepository.findByAdvertisement(advertisement);

        if (user != null && advertisement != null && advertisementRaport.isEmpty()) {
            report = new Report(advertisement, user);
            reportRepository.save(report);
        }
        return report;
    }

}

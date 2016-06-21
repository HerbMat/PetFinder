package com.petfinder.controller;

import com.petfinder.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportController {

    @Autowired
    ReportService reportService;

    @RequestMapping(value = "/addReport")
    public String addReport(@RequestParam(required = false) Long idAdd, Model model) {
        reportService.addReport(idAdd);
        return "redirect:/latestCode/2";
    }
}

package com.example.jobportal.company.controller;

import com.example.jobportal.company.entity.Company;
import com.example.jobportal.company.service.CompanyService;
import com.example.jobportal.user.dto.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> create(@RequestBody Company company){
        companyService.createCompany(company);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Company created successfully")
                .build();
        return ResponseEntity.ok(responseMessage);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> update(@RequestBody Company company){
        companyService.updateCompany(company);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Company updated successfully")
                .build();
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/getCompany/{id}")
    public ResponseEntity<Company> getCompany(@PathVariable String id){
        Company company = companyService.getCompany(id);
        return ResponseEntity.ok(company);
    }

    @GetMapping("/getCompany")
    public ResponseEntity<Company> getCompany(){
        Company company = companyService.getCompanyByUser();
        return ResponseEntity.ok(company);
    }
}

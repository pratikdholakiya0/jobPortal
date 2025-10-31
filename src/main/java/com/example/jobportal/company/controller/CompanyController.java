package com.example.jobportal.company.controller;

import com.example.jobportal.auth.service.JobPortalUserPrincipal;
import com.example.jobportal.company.entity.Company;
import com.example.jobportal.company.service.CompanyService;
import com.example.jobportal.user.dto.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> create(@RequestBody Company company, @AuthenticationPrincipal JobPortalUserPrincipal principal) {

        if (principal == null) {
            throw new AccessDeniedException("Authentication required to create company.");
        }

        companyService.createCompany(company, principal);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Company created successfully")
                .build();
        return ResponseEntity.ok(responseMessage);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> update(@RequestBody Company company, @AuthenticationPrincipal JobPortalUserPrincipal principal){

        if (principal == null) {
            throw new AccessDeniedException("Authentication required to update company.");
        }

        companyService.updateCompany(company, principal);

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
    public ResponseEntity<Company> getCompany(@AuthenticationPrincipal JobPortalUserPrincipal principal){

        if (principal == null) {
            throw new AccessDeniedException("Authentication required to retrieve company.");
        }

        Company company = companyService.getCompanyByUser(principal);
        return ResponseEntity.ok(company);
    }
}

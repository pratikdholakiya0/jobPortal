package com.example.jobportal.company.service;

import com.example.jobportal.auth.service.JobPortalUserPrincipal;
import com.example.jobportal.company.entity.Company;
import com.example.jobportal.company.repository.CompanyRepository;
import com.example.jobportal.exeptionHandler.customException.CompanyAlreadyRegisterByUser;
import com.example.jobportal.exeptionHandler.customException.CompanyNotFound;
import com.example.jobportal.user.entity.User;
import com.example.jobportal.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private JobPortalUserPrincipal getPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof JobPortalUserPrincipal) {
            return (JobPortalUserPrincipal) principal;
        }

        throw new AccessDeniedException("User authentication context is invalid or incomplete.");
    }

    public String getCurrentUserId(){
        return getPrincipal().getUserId();
    }

    public String getCurrentUserEmail(){
        return getPrincipal().getUsername();
    }

    public void createCompany(Company company){
        String userId = getCurrentUserId();
        Company existingCompany = companyRepository.findCompanyByUserId(userId);

        if(existingCompany!=null) throw new CompanyAlreadyRegisterByUser("Employer already has a registered company profile. Only one company is allowed per user.");

        company.setUserId(userId);
        companyRepository.save(company);
    }

    public Company getCompany(String id){
        Optional<Company> existingCompany = companyRepository.findById(id);
        if(existingCompany.isPresent()) return existingCompany.get();
        throw new CompanyNotFound("Company not found with this id : " + id);
    }

    public Company getCompanyByUser(){
        String userId = getCurrentUserId();
        String userEmail = getCurrentUserEmail();
        Company company = companyRepository.findCompanyByUserId(userId);
        if (company == null) throw new CompanyNotFound("Company not found with this email : " + userEmail);
        return company;
    }

    public void updateCompany(Company company){
        String userId = getCurrentUserId();
        Company companyDb = companyRepository.findCompanyByUserId(userId);

        if(companyDb == null) throw new CompanyNotFound("Company not found with this id : " + userId);

        if (company.getName() != null && !company.getName().isEmpty()) companyDb.setName(company.getName());
        if (company.getDescription() != null && !company.getDescription().isEmpty()) companyDb.setDescription(company.getDescription());
        if (company.getIndustry() != null && !company.getIndustry().isEmpty()) companyDb.setIndustry(company.getIndustry());
        if (company.getWebsite() != null && !company.getWebsite().isEmpty()) companyDb.setWebsite(company.getWebsite());
        if (company.getHeadquartersLocation()!=null && !company.getHeadquartersLocation().isEmpty()) companyDb.setHeadquartersLocation(company.getHeadquartersLocation());
        if (company.getLogoFileUrl()!=null && !company.getLogoFileUrl().isEmpty()) companyDb.setLogoFileUrl(company.getLogoFileUrl());

        companyRepository.save(companyDb);
    }
}

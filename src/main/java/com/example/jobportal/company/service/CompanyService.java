package com.example.jobportal.company.service;

import com.example.jobportal.company.entity.Company;
import com.example.jobportal.company.repository.CompanyRepository;
import com.example.jobportal.exeptionHandler.customException.CompanyAlreadyRegisterByUser;
import com.example.jobportal.exeptionHandler.customException.CompanyNotFound;
import com.example.jobportal.user.entity.User;
import com.example.jobportal.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public String getCurrentUserName(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public void createCompany(Company company){
        User user = userRepository.getUserByEmail(getCurrentUserName());
        Company existingCompany = companyRepository.findCompanyByUserId(user.getId());

        if(existingCompany!=null) throw new CompanyAlreadyRegisterByUser("Employer already has a registered company profile. Only one company is allowed per user.");

        company.setUserId(user.getId());
        companyRepository.save(company);
    }

    public Company getCompany(String id){
        Optional<Company> existingCompany = companyRepository.findById(id);
        if(existingCompany.isPresent()) return existingCompany.get();
        throw new CompanyNotFound("Company not found with this id : " + id);
    }

    public Company getCompanyByUser(){
        String email = getCurrentUserName();
        User user = userRepository.getUserByEmail(email);
        Company company = companyRepository.findCompanyByUserId(user.getId());
        if (company == null) throw new CompanyNotFound("Company not found with this email : " + email);
        return company;
    }

    public void updateCompany(Company company){
        User user = userRepository.getUserByEmail(getCurrentUserName());
        Company companyDb = companyRepository.findCompanyByUserId(user.getId());

        if(companyDb == null) throw new CompanyNotFound("Company not found with this id : " + user.getId());

        if (company.getName() != null && !company.getName().isEmpty()) companyDb.setName(company.getName());
        if (company.getDescription() != null && !company.getDescription().isEmpty()) companyDb.setDescription(company.getDescription());
        if (company.getIndustry() != null && !company.getIndustry().isEmpty()) companyDb.setIndustry(company.getIndustry());
        if (company.getWebsite() != null && !company.getWebsite().isEmpty()) companyDb.setWebsite(company.getWebsite());
        if (company.getHeadquartersLocation()!=null && !company.getHeadquartersLocation().isEmpty()) companyDb.setHeadquartersLocation(company.getHeadquartersLocation());
        if (company.getLogoFileUrl()!=null && !company.getLogoFileUrl().isEmpty()) companyDb.setLogoFileUrl(company.getLogoFileUrl());

        companyRepository.save(companyDb);
    }
}

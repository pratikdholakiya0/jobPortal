package com.example.jobportal.application.repository;

import com.example.jobportal.application.entity.Application;
import com.example.jobportal.job.entity.JobPosting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {


    List<Application> findAllByCompanyId(String companyId);

    Application findApplicationById(String id);

    Application getApplicationsByUserIdAndJobPostingId(String userId, String jobPostingId);

    List<Application> getApplicationsByUserId(String userId);

    Application getApplicationsById(String id);
}

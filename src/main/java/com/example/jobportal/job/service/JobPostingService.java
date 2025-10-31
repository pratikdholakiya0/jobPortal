package com.example.jobportal.job.service;

import com.example.jobportal.auth.service.JobPortalUserPrincipal;
import com.example.jobportal.company.entity.Company;
import com.example.jobportal.company.repository.CompanyRepository;
import com.example.jobportal.exeptionHandler.customException.CompanyNotFound;
import com.example.jobportal.exeptionHandler.customException.JobPostNotFound;
import com.example.jobportal.job.dto.JobPostingDto;
import com.example.jobportal.job.entity.JobPosting;
import com.example.jobportal.job.repository.JobPostingRepository;
import com.example.jobportal.user.entity.User;
import com.example.jobportal.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobPostingService {
    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public JobPosting createJobPosting(JobPostingDto jobPostingDto, JobPortalUserPrincipal principal) {
        String companyId = principal.getCompanyId();

        if (companyId == null) throw new CompanyNotFound("This user does not own any company");

        JobPosting jobPosting = JobPosting.builder()
                .companyId(companyId)
                .title(jobPostingDto.getTitle())
                .description(jobPostingDto.getDescription())
                .locationType(jobPostingDto.getLocationType())
                .city(jobPostingDto.getCity())
                .employmentType(jobPostingDto.getEmploymentType())
                .salaryRange(jobPostingDto.getSalaryRange())
                .postedDate(new Date(System.currentTimeMillis()))
                .deadline(jobPostingDto.getDeadline())
                .isActive(true)
                .isApproved(false)
                .build();
        return jobPostingRepository.save(jobPosting);
    }

    public JobPosting updateJobPosting(String jobId,JobPostingDto jobPostingDto, JobPortalUserPrincipal principal) {
        String companyId = principal.getCompanyId();

        JobPosting jobPosting = jobPostingRepository.findJobPostingById(jobId);
        if (jobPosting == null) throw new JobPostNotFound("Job posting not found");

        if (!jobPosting.getCompanyId().equals(companyId)){
            throw new AccessDeniedException("You are not authorized to update this job posting.");
        }

        if (jobPostingDto.getTitle()!=null) jobPosting.setTitle(jobPostingDto.getTitle());
        if (jobPostingDto.getDescription()!=null) jobPosting.setDescription(jobPostingDto.getDescription());
        if (jobPostingDto.getLocationType()!=null) jobPosting.setLocationType(jobPostingDto.getLocationType());
        if (jobPostingDto.getCity()!=null) jobPosting.setCity(jobPostingDto.getCity());
        if (jobPostingDto.getEmploymentType()!=null) jobPosting.setEmploymentType(jobPostingDto.getEmploymentType());
        if (jobPostingDto.getSalaryRange()!=null) jobPosting.setSalaryRange(jobPostingDto.getSalaryRange());
        if (jobPostingDto.getDeadline()!=null) jobPosting.setDeadline(jobPostingDto.getDeadline());
        jobPosting.setActive(jobPostingDto.isActive());
        jobPosting.setApproved(jobPostingDto.isApproved());

        return jobPostingRepository.save(jobPosting);
    }

    public List<JobPosting> findAllActiveJobs() {
        return jobPostingRepository.findAll().stream().filter(jobPosting -> jobPosting.isActive() && jobPosting.isApproved()).collect(Collectors.toList());
    }

    public JobPosting findJobPostById(String id) {
        JobPosting jobPosting = jobPostingRepository.getJobPostingById(id);
        if (jobPosting == null) throw new JobPostNotFound("Job post with id : " + id + " not found");
        return jobPosting;
    }
}

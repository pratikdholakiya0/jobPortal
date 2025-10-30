package com.example.jobportal.application.service;

import com.example.jobportal.application.dto.ApplicationStatusUpdate;
import com.example.jobportal.application.dto.ApplicationSubmissionRequest;
import com.example.jobportal.application.entity.Application;
import com.example.jobportal.application.entity.ApplicationActivity;
import com.example.jobportal.application.enums.ApplicationStatus;
import com.example.jobportal.application.repository.ApplicationActivityRepository;
import com.example.jobportal.application.repository.ApplicationRepository;
import com.example.jobportal.candidate.entity.CandidateProfile;
import com.example.jobportal.candidate.repository.CandidateProfileRepository;
import com.example.jobportal.company.entity.Company;
import com.example.jobportal.company.repository.CompanyRepository;
import com.example.jobportal.exeptionHandler.customException.ApplicationAlreadySubmited;
import com.example.jobportal.exeptionHandler.customException.ApplicationNotApplied;
import com.example.jobportal.exeptionHandler.customException.CandidateProfileNotCreated;
import com.example.jobportal.exeptionHandler.customException.JobPostNotFound;
import com.example.jobportal.job.entity.JobPosting;
import com.example.jobportal.job.repository.JobPostingRepository;
import com.example.jobportal.job.service.JobPostingService;
import com.example.jobportal.user.entity.User;
import com.example.jobportal.user.enums.Role;
import com.example.jobportal.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ApplicationService {
    @Autowired
    private JobPostingService jobPostingService;

    @Autowired
    private ApplicationActivityRepository applicationActivityRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateProfileRepository candidateProfileRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    public String getCurrentUserId(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByEmail(email);
        CandidateProfile candidateProfile = candidateProfileRepository.findCandidateProfileByUserId(user.getId());
        if(candidateProfile == null) throw new CandidateProfileNotCreated("Candidate Profile Not Created yet");
        return user.getId();
    }

    public String getEmployerCompanyId(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByEmail(email);
        Company company = companyRepository.findCompanyByUserId(user.getId());
        return company.getId();
    }

    private String getCandidateProfileId() {
        String userId = getCurrentUserId();
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new CandidateProfileNotCreated("Candidate profile required to apply. Please create your resume profile first."));
        return profile.getId();
    }

    public List<Application> getApplicationsByCandidate() {
        String candidateProfileId = getCandidateProfileId();
        List<Application> application = applicationRepository.getAllByCandidateProfileId(candidateProfileId);
        if(application == null) throw new ApplicationNotApplied("User have not applied to any application");
        return application;
    }

    public Application applyToJob(ApplicationSubmissionRequest  request) {
        String userId = getCurrentUserId();
        String candidateProfileId = getCandidateProfileId();

        JobPosting jobPosting = jobPostingRepository.findJobPostingById(request.getJobId());
        if (jobPosting == null) throw new JobPostNotFound("There is no job post with id : " + request.getJobId());

        CandidateProfile candidateProfile = candidateProfileRepository.findCandidateProfileByUserId(userId);
        if (candidateProfile == null) throw new CandidateProfileNotCreated("Candidate Profile Not Created yet");

        Application application = applicationRepository.getApplicationsByCandidateProfileIdAndJobPostingId(candidateProfileId, request.getJobId());
        if (application != null) throw new ApplicationAlreadySubmited("You have already applied to this job.");

        application = Application.builder()
                .candidateProfileId(candidateProfileId)
                .companyId(jobPosting.getCompanyId())
                .jobPostingId(request.getJobId())
                .coverLetterText(request.getCoverLetterText())
                .applicationDate(new Date(System.currentTimeMillis()))
                .status(ApplicationStatus.APPLIED)
                .build();

        Application savedApplication = applicationRepository.save(application);

        logApplicationActivity(savedApplication.getId(), ApplicationStatus.APPLIED, Role.APPLICANT, "Application submitted successfully.");

        return savedApplication;
    }

    public List<Application> getApplicationsByEmployer() {
        String employerCompanyId = getEmployerCompanyId();
        return applicationRepository.findAllByCompanyId(employerCompanyId);
    }

    public List<ApplicationActivity> getApplicationHistory(String applicationId) {
        return applicationActivityRepository.findAllByApplicationIdOrderByTimestampDesc(applicationId);
    }

    public Application updateApplicationStatus(ApplicationStatusUpdate applicationStatusUpdate) {
        String employerCompanyId = getEmployerCompanyId();
        Application application = applicationRepository.findApplicationById(applicationStatusUpdate.getApplicationId());
        if (application==null) throw new ApplicationNotApplied("Application Not Applied");

        JobPosting jobPosting = jobPostingRepository.getJobPostingById(application.getJobPostingId());
        if (!jobPosting.getId().equals(employerCompanyId)){
            throw new AccessDeniedException("User has no permission to update this application");
        }

        ApplicationStatus oldStatus = application.getStatus();
        ApplicationStatus newStatus = applicationStatusUpdate.getNewStatus();
        application.setStatus(applicationStatusUpdate.getNewStatus());
        Application updatedApplication = applicationRepository.save(application);
        logApplicationActivity(applicationStatusUpdate.getApplicationId(), newStatus, Role.EMPLOYER,
                String.format("Status changed from %s to %s.", oldStatus, newStatus.name()));

        return updatedApplication;
    }

    private void logApplicationActivity(String applicationId, ApplicationStatus status, Role changedBy, String note) {
        ApplicationActivity activity = ApplicationActivity.builder()
                .applicationId(applicationId)
                .status(status)
                .statusChangedBy(changedBy)
                .timestamp(new Date(System.currentTimeMillis()))
                .note(note)
                .build();
        applicationActivityRepository.save(activity);
    }
}

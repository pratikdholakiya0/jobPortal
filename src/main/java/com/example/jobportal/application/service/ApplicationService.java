package com.example.jobportal.application.service;

import com.example.jobportal.application.dto.ApplicationStatusUpdate;
import com.example.jobportal.application.dto.ApplicationSubmissionRequest;
import com.example.jobportal.application.entity.Application;
import com.example.jobportal.application.entity.ApplicationActivity;
import com.example.jobportal.application.enums.ApplicationStatus;
import com.example.jobportal.application.repository.ApplicationActivityRepository;
import com.example.jobportal.application.repository.ApplicationRepository;
import com.example.jobportal.auth.service.JobPortalUserPrincipal;
import com.example.jobportal.candidate.repository.ResumeRepository;
import com.example.jobportal.company.repository.CompanyRepository;
import com.example.jobportal.exeptionHandler.customException.ApplicationAlreadySubmited;
import com.example.jobportal.exeptionHandler.customException.ApplicationNotApplied;
import com.example.jobportal.exeptionHandler.customException.CandidateProfileNotCreated;
import com.example.jobportal.exeptionHandler.customException.JobPostNotFound;
import com.example.jobportal.job.entity.JobPosting;
import com.example.jobportal.job.repository.JobPostingRepository;
import com.example.jobportal.job.service.JobPostingService;
import com.example.jobportal.user.enums.Role;
import com.example.jobportal.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private ResumeRepository resumeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    public List<Application> getApplicationsByCandidate(JobPortalUserPrincipal principal) {
        if (principal.getResumeId()==null) throw new CandidateProfileNotCreated("Candidate Profile Not Created yet. Please complete your resume profile.");

        String userId = principal.getUserId();
        List<Application> applications = applicationRepository.getApplicationsByUserId(userId);
        if(applications == null || applications.isEmpty()) throw new ApplicationNotApplied("User have not applied to any application");
        return applications;
    }

    public void applyToJob(ApplicationSubmissionRequest  request, JobPortalUserPrincipal principal) {
        String userId = principal.getUserId();
        String resumeId = principal.getResumeId();

        if (resumeId == null) throw new CandidateProfileNotCreated("Candidate profile not created");

        JobPosting jobPosting = jobPostingRepository.findJobPostingById(request.getJobId());
        if (jobPosting == null) throw new JobPostNotFound("There is no job post with id : " + request.getJobId());

        Application application = applicationRepository.getApplicationsByUserIdAndJobPostingId(userId, request.getJobId());
        if (application != null) throw new ApplicationAlreadySubmited("You have already applied to this job.");

        application = Application.builder()
                .userId(userId)
                .companyId(jobPosting.getCompanyId())
                .jobPostingId(request.getJobId())
                .coverLetterText(request.getCoverLetterText())
                .applicationDate(new Date(System.currentTimeMillis()))
                .status(ApplicationStatus.APPLIED)
                .build();

        Application savedApplication = applicationRepository.save(application);

        logApplicationActivity(savedApplication.getId(), ApplicationStatus.APPLIED, Role.APPLICANT, "Application submitted successfully.");
    }

    public List<Application> getApplicationsByEmployer(JobPortalUserPrincipal principal) {
        String employerCompanyId = principal.getCompanyId();
        if (employerCompanyId == null)  throw new AccessDeniedException("Access denied: You must be associated with a company profile to view applications.");
        return applicationRepository.findAllByCompanyId(employerCompanyId);
    }

    public List<ApplicationActivity> getApplicationHistory(String applicationId, JobPortalUserPrincipal principal) {
        Application application = applicationRepository.getApplicationsById(applicationId);
        if (application==null) throw new ApplicationNotApplied("Application does not exist");
        if (!application.getUserId().equals(principal.getUserId())) throw new AccessDeniedException("You do not have permission to view application history.");
        return applicationActivityRepository.findAllByApplicationIdOrderByTimestampAsc(applicationId);
    }

    public void updateApplicationStatus(ApplicationStatusUpdate applicationStatusUpdate, JobPortalUserPrincipal principal) {
        String userId = principal.getUserId();
        String employerCompanyId = principal.getCompanyId();
        Role role = principal.getRole();

        Application application = applicationRepository.findApplicationById(applicationStatusUpdate.getApplicationId());
        if (application==null) throw new ApplicationNotApplied("Application does not exist");

        ApplicationStatus newStatus = applicationStatusUpdate.getNewStatus();
        boolean permissionGranted = false;

        if (role.equals(Role.ADMIN)) {
            permissionGranted = true;
        } else if (newStatus.equals(ApplicationStatus.WITHDRAWN)) {
            if (role.equals(Role.APPLICANT) && application.getUserId().equals(userId)) {
                permissionGranted = true;
            }
        } else if (role.equals(Role.EMPLOYER)) {
            if (employerCompanyId!=null && application.getCompanyId().equals(employerCompanyId)) {
                permissionGranted = true;
            }
        }

        if (!permissionGranted) {
            throw new AccessDeniedException("Access denied: You do not have permission to modify the status of this application.");
        }

        ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(applicationStatusUpdate.getNewStatus());
        applicationRepository.save(application);
        logApplicationActivity(applicationStatusUpdate.getApplicationId(), newStatus, Role.EMPLOYER,
                String.format("Status changed from %s to %s.", oldStatus, newStatus.name()));

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

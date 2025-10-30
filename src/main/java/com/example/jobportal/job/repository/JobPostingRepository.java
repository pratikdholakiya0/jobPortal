package com.example.jobportal.job.repository;

import com.example.jobportal.job.entity.JobPosting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface JobPostingRepository extends MongoRepository<JobPosting,String> {
    JobPosting getJobPostingById(String id);

    JobPosting findJobPostingById(String id);

    List<JobPosting> findAllByActiveAndApproved(boolean active, boolean approved);

    List<JobPosting> findAllByPostedDateIsBetween(Date postedDateAfter, Date postedDateBefore);
}

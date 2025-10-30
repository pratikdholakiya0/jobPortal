package com.example.jobportal.job.dto;

import com.example.jobportal.company.enums.JobType;
import com.example.jobportal.company.enums.LocationType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class JobPostingDto {
    private String title;
    private String description;
    private LocationType locationType;
    private String city;
    private JobType employmentType;
    private String salaryRange;
    private Date deadline;
    private boolean isActive;
    private boolean isApproved;
}

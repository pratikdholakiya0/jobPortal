package com.example.jobportal.candidate.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "candidate_profiles")
public class Resume {
    @Id
    private String id;
    @Indexed(unique = true)
    @NotBlank
    private String userId;
    private String bio;
    private List<Education> educationList;
    private List<Experience> experienceList;
    private List<Skill> skillList;
    private String resumeUrl;
    private boolean isSearchable;
}

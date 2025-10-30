package com.example.jobportal.candidate.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skill {
    // No @Id needed as it's embedded
    @NotBlank
    private String name;
    private String proficiencyLevel;
}
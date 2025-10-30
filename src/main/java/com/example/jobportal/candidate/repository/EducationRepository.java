package com.example.jobportal.candidate.repository;

import com.example.jobportal.candidate.entity.Education;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EducationRepository extends MongoRepository<Education, String> {
}

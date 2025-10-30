package com.example.jobportal.candidate.repository;

import com.example.jobportal.candidate.entity.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<Resume, String> {
    Optional<Resume> findByUserId(String userId);

    Resume getCandidateProfileByUserId(String userId);

    Resume getResumeByUserId(String userId);
}

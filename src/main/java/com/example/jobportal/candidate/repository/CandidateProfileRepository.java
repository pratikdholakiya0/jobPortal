package com.example.jobportal.candidate.repository;

import com.example.jobportal.candidate.entity.CandidateProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateProfileRepository extends MongoRepository<CandidateProfile, String> {
    Optional<CandidateProfile> findByUserId(String userId);

    CandidateProfile findCandidateProfileByUserId(String userId);

    Optional<CandidateProfile> getCandidateProfilesByUserId(String userId);

    CandidateProfile getCandidateProfileByUserId(String userId);
}

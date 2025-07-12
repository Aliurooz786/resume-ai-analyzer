package com.urooz.resume_ai_analyzer.service;

import com.urooz.resume_ai_analyzer.model.ResumeAnalysisResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Service
@Slf4j
public class ResumeService {

    public ResumeAnalysisResponse analyzeResume(MultipartFile resumeFile, String jobDescription) {
        log.info("Analyzing resume file: {} with job description", resumeFile.getOriginalFilename());

        // 🔸 Dummy logic for now (simulate AI)
        int score = 75;
        String[] strengths = {
                "Strong Java & Spring Boot skills",
                "Experience with REST APIs"
        };
        String[] suggestions = {
                "Add more projects related to microservices",
                "Include relevant keywords from job description"
        };

        return ResumeAnalysisResponse.builder()
                .matchScore(score)
                .strengths(Arrays.asList(strengths))
                .suggestions(Arrays.asList(suggestions))
                .build();
    }
}
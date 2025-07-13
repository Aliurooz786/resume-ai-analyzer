package com.urooz.resumeanalyzer.controller;

import com.urooz.resumeanalyzer.dto.ResumeAnalysisResponse;
import com.urooz.resumeanalyzer.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/analyze")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(
            @RequestParam("resumeFile") MultipartFile resumeFile,
            @RequestParam("jobDescription") String jobDescription
    ) {
        if (resumeFile == null || resumeFile.isEmpty()) {
            log.error("Resume file is missing or empty!");
            return ResponseEntity.badRequest().build();
        }

        log.info("Received resume: {}", resumeFile.getOriginalFilename());
        log.info("Job Description: {}", jobDescription);

        ResumeAnalysisResponse response = resumeService.analyzeResume(resumeFile, jobDescription);
        return ResponseEntity.ok(response);
    }
}
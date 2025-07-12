package com.urooz.resume_ai_analyzer.controller;

import com.urooz.resume_ai_analyzer.model.ResumeAnalysisResponse;
import com.urooz.resume_ai_analyzer.service.ResumeService;
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
        log.info("Received resume: {} for analysis against JD", resumeFile.getOriginalFilename());

        ResumeAnalysisResponse response = resumeService.analyzeResume(resumeFile, jobDescription);

        return ResponseEntity.ok(response);
    }
}
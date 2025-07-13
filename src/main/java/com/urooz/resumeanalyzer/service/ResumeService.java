package com.urooz.resumeanalyzer.service;

import com.urooz.resumeanalyzer.dto.ResumeAnalysisResponse;
import com.urooz.resumeanalyzer.util.AIResponseParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final GeminiClientService geminiClientService;
    private final PdfParserService pdfParserService;

    public ResumeAnalysisResponse analyzeResume(MultipartFile resumeFile, String jobDescription) {
        String resumeText = pdfParserService.extractTextFromPdf(resumeFile);

        String aiRawResponse = geminiClientService.analyze(resumeText, jobDescription);

        return AIResponseParserUtil.parse(aiRawResponse);
    }
}
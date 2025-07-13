package com.urooz.resumeanalyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenAiClientService {

    @Value("${openai.api.key}")
    private String apiKey;

    public String analyzeResumeWithAI(String resumeText, String jobDescription) {

        resumeText = resumeText != null ? resumeText.substring(0, Math.min(resumeText.length(), 1500)) : "";
        jobDescription = jobDescription != null ? jobDescription.substring(0, Math.min(jobDescription.length(), 600)) : "";

        log.info("Resume char count (used): {}", resumeText.length());
        log.info("JD char count (used): {}", jobDescription.length());

        String prompt = String.format("""
Compare the following resume and job description. Return:
- Match Score (out of 100)
- 3 strengths
- 2 suggestions

Resume:
%s

Job Description:
%s
""", resumeText, jobDescription);

        // Prepare request body
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "max_tokens", 200,
                "temperature", 0.5,
                "messages", List.of(
                        Map.of("role", "system", "content", "You are an AI Resume Analyzer."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String endpoint = "https://api.openai.com/v1/chat/completions";

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("AI response received.");
            return response.getBody();

        } catch (HttpStatusCodeException e) {
            log.error("OpenAI API error: {}", e.getResponseBodyAsString());
            return "OpenAI API error: " + e.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("Exception while calling OpenAI API", e);
            return "Error: " + e.getMessage();
        }
    }
}
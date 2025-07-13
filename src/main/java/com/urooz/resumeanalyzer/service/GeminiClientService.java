package com.urooz.resumeanalyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urooz.resumeanalyzer.prompt.GeminiPromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiClientService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final GeminiPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyze(String resumeText, String jobDescription) {
        resumeText = resumeText != null ? resumeText.substring(0, Math.min(resumeText.length(), 1500)) : "";
        jobDescription = jobDescription != null ? jobDescription.substring(0, Math.min(jobDescription.length(), 600)) : "";

        log.info("Resume chars: {}, JD chars: {}", resumeText.length(), jobDescription.length());

        String prompt = promptBuilder.buildResumeComparisonPrompt(resumeText, jobDescription);

        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String response = responseEntity.getBody();
            log.info("Gemini raw response:\n{}", response);

            JsonNode root = objectMapper.readTree(response);
            JsonNode textNode = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text");

            if (textNode.isMissingNode()) {
                log.error("Missing 'text' field in Gemini response.");
                return "Error: Missing 'text' in response.";
            }

            String aiText = textNode.asText();
            log.info("Extracted AI text:\n{}", aiText);
            return aiText;

        } catch (Exception e) {
            log.error("Failed to call Gemini API or parse response", e);
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
}
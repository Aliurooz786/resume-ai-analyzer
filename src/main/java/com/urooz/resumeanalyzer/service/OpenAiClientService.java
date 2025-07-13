package com.urooz.resumeanalyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
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

        WebClient client = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "max_tokens", 200,
                "temperature", 0.5,
                "messages", List.of(
                        Map.of("role", "system", "content", "You are an AI Resume Analyzer."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        String response = client.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class).flatMap(error -> {
                            log.error("OpenAI API error: {}", error);
                            return Mono.error(new RuntimeException("OpenAI API error: " + error));
                        })
                )
                .bodyToMono(String.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests)
                )
                .block();

        log.info("AI response received.");
        return response;
    }
}
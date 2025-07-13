package com.urooz.resumeanalyzer.prompt;

import org.springframework.stereotype.Component;

@Component
public class GeminiPromptBuilder {

    public String buildResumeComparisonPrompt(String resumeText, String jobDescription) {
        return String.format("""
Compare the following resume and job description. Return:
- Match Score (out of 100)
- 3 strengths
- 2 improvement suggestions

Resume:
%s

Job Description:
%s
""", resumeText, jobDescription);
    }
}
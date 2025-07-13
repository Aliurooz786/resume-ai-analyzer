package com.urooz.resumeanalyzer.util;

import com.urooz.resumeanalyzer.dto.ResumeAnalysisResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AIResponseParserUtil {

    public static ResumeAnalysisResponse parse(String aiText) {
        int matchScore = extractMatchScore(aiText);

        List<String> allBullets = extractBullets(aiText);

        List<String> strengths = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        for (int i = 0; i < allBullets.size(); i++) {
            if (i < 3) strengths.add(allBullets.get(i));
            else suggestions.add(allBullets.get(i));
        }

        return ResumeAnalysisResponse.builder()
                .matchScore(matchScore)
                .strengths(strengths)
                .suggestions(suggestions)
                .build();
    }
    private static List<String> extractBullets(String text) {
        List<String> bullets = new ArrayList<>();

        Pattern bulletPattern = Pattern.compile("(?m)^\\s*\\d+\\.\\s+(.*)");
        Matcher matcher = bulletPattern.matcher(text);

        while (matcher.find()) {
            String bullet = matcher.group(1).trim();
            bullets.add(bullet);
        }

        return bullets;
    }

    private static int extractMatchScore(String text) {
        Pattern pattern = Pattern.compile("(?i)match score\\s*[:\\-]?\\s*(\\d+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private static List<String> tryExtractWithFallbacks(String text, List<String> possibleHeadings) {
        for (String heading : possibleHeadings) {
            log.info("Looking for heading: {}", heading);
            List<String> result = extractBulletPoints(text, heading);
            if (!result.isEmpty()) {
                return result;
            }
            log.warn("⚠️ Heading '{}' not found or empty.", heading);
        }
        return new ArrayList<>();
    }

    private static List<String> extractBulletPoints(String text, String headingKeyword) {
        List<String> points = new ArrayList<>();

        log.info("Looking for heading: {}", headingKeyword);

        // Match markdown-style headings and capture text after it until the next heading or end
        String patternStr = "(?i)(\\*{0,2}\\s*" + Pattern.quote(headingKeyword) + "\\s*\\*{0,2})[:：]?\\s*\\n{1,2}(.*?)(\\n{2,}|\\z)";
        Pattern sectionPattern = Pattern.compile(patternStr, Pattern.DOTALL);
        Matcher matcher = sectionPattern.matcher(text);

        if (matcher.find()) {
            String sectionBody = matcher.group(2).trim();
            log.info("Matched section for '{}'", headingKeyword);
            log.info("Section body:\n{}", sectionBody);

            String[] lines = sectionBody.split("\\n");

            for (String line : lines) {
                line = line.trim();

                // Remove leading markdown (e.g. "**" or bullets like "1." or "-")
                line = line.replaceAll("^\\*+", "");
                line = line.replaceAll("^[\\d]+\\.\\s*", "");
                line = line.replaceAll("^[-•]\\s*", "");

                if (!line.isBlank()) {
                    points.add(line);
                    log.info("Extracted bullet: {}", line);
                }
            }

        } else {
            log.warn("Heading '{}' not found or empty.", headingKeyword);
        }

        return points;
    }

}
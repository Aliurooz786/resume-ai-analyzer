#Resume AI Analyzer - Backend (Spring Boot)

A powerful backend service built with Spring Boot that uses **Gemini AI (Google)** to analyze resumes against job descriptions and return:
- Match Score (out of 100)
- Top 3 Strengths
- Top 2 Improvement Suggestions

## Features
- Upload PDF Resume and Job Description
- Resume text extraction using PDF parser
- AI-powered resume-job match using Gemini API
- Clean JSON response
- CORS configured for frontend integration

---

## Tech Stack
- Java 17
- Spring Boot 3.x
- Gemini (Google Generative AI)
- Apache PDFBox
- WebClient (Reactive)
- Maven

---

## Setup Instructions

### 1. Clone this Repository
```bash
git clone https://github.com/Aliurooz786/resume-ai-analyzer.git
cd resume-ai-analyzer

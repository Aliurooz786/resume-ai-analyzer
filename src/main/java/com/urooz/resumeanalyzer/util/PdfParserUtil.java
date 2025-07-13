package com.urooz.resumeanalyzer.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class PdfParserUtil {

    public static String extractTextFromPdf(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            log.info("PDF parsed successfully. Text length: {}", text.length());
            return text;

        } catch (IOException e) {
            log.error("Failed to parse PDF file: {}", e.getMessage());
            return "";
        }
    }
}
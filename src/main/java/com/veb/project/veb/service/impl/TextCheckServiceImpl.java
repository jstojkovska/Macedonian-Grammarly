package com.veb.project.veb.service.impl;

import com.veb.project.veb.service.TextCheckService;
import com.veb.project.veb.utils.TextUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class TextCheckServiceImpl implements TextCheckService {

    private static final String API_URL = "https://macedoniangrammarly.free.beeceptor.com/";

    @Override
    public List<Map<String, String>> checkText(String originalText) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("original", originalText);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, requestEntity, String.class);
            String rawText = response.getBody();

            if (rawText == null || !rawText.contains("correction")) {
                return List.of(Map.of("error", "Нема корекција."));
            }

            List<String> correctionValues = new ArrayList<>();
            for (String line : rawText.split("\n")) {
                if (line.trim().startsWith("\"correction\"")) {
                    String value = line.split(":", 2)[1].replace("\"", "").replace(",", "").trim();
                    correctionValues.add(value);
                }
            }

            String[] originalWords = originalText.split("\\s+");
            List<Map<String, String>> corrections = new ArrayList<>();

            for (String correction : correctionValues) {
                String normalizedCorrection = correction.replace(" ", "").toLowerCase();

                for (String word : originalWords) {
                    String normalizedWord = word.toLowerCase();

                    if (normalizedWord.equals(normalizedCorrection)) {
                        corrections.add(Map.of("wrong", word, "correct", correction));
                        break;
                    }

                    if (!normalizedWord.equals(correction.toLowerCase()) &&
                            normalizedWord.length() > 3 &&
                            correction.length() > 3 &&
                            TextUtils.levenshtein(normalizedWord, correction.toLowerCase()) <= 2) {
                        corrections.add(Map.of("wrong", word, "correct", correction));
                        break;
                    }
                }
            }

            if (corrections.isEmpty()) {
                return List.of(Map.of("error", "Не се најдени зборови за замена."));
            }

            return corrections;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(Map.of("error", "Грешка при поврзување со сервисот."));
        }
    }
}

package com.rasa.eventsync.business.service.impl;

import com.rasa.eventsync.business.service.SentimentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class SentimentServiceImpl implements SentimentService {

    private final RestTemplate restTemplate;
    private final String hfApiToken;

    public SentimentServiceImpl(RestTemplate restTemplate, @Value("${huggingface.api.token}") String hfApiToken) {
        this.restTemplate = restTemplate;
        this.hfApiToken = hfApiToken;
    }

    @Override
    public String analyzeSentiment(String text) {
        String url = "https://api-inference.huggingface.co/models/cardiffnlp/twitter-roberta-base-sentiment";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + hfApiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = Map.of("inputs", text);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.POST, entity, List.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> topPrediction = (Map<String, Object>) response.getBody().get(0);
            return (String) topPrediction.get("label");
        }

        throw new RuntimeException("Hugging Face sentiment analysis failed");
    }
}


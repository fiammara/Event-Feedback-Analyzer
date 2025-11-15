package com.rasa.eventsync.business.service.impl;

import com.rasa.eventsync.business.handlers.SentimentAnalysisException;
import com.rasa.eventsync.business.service.SentimentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class SentimentServiceImpl implements SentimentService {

    private static final Logger log = LoggerFactory.getLogger(SentimentServiceImpl.class);

    private final RestTemplate restTemplate;

    @Value("${HUGGINGFACE_TOKEN}")
    private String hfApiToken;

    public SentimentServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private void ensureToken() {
        if (hfApiToken == null || hfApiToken.isEmpty()) {
            hfApiToken = System.getenv("HUGGINGFACE_TOKEN");
            if (hfApiToken == null || hfApiToken.isEmpty()) {
                throw new IllegalStateException("HUGGINGFACE_TOKEN missing in environment");
            }
        }
    }

    @Override
    public String analyzeSentiment(String text) {
        ensureToken();
        String url = "https://router.huggingface.co/hf-inference/models/cardiffnlp/twitter-xlm-roberta-base-sentiment";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + hfApiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, Object> request = Map.of("inputs", text);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            Object responseBody = restTemplate.postForObject(url, entity, Object.class);

            if (responseBody instanceof List<?> outerList && !outerList.isEmpty()) {
                Object firstItem = outerList.get(0);

                if (firstItem instanceof List<?> predictionsList && !predictionsList.isEmpty()) {
                    Object topPredictionObj = predictionsList.get(0);
                    if (topPredictionObj instanceof Map<?, ?> topPrediction) {
                        return (String) topPrediction.get("label");
                    }
                } else if (firstItem instanceof Map<?, ?> topPredictionMap) {
                    return (String) topPredictionMap.get("label");
                }
            }

            throw new SentimentAnalysisException("Hugging Face sentiment analysis returned empty result");

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                log.warn("Client error during sentiment analysis for text '{}': {} - {}",
                    text, e.getStatusCode(), e.getResponseBodyAsString(), e);
            } else {
                log.error("Server error during sentiment analysis for text '{}': {} - {}",
                    text, e.getStatusCode(), e.getResponseBodyAsString(), e);
            }
            throw new SentimentAnalysisException(
                "HTTP error during sentiment analysis for text '" + text + "'; status: " + e.getStatusCode(), e
            );

        } catch (RestClientException | ClassCastException | IllegalStateException e) {
            log.error("Error during sentiment analysis for text '{}'", text, e);
            throw new SentimentAnalysisException(
                "Unexpected error during sentiment analysis for text '" + text + "'", e
            );
        }
    }
}


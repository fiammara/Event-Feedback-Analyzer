package com.rasa.eventsync;

import com.rasa.eventsync.business.service.impl.SentimentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SentimentServiceTests {
    @Mock
    private RestTemplate restTemplate;

    private SentimentServiceImpl sentimentService;

    @BeforeEach
    void setup() {

        sentimentService = Mockito.spy(new SentimentServiceImpl(restTemplate));
        ReflectionTestUtils.setField(sentimentService, "hfApiToken", "dummy-token");
    }

    @Test
    void analyzeSentiment_shouldReturnLabel_fromMapResponse() {

        Map<String, Object> prediction = Map.of("label", "POSITIVE", "score", 0.95);
        List<Object> response = List.of(prediction);

        when(restTemplate.postForObject(anyString(), any(), eq(Object.class)))
            .thenReturn(response);

        String result = sentimentService.analyzeSentiment("Great event!");

        assertEquals("POSITIVE", result);
        verify(restTemplate).postForObject(anyString(), any(), eq(Object.class));
    }

    @Test
    void analyzeSentiment_shouldReturnLabel_fromNestedListResponse() {
        Map<String, Object> prediction = Map.of("label", "NEGATIVE", "score", 0.7);
        List<Object> nestedList = List.of(prediction);
        List<Object> response = List.of(nestedList);

        when(restTemplate.postForObject(anyString(), any(), eq(Object.class)))
            .thenReturn(response);

        String result = sentimentService.analyzeSentiment("Bad event!");

        assertEquals("NEGATIVE", result);
    }

    @Test
    void analyzeSentiment_shouldThrowException_whenEmptyResponse() {
        when(restTemplate.postForObject(anyString(), any(), eq(Object.class)))
            .thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> sentimentService.analyzeSentiment("Test"));

        assertTrue(ex.getMessage().contains("empty result"));
    }

    @Test
    void analyzeSentiment_shouldThrowHttpClientErrorException() {
        when(restTemplate.postForObject(anyString(), any(), eq(Object.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request"));

        assertThrows(HttpClientErrorException.class,
            () -> sentimentService.analyzeSentiment("Test"));
    }

    @Test
    void analyzeSentiment_shouldThrowException_whenNullResponse() {
        when(restTemplate.postForObject(anyString(), any(), eq(Object.class)))
            .thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> sentimentService.analyzeSentiment("Test"));

        assertTrue(ex.getMessage().contains("empty result"));
    }
}


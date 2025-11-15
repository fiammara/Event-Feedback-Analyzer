package com.rasa.eventsync.business.handlers;

public class SentimentAnalysisException extends RuntimeException {

    public SentimentAnalysisException(String message) {
        super(message);
    }

    public SentimentAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}


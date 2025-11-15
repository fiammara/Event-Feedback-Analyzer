package com.rasa.eventsync.configs;

public final class HTMLResponseMessages {

    private HTMLResponseMessages() {}
    public static final String HTTP_200 = "Successful request gets this response code and returns requested data. " +
        "Example data can be seen below";

    public static final String HTTP_201 = "Successful request saves posted data and returns that data with this " +
        "response code. Example data can be seen below";

    public static final String HTTP_204 = "No content";

    public static final String HTTP_400 = "Unsuccessful request responds with this code. Passed data has errors - " +
        "fields have incorrect values or there are missing fields";

    public static final String HTTP_404 = "Unsuccessful request responds with this code. Requested resource has not " +
        "been found";

    public static final String HTTP_500 = "Unsuccessful request responds with this code. Internal server error. " +
        "Check the response headers for information";

}
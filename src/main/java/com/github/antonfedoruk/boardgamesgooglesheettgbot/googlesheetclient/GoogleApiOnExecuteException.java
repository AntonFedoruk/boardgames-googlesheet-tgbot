package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;

import java.io.IOException;

/**
 *  Exception with Google API.
 */
public class GoogleApiOnExecuteException extends IOException {
    private final HttpResponseException httpResponseException;
    public GoogleApiOnExecuteException(String message, HttpResponseException httpResponseException) {
        super(message);
        this.httpResponseException = httpResponseException;
    }

    public int getStatusCode (){
        return httpResponseException.getStatusCode();
    }
//    public GoogleApiOnExecuteException(String message, Throwable cause) {
//        super(message, cause);
//    }
}

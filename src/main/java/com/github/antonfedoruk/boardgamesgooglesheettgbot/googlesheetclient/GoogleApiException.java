package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient;

import java.io.IOException;

/**
 *  Exception with Google API.
 */
public class GoogleApiException extends IOException {
    public GoogleApiException(String message) {
        super(message);
    }

    public GoogleApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

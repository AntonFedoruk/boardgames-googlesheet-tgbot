package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient;

import java.io.IOException;

/**
 *  Exception with Google API.
 */
public class GoogleApiIOException extends IOException {
//    public GoogleApiIOException(String message) {
//        super(message);
//    }
    public GoogleApiIOException(String message, Throwable cause) {
        super(message, cause);
    }
}

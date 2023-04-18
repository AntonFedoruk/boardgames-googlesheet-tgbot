package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleApiException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * SheetsServiceUtil class that uses the {@link Credential} object to obtain an instance of {@link Sheets}
 * <p>
 * A Sheets object is used as a client for reading and writing through the API.
 */
@Slf4j
public class SheetsServiceUtil {
    private static final String APPLICATION_NAME = "Board Games Telegram Bot";

    public static Sheets getSheetsService(String clientId) throws GoogleApiException {
        log.trace("Method getSheetsService() invoked...");
        final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        Credential credential;
        try {
            credential = GoogleAuthorizeUtil.getGoogleAuthorizationCodeFlow().loadCredential(clientId);
        } catch (IOException e) {
            log.error("Exception occurs trying to load credential of user(" + clientId + ") -> because 'credential' is null. Signing needed.", e);
            throw new GoogleApiException("Failed to load credential of user(" + clientId + ")", e);
        }
        Sheets sheets = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
        log.trace("... getSheetsService() completed, sheets service obtained.");
        return sheets;
    }
}
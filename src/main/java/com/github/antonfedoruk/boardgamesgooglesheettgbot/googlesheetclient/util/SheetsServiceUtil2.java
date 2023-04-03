package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util.GAuthUtil;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * SheetsServiceUtil class that uses the {@link Credential} object to obtain an instance of {@link Sheets}
 *
 * A Sheets object is used as a client for reading and writing through the API.
 */
public class SheetsServiceUtil2 {
    private static final String APPLICATION_NAME = "Board Games Telegram Bot";

    public static Sheets getSheetsService(String clientId) throws IOException, GeneralSecurityException {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        Credential credential = GAuthUtil.getGoogleAuthorizationCodeFlow(HTTP_TRANSPORT).loadCredential(clientId);

        return new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
    }
}
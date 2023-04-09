package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleSheetClientImpl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * GoogleAuthorizeUtil class with a static authorize() method which reeds credentials and send the authorization request to Google API.
 */
public class GoogleAuthorizeUtil {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens/path";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Global instance of the scopes required for this APP('read' and 'update' spreadsheets)
     * - SPREADSHEETS: give access Google Sheets and using an in-memory DataStoreFactory to store the credentials received;
     * - DRIVE: give ability to see, edit, create, and delete all of your Google Drive files
     * <p>
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);

    /**
     * Creates an authorized Credential object. This will grant the application access to the Google Sheet.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public static GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow(final HttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets. From credential.json file out of /resources.
        InputStream in = GoogleSheetClientImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        // Use the authorization code flow to allow the end user to grant your application access to their protected data.}
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH))) //save our authorization token at TOKENS_DIRECTORY_PATH
                .setAccessType("offline")
                .build();
        return flow;
    }
}
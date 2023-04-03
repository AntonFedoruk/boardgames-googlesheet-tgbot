package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleSheetClientImpl;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
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
     *  - SPREADSHEETS: give access Google Sheets and using an in-memory DataStoreFactory to store the credentials received;
     *  - DRIVE: give ability to see, edit, create, and delete all of your Google Drive files
     *
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);

    /**
     * Creates an authorized Credential object. This will grant the application access to the Google Sheet.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @param clientId client's id in Google API.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public static Credential authorize(final NetHttpTransport HTTP_TRANSPORT, String clientId) throws IOException {
        // Load client secrets. From credential.json file out of /resources.
        InputStream in = GoogleSheetClientImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        // {Use the authorization code flow to allow the end user to grant your application access to their protected data.}
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH))) //save our authorization token at TOKENS_DIRECTORY_PATH
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver();
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        // Create HttpServer instance and start server on localhost with available port
        //HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        //server.start();
        //int port = server.getAddress().getPort();

        //// Build authorization URL with localhost and available port
        //String redirectUri = "http://localhost:" + port + "/authorize";

        //AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);

        //// Send user to authorization URL
        //System.out.println("Please visit this URL to authorize the application: ");
        //System.out.println(authorizationUrl.build());

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(clientId);
    }

//    private static String getAuthorizationCode() throws IOException, GeneralSecurityException {
//        // Load client secrets. From credential.json file out of /resources.
//        InputStream in = GoogleSheetClientImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//        if (in == null) {
//            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
//        }
//        GoogleClientSecrets clientSecrets =
//                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
//                .setAccessType("ofline")
//                .build();
//        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI);
//        )
//
//    }
}
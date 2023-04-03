package com.github.antonfedoruk.boardgamesgooglesheettgbot.controller;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util.GAuthUtil;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util.SheetsServiceUtil2;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("V2")
public class GOauthController2 {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport(); //this var makes API calls
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance(); //this var makes serialization and deserialization of response
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);

    @Value("${google.user.identify.key}")
    private String USER_IDENTIFY_KEY;

    @Value("${google.spreadsheets.id}")
    private String SPREADSHEET_ID;

    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;

    @Value("${google.credentials.folder.path}")
    private Resource credentialFolder;

    private GoogleAuthorizationCodeFlow flow;

    @PostConstruct
    public void init() throws IOException {
        flow = GAuthUtil.getGoogleAuthorizationCodeFlow(HTTP_TRANSPORT);
    }

    @GetMapping(value = "/")
    public String showHomePage() throws IOException {
        boolean isUserAuthenticated = false;

        Credential credential = flow.loadCredential(USER_IDENTIFY_KEY);
        if (credential != null) {
            System.out.println("credential is NOT NULL");
            System.out.println("credential.getExpirationTimeMilliseconds(): " + credential.getExpirationTimeMilliseconds());
            boolean tokenValid = credential.refreshToken();
            if (tokenValid) {
                isUserAuthenticated = true;
            }
        }
        return isUserAuthenticated ? "boardgames" : "index";
    }


    @GetMapping(value = "/googlesignin")
    public void doGoogleSignIn(HttpServletResponse response) throws IOException {
        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        String redirectUrl = url.setRedirectUri(CALLBACK_URI).setAccessType("offline").build();
        response.sendRedirect(redirectUrl);
    }

    @GetMapping(value = "/oauth")
    public String saveAuthorizationCode(HttpServletRequest request) throws IOException {
        String code = request.getParameter("code");
        if (code != null) {
            saveToken(code);
            return "boardgames.html";
        }
        return "index.html";
    }

    private void saveToken(String code) throws IOException {
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(CALLBACK_URI).execute();
        flow.createAndStoreCredential(response, USER_IDENTIFY_KEY);
    }

    @GetMapping(value = "/games")
    public String gameList() throws IOException {
//        Credential credential = flow.loadCredential(USER_IDENTIFY_KEY);
//        Sheets sheetsService = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), credential).setApplicationName("Board Games Telegram Bot").build();
        Sheets sheetsService = null;
        try {
//            sheetsService = SheetsServiceUtil2.getSheetsService(USER_IDENTIFY_KEY);
            sheetsService = SheetsServiceUtil2.getSheetsService(USER_IDENTIFY_KEY);
        } catch (GeneralSecurityException e) {
            System.out.println("error in controller");
            throw new RuntimeException(e);
        }

        // Build a new authorized API client service.
        final String range = "BoardGames!A2:E";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values != null) {
            System.out.println("games found: " + values.size());
        }
        return "games";
    }

    public Map<String, Game> getGamesFromGoogleSheet() throws IOException {
        // Build a new authorized API client service.
        final String range = "BoardGames!A2:E";
        ValueRange response = getSheetsService().spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();
        List<List<Object>> values = response.getValues();

        Map<String, Game> games = new HashMap<>();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Games found.");
            for (List row : values) {
                games.put((String) row.get(0), new Game((String) row.get(0), (String) row.get(1), (String) row.get(2), (String) row.get(3), (String) row.get(4)));
            }
        }
        return games;
    }

    public Sheets getSheetsService() throws IOException {
        Credential credential = flow.loadCredential(USER_IDENTIFY_KEY);
        return new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), credential).setApplicationName("Board Games Telegram Bot").build();
    }
}

package com.github.antonfedoruk.boardgamesgooglesheettgbot.controller;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util.GoogleAuthorizeUtil;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util.SheetsServiceUtil;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@ControllerAdvice
public class GOauthController {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport(); //this var makes API calls

    @Value("${google.user.identify.key}")
    private String USER_IDENTIFY_KEY;

    @Value("${google.spreadsheets.id}")
    private String SPREADSHEET_ID;

    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;

    private GoogleAuthorizationCodeFlow flow;

    @PostConstruct
    public void init() throws IOException {
        flow = GoogleAuthorizeUtil.getGoogleAuthorizationCodeFlow(HTTP_TRANSPORT);
    }

    @GetMapping(value = "/")
    public String showHomePage() throws IOException {
        boolean isUserAuthenticated = false;

        Credential credential = flow.loadCredential(USER_IDENTIFY_KEY);
        if (credential != null) {
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
            return "boardgames";
        }
        return "index";
    }

    private void saveToken(String code) throws IOException {
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(CALLBACK_URI).execute();
        flow.createAndStoreCredential(response, USER_IDENTIFY_KEY);
    }

    @GetMapping(value = "/games")
    public String gameList(Model model){
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(USER_IDENTIFY_KEY);
            // Build a new authorized API client service.
            final String range = "BoardGames!A2:E";
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            model.addAttribute("gamesCount", values.size());
        } catch (
                GoogleJsonResponseException e) {
            if (e.getStatusCode() == 403) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access to the resource is forbidden", e);
            } else {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving the game list", e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving the game list", e);
        }
        return "games";
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ModelAndView handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request){
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("index");
        return mav;
    }
}
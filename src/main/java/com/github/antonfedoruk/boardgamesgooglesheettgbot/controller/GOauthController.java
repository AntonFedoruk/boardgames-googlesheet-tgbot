package com.github.antonfedoruk.boardgamesgooglesheettgbot.controller;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.command.NoCommand;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleApiException;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util.GoogleAuthorizeUtil;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@ControllerAdvice
public class GOauthController {
    private static final Logger log = LoggerFactory.getLogger(NoCommand.class);
    private final String USER_IDENTIFY_KEY;
    private final String CALLBACK_URI;

    @Getter
    private GoogleAuthorizationCodeFlow flow;

    @Autowired
    private GoogleApiService googleApiService;

    public GOauthController(@Value("${google.user.identify.key}") String userIdentifyKey,
                            @Value("${google.oauth.callback.uri}") String callbackUri) {
        USER_IDENTIFY_KEY = userIdentifyKey;
        CALLBACK_URI = callbackUri;
    }

    @PostConstruct
    public void init() throws IOException {
        log.trace("@PostConstruct init() in GOauthController invoked to initialize GoogleAuthorizationCodeFlow.");
        flow = GoogleAuthorizeUtil.getGoogleAuthorizationCodeFlow();
    }

    @GetMapping(value = "/")
    public String showHomePage() throws IOException {
        log.trace("GetMapping for '/' page. Home page should be shown.");
        Credential credential = flow.loadCredential(USER_IDENTIFY_KEY);
        if (credential != null) {
            if (credential.refreshToken()) {
                log.trace("User IS authenticated -> redirect on: 'boardgames.html'.");
                return "boardgames";
            }
        }
        log.trace("User IS NOT authenticated -> redirect on: 'index.html'.");
        return "index";
    }

    @GetMapping(value = "/googlesignin")
    public void doGoogleSignIn(HttpServletResponse response) throws IOException {
        log.trace("GetMapping for '/googlesignin' page. User will be redirected to Google sign in page.");
        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        String redirectUrl = url.setRedirectUri(CALLBACK_URI).setAccessType("offline").build();
        response.sendRedirect(redirectUrl);
    }

    @GetMapping(value = "/oauth")
    public String saveAuthorizationCode(HttpServletRequest request) throws IOException {
        log.trace("GetMapping for '/oauth' page. This request contains authorization 'code' that should be saved for further token request to Google.");
        String code = request.getParameter("code");
        if (code != null) {
            saveToken(code);
            log.trace("Redirect on: 'boardgames.html'.");
            return "boardgames";
        }
        log.trace("Redirect on: 'index.html'.");
        return "index";
    }

    private void saveToken(String code) throws IOException {
        log.trace("Sending token request...");
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(CALLBACK_URI).execute();
        flow.createAndStoreCredential(response, USER_IDENTIFY_KEY);
        log.trace("Obtained response(that contain token) was saved to credential directory.");
    }

    @GetMapping(value = "/games")
    public String gameList(Model model) {
        log.trace("GetMapping for '/games' page.");
        try {
            Credential credential = flow.loadCredential(USER_IDENTIFY_KEY);
            if (credential != null) {
                if (credential.refreshToken()) {
                    log.trace("User IS authenticated -> access to games is available.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            int gamesCount = googleApiService.getGamesFromGoogleSheet().size();
            model.addAttribute("gamesCount", gamesCount);
            log.trace("Added 'gamesCount':'" + gamesCount + "' to Model.");
            // todo
//        } catch (GoogleJsonResponseException e) {
//            if (e.getStatusCode() == 403) {
//                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access to the resource is forbidden", e);
//            } else {
//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving the game list", e);
//            }
        } catch (GoogleApiException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving the game list", e);
        }
        log.trace("Redirect on: 'games.html'.");
        return "games";
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ModelAndView handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("index");
        log.trace("Redirect on: 'index.html'.");
        return mav;
    }
}
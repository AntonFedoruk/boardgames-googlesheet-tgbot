package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * Integration-level testing for {@link GoogleSheetClient}.
 */
@DisplayName("Integration-level testing for GoogleSheetClient")
@ActiveProfiles("test")
@SpringBootTest
class GoogleSheetClientIT {

    @Autowired
    private GoogleSheetClient googleSheetClient;

    @Test
    @DisplayName("Should retrieve all games from Google Sheet")
    void shouldRetrieveAllGamesFromGoogleSheet() throws IOException, GeneralSecurityException {
        // when
        Map<String, Game> gamesFromGoogleSheet = googleSheetClient.getGamesFromGoogleSheet();
        // then
        Assertions.assertEquals(56, gamesFromGoogleSheet.size());
    }

    @Test
    @DisplayName("Should update games location on Google Sheet")
    void shouldUpdateGamesLocationOnGoogleSheet() throws IOException, GeneralSecurityException {
        // given
        Map<String, Game> gamesFromGoogleSheet = googleSheetClient.getGamesFromGoogleSheet();
        String gamesId = "56";
        String previousLocation = gamesFromGoogleSheet.get(gamesId).getLastLocation();
        String newLocation = "test";
        // when
        String updatedLocation = googleSheetClient.updateGameLocation(gamesId, newLocation);
        // then
        Assertions.assertEquals(newLocation, updatedLocation);
        //after
        googleSheetClient.updateGameLocation(gamesId, previousLocation);
    }
}
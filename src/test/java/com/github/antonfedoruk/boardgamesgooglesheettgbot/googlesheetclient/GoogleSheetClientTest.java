package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
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
    @EnabledIf("isFileExists")
    @DisplayName("Should retrieve all games from Google Sheet")
    void shouldRetrieveAllGamesFromGoogleSheet() throws GoogleApiException {
        // when
        Map<String, Game> gamesFromGoogleSheet = googleSheetClient.getGamesFromGoogleSheet();
        // then
        Assertions.assertEquals(56, gamesFromGoogleSheet.size());
    }

    @Test
    @EnabledIf("isFileExists")
    @DisplayName("Should update games location on Google Sheet")
    void shouldUpdateGamesLocationOnGoogleSheet() throws GoogleApiException {
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

    boolean isFileExists() {
        String path = "tokens/path/StoredCredential";
        File file = new File(path);
        return file.exists();
    }
}
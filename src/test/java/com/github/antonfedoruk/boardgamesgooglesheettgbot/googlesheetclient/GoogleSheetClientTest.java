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

    @BeforeEach
    void init() throws GeneralSecurityException, IOException {
        googleSheetClient.initSheetsService();
    }

    @Test
    @DisplayName("Should retrieve all games from Google Sheet")
    void shouldRetrieveAllGamesFromGoogleSheet() throws IOException, GeneralSecurityException {
        // when
        Map<String, Game> gamesFromGoogleSheet = googleSheetClient.getGamesFromGoogleSheet();
        // then
        Assertions.assertEquals(55, gamesFromGoogleSheet.size());
    }
}
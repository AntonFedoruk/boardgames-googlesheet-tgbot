package com.github.antonfedoruk.boardgamesgooglesheettgbot.service;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.WinRecord;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleApiException;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleSheetClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GoogleApiService {
    @Getter
    private final String getPhotoPathname;

    private final GoogleSheetClient googleSheetClient;

    public GoogleApiService(@Value("${resources.gamescommand.photo.pathname}") String getPhotoPathname, GoogleSheetClient googleSheetClient) {
        this.getPhotoPathname = getPhotoPathname;
        this.googleSheetClient = googleSheetClient;
    }

    public Map<String, Game> getGamesFromGoogleSheet() throws GoogleApiException {
        log.trace("GoogleApiService's getGamesFromGoogleSheet() invoked...");
        Map<String, Game> gamesFromGoogleSheet = googleSheetClient.getGamesFromGoogleSheet();
        log.trace("...GoogleApiService's getGamesFromGoogleSheet() completed.");
        return gamesFromGoogleSheet;
    }

    public String updateGameLocation(String id, String newLocation) throws GoogleApiException {
        log.trace("GoogleApiService's updateGameLocation() invoked...");
        String updatedLocation = googleSheetClient.updateGameLocation(id, newLocation);
        log.trace("...GoogleApiService's updateGameLocation() completed.");
        return updatedLocation;
    }

    public boolean createNewSheetTab(String newTabName) throws GoogleApiException {
        log.trace("GoogleApiService's createNewSheetTab() invoked...");
        boolean isNewTabCreated = googleSheetClient.createNewSheetTab(newTabName);
        log.trace("...GoogleApiService's createNewSheetTab() completed.");
        return isNewTabCreated;
    }

    public boolean addWinRecordToTheSheet(String tableName, WinRecord gameResult) throws GoogleApiException {
        log.trace("GoogleApiService's addWinRecordToTheSheet() invoked...");
        boolean isWinRecordAdded = googleSheetClient.addWinRecordToTheSheet(tableName, gameResult);
        log.trace("...GoogleApiService's addWinRecordToTheSheet() completed.");
        return isWinRecordAdded;
    }

    public List<WinRecord> getWinRecordsForGameFromGoogleSheet(String game) throws GoogleApiException {
        log.trace("GoogleApiService's getWinRecordsForGameFromGoogleSheet() invoked...");
        List<WinRecord> winRecordsForGameFromGoogleSheet = googleSheetClient.getWinRecordsForGameFromGoogleSheet(game);
        log.trace("...GoogleApiService's getWinRecordsForGameFromGoogleSheet() completed.");
        return winRecordsForGameFromGoogleSheet;
    }
}

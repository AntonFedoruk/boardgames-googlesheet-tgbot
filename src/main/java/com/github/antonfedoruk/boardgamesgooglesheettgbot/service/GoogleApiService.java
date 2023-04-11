package com.github.antonfedoruk.boardgamesgooglesheettgbot.service;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.WinRecord;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleSheetClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

@Service
public class GoogleApiService {
    @Getter
    @Value("${resources.gamescommand.photo.pathname}")
    private String getPhotoPathname;

    @Autowired
    private GoogleSheetClient googleSheetClient;

    public Map<String, Game> getGamesFromGoogleSheet() throws GeneralSecurityException, IOException {
        return googleSheetClient.getGamesFromGoogleSheet();
    }

    public String updateGameLocation(String id, String newLocation) throws IOException, GeneralSecurityException {
       return googleSheetClient.updateGameLocation(id, newLocation);
    }

    public boolean createNewSheetTab(String newTabName) throws IOException, GeneralSecurityException {
        return googleSheetClient.createNewSheetTab(newTabName);
    }

    public boolean addWinRecordToTheSheet(String tableName, WinRecord gameResult) throws GeneralSecurityException, IOException {
        return googleSheetClient.addWinRecordToTheSheet(tableName, gameResult);
    }

    public List<WinRecord> getWinRecordsForGameFromGoogleSheet(String game) throws GeneralSecurityException, IOException {
        return googleSheetClient.getWinRecordsForGameFromGoogleSheet(game);
    }
}

package com.github.antonfedoruk.boardgamesgooglesheettgbot.service;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleSheetClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@Service
public class GoogleApiService {
    @Autowired
    private GoogleSheetClient googleSheetClient;

    public Map<String, Game> getGamesFromGoogleSheet() throws GeneralSecurityException, IOException {
        return googleSheetClient.getGamesFromGoogleSheet();
    }

    public String updateGameLocation(String id, String newLocation) throws IOException, GeneralSecurityException {
       return googleSheetClient.updateGameLocation(id, newLocation);
    }

//    public Map<Object, Object> readDataFromGoogleSheet() throws GeneralSecurityException, IOException {
//        return googleSheetClient.getDataFromSheet();
//    }
//
//    public GoogleSheetResponseDTO createGoogleSheet(GoogleSheetDTO request) throws GeneralSecurityException, IOException {
//        return googleSheetClient.createGoogleSheet(request);
//    }
//
//    public GoogleSheetResponseDTO writeValuesOnSheet(GoogleSheetDTO request, String spreadsheetId) throws GeneralSecurityException, IOException {
//        return googleSheetClient.writeValuesOnSheet(request, spreadsheetId);
//    }
}

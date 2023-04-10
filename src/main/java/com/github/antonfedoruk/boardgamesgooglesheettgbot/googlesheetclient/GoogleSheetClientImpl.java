package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util.SheetsServiceUtil;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@link GoogleSheetClient} interface.
 */
@Component
public class GoogleSheetClientImpl implements GoogleSheetClient {
    private final String SPREADSHEET_ID;

    @Value("${google.user.identify.key}")
    private String USER_IDENTIFY_KEY;

    private Sheets sheetsService;

    public GoogleSheetClientImpl(@Value("${google.spreadsheets.id}") String spreadsheetId) {
        SPREADSHEET_ID = spreadsheetId;
    }

//    public GoogleSheetResponseDTO createGoogleSheet(GoogleSheetDTO request) throws IOException {
//        Sheets service = sheetsService;
//
//        SpreadsheetProperties spreadsheetProperties = new SpreadsheetProperties();
//        spreadsheetProperties.setTitle(request.getSheetName());
//
//        SheetProperties sheetProperties = new SheetProperties();
//        sheetProperties.setTitle(request.getSheetName());
//
//        Sheet sheet = new Sheet().setProperties(sheetProperties);
//
//        Spreadsheet spreadsheet = new Spreadsheet().setProperties(spreadsheetProperties).setSheets(Collections.singletonList(sheet));
//
//        Spreadsheet createdResponse = service.spreadsheets().create(spreadsheet).execute();
//
//        GoogleSheetResponseDTO responseDTO = new GoogleSheetResponseDTO();
//
//        ValueRange valueRange = new ValueRange().setValues(request.getDataToBeUpdated());
//        service.spreadsheets().values().update(createdResponse.getSpreadsheetId(), "A1", valueRange).setValueInputOption("RAW").execute();
//
//        responseDTO.setSpreadSheetId(createdResponse.getSpreadsheetId());
//        responseDTO.setSpreadSheetUrl(createdResponse.getSpreadsheetUrl());
//        return responseDTO;
//    }

    /**
     * Set new game's location at spreadsheet.
     */
    @Override
    public String updateGameLocation(String id, String newLocation) throws IOException, GeneralSecurityException {
        if (sheetsService == null) {
            initSheetsService();
        }

        ValueRange location = new ValueRange().setValues(List.of(Collections.singletonList(newLocation)));

        String range = "BoardGames!E" + (Integer.parseInt(id) + 1);
        UpdateValuesResponse valuesResponse = sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, range, location)
                .setValueInputOption("RAW")
                .setIncludeValuesInResponse(true)
                .execute();

        return valuesResponse.getUpdatedData().getValues().get(0).get(0).toString();
    }

    @Override
    public void initSheetsService() throws GeneralSecurityException, IOException {
        this.sheetsService = SheetsServiceUtil.getSheetsService(USER_IDENTIFY_KEY);
    }

    /**
     * Retrieve the games and their property from the spreadsheet.
     */
    @Override
    public Map<String, Game> getGamesFromGoogleSheet() throws IOException, GeneralSecurityException {
        if (sheetsService == null) {
            initSheetsService();
        }

        // Build a new authorized API client service.
        final String range = "BoardGames!A2:E";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();
        List<List<Object>> values = response.getValues();

        Map<String, Game> games = new HashMap<>();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Games found.");
            for (List row : values) {
                if (row.get(0) != null)
                    games.put((String) row.get(0), new Game((String) row.get(0), (String) row.get(1), (String) row.get(2), (String) row.get(3), (String) row.get(4)));
            }
        }
        return games;
    }
}
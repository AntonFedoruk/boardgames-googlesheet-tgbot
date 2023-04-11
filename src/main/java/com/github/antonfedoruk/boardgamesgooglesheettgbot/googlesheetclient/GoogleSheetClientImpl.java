package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.WinRecord;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util.SheetsServiceUtil;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.*;

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

    public void initSheetsService() throws GeneralSecurityException, IOException {
        if (sheetsService == null) {
            this.sheetsService = SheetsServiceUtil.getSheetsService(USER_IDENTIFY_KEY);
        }
    }

    public boolean createNewSheetTab(String newTabName) throws IOException, GeneralSecurityException {
        initSheetsService();

        if (isSuchTableExist(newTabName)) {
            return true;
        } else {
            SheetProperties sheetProperties = new SheetProperties();
            sheetProperties.setTitle(newTabName);
            BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
            List<Request> requests = new ArrayList<>();
            requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(sheetProperties)));
            requestBody.setRequests(requests);
            sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID, requestBody).execute();

            addColumnNamesForEmptyWinnersTable(newTabName);
        }

        return isSuchTableExist(newTabName);
    }

    private boolean isSuchTableExist(String tableName) throws IOException {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(SPREADSHEET_ID).execute();
        List<Sheet> sheets = spreadsheet.getSheets();
        for (Sheet sheet : sheets) {
            if (sheet.getProperties().getTitle().contains(tableName)) {
                return true;
            }
        }
        return false;
    }

    private void addColumnNamesForEmptyWinnersTable(String tableName) throws IOException {
        ValueRange columnNames = new ValueRange()
                .setValues(Collections.singletonList(Arrays.asList("Дата", "Переможець", "Переможні очки", "Інші гравці", "Примітки")));
        AppendValuesResponse appendСolumnNames = sheetsService.spreadsheets().values()
                .append(SPREADSHEET_ID, String.format("%s!A1", tableName), columnNames)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
    }

    @Override
    public boolean addWinRecordToTheSheet(String tableName, WinRecord gameResult) throws GeneralSecurityException, IOException {
        initSheetsService();
        boolean isWinRecordAdded = false;

        ValueRange winRecord = new ValueRange()
                .setValues(Collections.singletonList(gameResult.summaryForWinRecord()));

        AppendValuesResponse appendWinRecord = sheetsService.spreadsheets().values()
                .append(SPREADSHEET_ID, String.format("%s!A1", tableName), winRecord)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();

        System.out.printf("%d rows appended.", appendWinRecord.getUpdates().getUpdatedRows());
        if (appendWinRecord.getUpdates().getUpdatedRows() == 1) {
            isWinRecordAdded = true;
        }
        return isWinRecordAdded;
    }

    @Override
    public String updateGameLocation(String id, String newLocation) throws IOException, GeneralSecurityException {
        initSheetsService();

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
    public Map<String, Game> getGamesFromGoogleSheet() throws IOException, GeneralSecurityException {
        initSheetsService();

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

    @Override
    public List<WinRecord> getWinRecordsForGameFromGoogleSheet(String game) throws GeneralSecurityException, IOException {
        initSheetsService();

        List<WinRecord> winRecords = new ArrayList<>();

        if (!isSuchTableExist(game)) {
            return winRecords;
        }

        final String range = game + "!A2:E";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No WinRecords found.");
        } else {
            System.out.println("WinRecords found.");
            for (List row : values) {
                if (row.get(0) != null)
                    winRecords.add(new WinRecord(LocalDate.parse((String) row.get(0)), (String) row.get(1), (String) row.get(2), (String) row.get(3), (String) row.get(4)));
            }
        }
        return winRecords;
    }
}
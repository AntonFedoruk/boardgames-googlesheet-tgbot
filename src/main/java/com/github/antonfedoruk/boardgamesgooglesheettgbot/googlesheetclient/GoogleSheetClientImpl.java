package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.WinRecord;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util.GoogleAuthorizeUtil;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util.SheetsServiceUtil;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Implementation of the {@link GoogleSheetClient} interface.
 */
@Slf4j
@Component
public class GoogleSheetClientImpl implements GoogleSheetClient {
    private final String SPREADSHEET_ID;
    private final String USER_IDENTIFY_KEY;

    private Sheets sheetsService;

    public GoogleSheetClientImpl(@Value("${google.spreadsheets.id}") String spreadsheetId,
                                 @Value("${google.user.identify.key}") String userIdentifyKey) {
        SPREADSHEET_ID = spreadsheetId;
        USER_IDENTIFY_KEY = userIdentifyKey;
    }

    public void initSheetsService() throws GoogleApiException {
        if (sheetsService == null) {
            log.trace("Initialization of Sheets service for user:'" + USER_IDENTIFY_KEY + "'.");
            try {
                Credential credential = GoogleAuthorizeUtil.getGoogleAuthorizationCodeFlow().loadCredential(USER_IDENTIFY_KEY);
                if (credential != null) {
                    if (credential.refreshToken()) {
                        log.trace("As credential.refreshToken()=" + credential.refreshToken() + " ---> sheetService CAN be initialized.");
                        this.sheetsService = SheetsServiceUtil.getSheetsService(USER_IDENTIFY_KEY);
                    }
                } else {
                    System.out.println("credential.refreshToken() in GoogleSheetClientImpl:  ");
                    log.trace("As credential.refreshToken() IS NULL ---> sheetService CAN'T be initialized.");
                }
            } catch (IOException e) {
                log.error("Exception occurs trying to load credential of user(" + USER_IDENTIFY_KEY + ") -> because 'credential' is null. Signing needed.", e);
                throw new GoogleApiException("Failed to load credential of user(" + USER_IDENTIFY_KEY + ")", e);
            }
        }
    }

    public boolean createNewSheetTab(String newTabName) throws GoogleApiException {
        log.trace("Method createNewSheetTab() invoked for table: '" + newTabName + "'...");
        initSheetsService();

        if (isSuchTableExist(newTabName)) {
            log.trace("Table with name '" + newTabName + "' already exists.");
            return true;
        } else {
            SheetProperties sheetProperties = new SheetProperties();
            sheetProperties.setTitle(newTabName);
            BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
            List<Request> requests = new ArrayList<>();
            requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(sheetProperties)));
            requestBody.setRequests(requests);
            try {
                sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID, requestBody).execute();
                log.trace("Added new table '" + newTabName + "' to the spreadsheet(" + SPREADSHEET_ID + ").");
            } catch (IOException e) {
                log.error("Exception occurs trying to add new table '" + newTabName + "' to the spreadsheet(" + SPREADSHEET_ID + ").", e);
                throw new GoogleApiException("Failed to add a new table '" + newTabName + "'to the  Google spreadsheet(" + SPREADSHEET_ID + ").", e);
            }

            addColumnNamesForEmptyWinnersTable(newTabName);
        }

        boolean isNewSheetCreated = isSuchTableExist(newTabName);
        if (!isNewSheetCreated) {
            log.trace("...failed to create a new table '" + newTabName + "'.");
            return false;
        }
        log.trace("... new table '" + newTabName + "' was created.");
        return true;
    }

    private boolean isSuchTableExist(String tableName) throws GoogleApiException {
        log.trace("Method isSuchTableExist() invoked for table: '" + tableName + "'...");
        Spreadsheet spreadsheet;
        try {
            spreadsheet = sheetsService.spreadsheets().get(SPREADSHEET_ID).execute();
        } catch (IOException e) {
            log.error("Exception occurs getting spreadsheets with id:'" + SPREADSHEET_ID + "'.", e);
            throw new GoogleApiException("Failed to load Google spreadsheets with id:'" + SPREADSHEET_ID + "'.", e);
        }
        List<Sheet> sheets = spreadsheet.getSheets();
        for (Sheet sheet : sheets) {
            if (sheet.getProperties().getTitle().contains(tableName)) {
                log.trace("... table '" + tableName + "' has been found.");
                return true;
            }
        }
        log.trace("... table '" + tableName + "' hasn't been found.");
        return false;
    }

    private void addColumnNamesForEmptyWinnersTable(String tableName) throws GoogleApiException {
        log.trace("Method addColumnNamesForEmptyWinnersTable() invoked for table: '" + tableName + "'...");
        ValueRange columnNames = new ValueRange()
                .setValues(Collections.singletonList(Arrays.asList("Дата", "Переможець", "Переможні очки", "Інші гравці", "Примітки")));
        try {
            sheetsService.spreadsheets().values()
                    .append(SPREADSHEET_ID, String.format("%s!A1", tableName), columnNames)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();
            log.trace("...row with column names was added to the table '" + tableName + "'.");
        } catch (IOException e) {
            log.error("Exception occurs trying to append a row of column names for table:'" + tableName + "'.", e);
            throw new GoogleApiException("Failed to append a row of column names for table:'" + tableName + "'.", e);
        }
    }

    @Override
    public boolean addWinRecordToTheSheet(String tableName, WinRecord gameResult) throws GoogleApiException {
        log.trace("Method addWinRecordToTheSheet() invoked for table '" + tableName + "' with win record: " + gameResult + "...");
        initSheetsService();
        boolean isWinRecordAdded = false;

        ValueRange winRecord = new ValueRange()
                .setValues(Collections.singletonList(gameResult.summaryForWinRecord()));
        try {
            AppendValuesResponse appendWinRecord = sheetsService.spreadsheets().values()
                    .append(SPREADSHEET_ID, String.format("%s!A1", tableName), winRecord)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();
            if (appendWinRecord.getUpdates().getUpdatedRows() == 1) {
                log.trace("...row '" + appendWinRecord.getUpdates().getUpdatedRows() + "' has been appended to the table '" + tableName + "'.");
                isWinRecordAdded = true;
            }
        } catch (IOException e) {
            log.error("Exception occurs trying to append a new win record('" + winRecord + "') to the table:'" + tableName + "'.", e);
            throw new GoogleApiException("Failed to append a new win record('" + winRecord + "') to the table:'" + tableName + "'.", e);
        }
        return isWinRecordAdded;
    }

    @Override
    public String updateGameLocation(String gamesId, String newLocation) throws GoogleApiException {
        log.trace("Method updateGameLocation() invoked for game #:'" + gamesId + "' with new location: '" + newLocation + "'...");
        initSheetsService();

        ValueRange location = new ValueRange().setValues(List.of(Collections.singletonList(newLocation)));

        String range = "BoardGames!E" + (Integer.parseInt(gamesId) + 1);
        UpdateValuesResponse valuesResponse;
        try {
            valuesResponse = sheetsService.spreadsheets().values()
                    .update(SPREADSHEET_ID, range, location)
                    .setValueInputOption("RAW")
                    .setIncludeValuesInResponse(true)
                    .execute();
        } catch (IOException e) {
            log.error("Exception occurs trying to update game's(#:" + gamesId + ") location to '" + newLocation + "'.", e);
            throw new GoogleApiException("Failed to update game's(#:" + gamesId + ") location to '" + newLocation + "'.", e);
        }

        String updatedLocation = valuesResponse.getUpdatedData().getValues().get(0).get(0).toString();
        log.trace("... game's(#" + gamesId + ") location has been updated to '" + updatedLocation + "'.");
        return updatedLocation;
    }

    @Override
    public Map<String, Game> getGamesFromGoogleSheet() throws GoogleApiException {
        log.trace("Method getGamesFromGoogleSheet() invoked...");
        initSheetsService();

        final String range = "BoardGames!A2:E";
        ValueRange response = getValueRangeFromSpreadsheetOrThrowGoogleApiException(range);

        List<List<Object>> values = response.getValues();

        Map<String, Game> games = new HashMap<>();
        if (values == null || values.isEmpty()) {
            log.trace("No data obtained from spreadsheet with id: '" + SPREADSHEET_ID + "' and range:' " + range + "'.");
        } else {
            log.trace("Data has been obtained from spreadsheet with id: '" + SPREADSHEET_ID + "' and range:' " + range + "'.");
            for (List row : values) {
                if (row.get(0) != null)
                    games.put((String) row.get(0), new Game((String) row.get(0), (String) row.get(1), (String) row.get(2), (String) row.get(3), (String) row.get(4)));
            }
        }
        log.trace("..." + games.size() + " games have been obtained from Google Sheet.");
        return games;
    }

    @Override
    public List<WinRecord> getWinRecordsForGameFromGoogleSheet(String game) throws GoogleApiException {
        log.trace("Method getWinRecordsForGameFromGoogleSheet() invoked for game: '" + game + "'...");
        initSheetsService();

        List<WinRecord> winRecords = new ArrayList<>();

        if (!isSuchTableExist(game)) {
            return winRecords;
        }

        final String range = game + "!A2:E";
        List<List<Object>> values = getValueRangeFromSpreadsheetOrThrowGoogleApiException(range).getValues();

        if (values == null || values.isEmpty()) {
            log.trace("No win records obtained from spreadsheet with id: '" + SPREADSHEET_ID + "' and range:' " + range + "'.");
        } else {
            log.trace("Win records has been obtained from spreadsheet with id: '" + SPREADSHEET_ID + "' and range:' " + range + "'.");
            for (List row : values) {
                if (row.get(0) != null)
                    winRecords.add(new WinRecord(LocalDate.parse((String) row.get(0)), (String) row.get(1), (String) row.get(2), (String) row.get(3), (String) row.get(4)));
            }
        }

        log.trace("..." + winRecords.size() + " win records have been obtained from Google Sheet.");
        return winRecords;
    }

    private ValueRange getValueRangeFromSpreadsheetOrThrowGoogleApiException(String range) throws GoogleApiException {
        try {
            return sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();
        } catch (IOException e) {
            log.error("Exception occurs trying to get data from Google Sheet spreadsheet's '" + SPREADSHEET_ID + "' and '" + range + "'range.", e);
            throw new GoogleApiException("Failed to get data from Google Sheet spreadsheet's '" + SPREADSHEET_ID + "' and '" + range + "'range.", e);
        }
    }
}
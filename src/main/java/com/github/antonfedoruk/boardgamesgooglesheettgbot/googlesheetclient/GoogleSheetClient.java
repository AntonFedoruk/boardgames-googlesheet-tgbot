package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.WinRecord;
import com.google.api.services.sheets.v4.model.Sheet;

import java.util.List;
import java.util.Map;

/**
 * Client for Google Sheet API.
 */
public interface GoogleSheetClient {

     /** Create a Google {@link Sheet}.
     *
     * @param newTabName will be the Title of new table.
     * @return boolean is true once new table is created, it is false id such table already exists.
      */
    boolean createNewSheetTab(String newTabName) throws GoogleApiIOException, GoogleApiOnExecuteException;

    /** Create a Google {@link Sheet}.
     *
     * @param tableName is the sheetId value if certain table.
     * @return boolean is true if record was added successfully.
     */
    boolean addWinRecordToTheSheet(String tableName, WinRecord gameResult) throws GoogleApiOnExecuteException, GoogleApiIOException;

    /**
     * Update {@link Game}'s location on Google {@link Sheet}.
     */
    String updateGameLocation(String id, String newLocation) throws GoogleApiIOException, GoogleApiOnExecuteException;

    /**
     * Retrieve all {@link Game}s data from the {@link Sheet}.
     *
     * @return {@link Map} of {@link Game} by id.
     */
    Map<String, Game> getGamesFromGoogleSheet() throws GoogleApiIOException, GoogleApiOnExecuteException;

    /**
     * Retrieve all {@link WinRecord}s of certain game.
     *
     * @return {@link Map} of {@link WinRecord} by id.
     */
    List<WinRecord> getWinRecordsForGameFromGoogleSheet(String game) throws GoogleApiIOException, GoogleApiOnExecuteException;
}
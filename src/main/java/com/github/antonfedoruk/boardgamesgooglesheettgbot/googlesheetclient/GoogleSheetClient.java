package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.WinRecord;
import com.google.api.services.sheets.v4.model.Sheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
     * @throws GeneralSecurityException,IOException
     */
    boolean createNewSheetTab(String newTabName) throws GeneralSecurityException, IOException;

    /** Create a Google {@link Sheet}.
     *
     * @param tableName is the sheetId value if certain table.
     * @return boolean is true if record was added successfully.
     * @throws GeneralSecurityException,IOException
     */
    boolean addWinRecordToTheSheet(String tableName, WinRecord gameResult) throws GeneralSecurityException, IOException;

    /**
     * Update {@link Game}'s location on Google {@link Sheet}.
     *
     * @throws GeneralSecurityException,IOException
     */
    String updateGameLocation(String id, String newLocation) throws GeneralSecurityException, IOException;

    /**
     * Retrieve all {@link Game}s data from the {@link Sheet}.
     *
     * @return {@link Map} of {@link Game} by id.
     * @throws GeneralSecurityException,IOException
     */
    Map<String, Game> getGamesFromGoogleSheet() throws GeneralSecurityException, IOException;

    /**
     * Retrieve all {@link WinRecord}s of certain game.
     *
     * @return {@link Map} of {@link WinRecord} by id.
     * @throws GeneralSecurityException,IOException
     */
    List<WinRecord> getWinRecordsForGameFromGoogleSheet(String game) throws GeneralSecurityException, IOException;
}
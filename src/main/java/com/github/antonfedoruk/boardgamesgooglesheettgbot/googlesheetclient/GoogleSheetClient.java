package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.dto.GoogleSheetDTO;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.dto.GoogleSheetResponseDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * Client for Google Sheet API.
 */
public interface GoogleSheetClient {
    void initSheetsService() throws GeneralSecurityException, IOException;


//    /**
//     * Retrieve all data from the sheet.
//     *
//     * @return {@link Map} of {@link Object}.
//     * @throws GeneralSecurityException,IOException
//     */
//    Map<Object, Object> getDataFromSheet() throws GeneralSecurityException, IOException;
//
//    /**
//     * Create a Google {@link Sheet}.
//     *
//     * @param request provided {@link GoogleSheetDTO}.
//     * @return Spreadsheet's ID and URL as {@link GoogleSheetResponseDTO} object.
//     * @throws GeneralSecurityException,IOException
//     */
//    GoogleSheetResponseDTO createGoogleSheet(GoogleSheetDTO request) throws GeneralSecurityException, IOException;
//
//    GoogleSheetResponseDTO writeValuesOnSheet(GoogleSheetDTO request, String spreadsheetId) throws GeneralSecurityException, IOException;

    /**
     * Update {@link Game}'s location on the sheet.
     *
     * @throws GeneralSecurityException,IOException
     */
    String updateGameLocation(String id, String newLocation) throws GeneralSecurityException, IOException;

    /**
     * Retrieve all {@link Game}s data from the sheet.
     *
     * @return {@link Map} of {@link Game} by id.
     * @throws GeneralSecurityException,IOException
     */
    Map<String, Game> getGamesFromGoogleSheet() throws GeneralSecurityException, IOException;
}
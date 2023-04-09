package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleSheetResponseDTO {
    private String spreadSheetId;
    private String spreadSheetUrl;
}
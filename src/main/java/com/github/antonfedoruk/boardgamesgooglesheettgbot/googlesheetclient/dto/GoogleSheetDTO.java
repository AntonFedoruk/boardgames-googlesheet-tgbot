package com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoogleSheetDTO {
    String sheetName;

    List<List<Object>> dataToBeUpdated;
}

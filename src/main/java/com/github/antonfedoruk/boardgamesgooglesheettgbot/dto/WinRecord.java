package com.github.antonfedoruk.boardgamesgooglesheettgbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WinRecord implements Serializable, Comparable<WinRecord> {
    @EqualsAndHashCode.Include
    private LocalDate date;
    @EqualsAndHashCode.Include
    private String winner;
    @EqualsAndHashCode.Include
    private String score;
    private String players;
    private String notes;

    public List<Object> summaryForWinRecord() {
        return Arrays.asList(date.toString(), winner, score, players, notes);
    }

    @Override
    public int compareTo(WinRecord o) {
        return StringUtils.compare(date.toString()+winner+score, o.getDate().toString()+winner+score);
    }
}
package com.github.antonfedoruk.boardgamesgooglesheettgbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Game implements Serializable, Comparable<Game> {
    private String Id;
    @EqualsAndHashCode.Include
    private String name;
    private String numberOfPlayers;
    @EqualsAndHashCode.Include
    private String owner;
    private String lastLocation;

    @Override
    public int compareTo(Game o) {
        return StringUtils.compare(name, o.getName());
    }
}
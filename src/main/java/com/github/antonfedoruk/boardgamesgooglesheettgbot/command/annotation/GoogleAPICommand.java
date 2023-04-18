package com.github.antonfedoruk.boardgamesgooglesheettgbot.command.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation should be used on each {@link com.github.antonfedoruk.boardgamesgooglesheettgbot.command.Command} to notify, that it use Google API.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GoogleAPICommand {
}

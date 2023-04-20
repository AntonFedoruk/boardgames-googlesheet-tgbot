package com.github.antonfedoruk.boardgamesgooglesheettgbot.command.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Mark if {@link com.github.antonfedoruk.boardgamesgooglesheettgbot.command.Command} can be viewed only by admins.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminCommand {
}
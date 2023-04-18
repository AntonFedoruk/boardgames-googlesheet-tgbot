package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import lombok.Getter;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

public class LoggingCommandDecorator implements Command{
    @Getter
    private final Command decoratedCommand;
    private Logger log;

    public LoggingCommandDecorator(Command decoratedCommand) {
        this.decoratedCommand = decoratedCommand;
        log = decoratedCommand.getLogger();
    }

    @Override
    public void execute(Update update) {
        log.trace("Method execute() was invoked...");
        decoratedCommand.execute(update);
        log.trace("...method execute() completed.");
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
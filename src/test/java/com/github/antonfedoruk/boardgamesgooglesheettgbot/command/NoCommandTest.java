package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.NO;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.NoCommand.NO_MESSAGE;

public class NoCommandTest extends AbstractCommandTest{
    @Override
    String getCommandName() {
        return NO.getCommandName();
    }

    @Override
    String getCommandMessage() {
        return NO_MESSAGE;
    }

    @Override
    Command getCommand() {
        return new NoCommand(sendBotMessageService);
    }
}
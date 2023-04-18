package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Help {@link Command}.
 */
public class SignInCommand implements Command{
    private static final Logger log = LoggerFactory.getLogger(SignInCommand.class);
    private final SendBotMessageService sendBotMessageService;

    public static final String SIGN_IN_NEEDED_MESSAGE = "Нажаль, я не можу виконати бажану команду. @antonfedoruk зайди через Google.";

    public SignInCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(CommandUtils.getChatId(update), SIGN_IN_NEEDED_MESSAGE);
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}

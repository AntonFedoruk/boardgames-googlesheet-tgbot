package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.command.annotation.GoogleAPICommand;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.WinRecord;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleApiException;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.WIN;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getChatId;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getMessage;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.UpdateLocationCommand.IO_OR_GENERAL_SECURITY_EXCEPTION_MESSAGE;

/**
 * Win {@link Command}.
 * This command add a note about player's victory to the corresponding tablet in Google Sheets.
 */
@GoogleAPICommand
public class WinCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(WinCommand.class);
    private final SendBotMessageService sendBotMessageService;
    private final GoogleApiService googleApiService;

    public static final String HOW_TO_USE_MESSAGE = "Щоб всі пам'ятали про твою потну катку скористайся наступним: \n" +
            WIN.getCommandName() + " <i>#_гри</i> <b>ім'я_переможця</b> <u>переможні_очки</u> <i>імена_інших_гравців</i>(написані через кому і без пробілу) <u>нотатки</u>\n" +
            "Приклад: \n" + WIN.getCommandName() + " 52 Толік 158 Сірьожа,Армен,Сєня Сєня продався Толіку за пачку сємок і злив партію( \n" +
            "П.С.: підгледіть <i>#_гри</i> можна скориставшись командою /games";
    public static final String CREATED_NEW_TAB_MESSAGE = "Створив нову вкладку для слідкуванням за перемогами по грі '%s'.";
    public static final String ADDED_RECORD_MESSAGE = "Новий запис про перемогу добавлено!";

    public WinCommand(SendBotMessageService sendBotMessageService, GoogleApiService googleApiService) {
        this.sendBotMessageService = sendBotMessageService;
        this.googleApiService = googleApiService;
    }

    @Override
    public void execute(Update update) {
        String commandFromUser = getMessage(update);
        if (commandFromUser.equalsIgnoreCase(WIN.getCommandName())) {
            log.error(commandFromUser + " was send without parameters, so instruction message should be sent.");
            sendBotMessageService.sendMessage(getChatId(update), HOW_TO_USE_MESSAGE);
            return;
        }

        String[] commandByParts = commandFromUser.split(" ");
        String gamesId = commandByParts[1];
        String winner = commandByParts[2];
        String score = commandByParts[3];
        String players = commandByParts[4];
        String notes = String.join(" ", Arrays.copyOfRange(commandByParts, 5, commandByParts.length));

        WinRecord gameResult = new WinRecord(LocalDate.now(),winner, score, players, notes);
        try {
            Map<String, Game> gamesFromGoogleSheet = googleApiService.getGamesFromGoogleSheet();
            String gameName = gamesFromGoogleSheet.get(gamesId).getName();
            if (googleApiService.createNewSheetTab(gameName)){
                sendBotMessageService.sendMessage(getChatId(update), String.format(CREATED_NEW_TAB_MESSAGE, gameName));
            }

            if (googleApiService.addWinRecordToTheSheet(gameName, gameResult)){
                sendBotMessageService.sendMessage(getChatId(update), ADDED_RECORD_MESSAGE);
            }
        } catch (GoogleApiException e) {
            log.error(e.getMessage());
            sendBotMessageService.sendMessage(getChatId(update), IO_OR_GENERAL_SECURITY_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.command.annotation.GoogleAPICommand;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleApiIOException;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleApiOnExecuteException;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.UPDATE_LOCATION;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getChatId;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getMessage;

/**
 * UpdateLocation {@link Command}.
 */
@GoogleAPICommand
public class UpdateLocationCommand implements Command {
    private final Logger log = LoggerFactory.getLogger(UpdateLocationCommand.class);
    private final SendBotMessageService sendBotMessageService;
    private final GoogleApiService googleApiService;

    public static final String HOW_TO_USE_MESSAGE = "Щоб оновити місце знаходження гри, скористайтесь наступною командою: \n" +
            UPDATE_LOCATION.getCommandName() + " <i>#_гри</i> <b>локація_гри</b>\n" +
            "Приклад: '" + UPDATE_LOCATION.getCommandName() + " 33 в Богдана'\n" +
            "П.С.: не робіть довгу назву 'локації', щоб воно в колонку влізло.";
    public static final String NUMBER_FORMAT_EXCEPTION_MESSAGE = "Зайченятко моє, щось ти наплутав, треба циферку, а не буковки там де <i>#_гри</i>. Подивись уважно і спробуй ще раз.";
    public static final String OUT_OF_RANGE_GAME_ID_MESSAGE = "Сонечко, щось не так, в нас ще нема так багато ігор, перевір <i>#_гри</i> будь ласочка і спробуй ще раз.";
    public static final String TO_LONG_LOCATION_NAME_MESSAGE = "Йо йо, Толік, це ти? По людські ж попросив НЕ РОБИТЬ довгу назву 'локації' :(";
    public static final String LOCATION_CHANGED_MESSAGE = "Місцезнаходження гри <b>#%s</b> успішно оновлено до <i>%s</i>.";
    public static final String IO_EXCEPTION_MESSAGE = "Йо-йой, щось пішло не так, ГОВНОКОД детекшн(IO_EXCEPTION)!";
    public static final String GENERAL_SECURITY_EXCEPTION_MESSAGE = "Йо-йой, щось пішло не так, ГОВНОКОД детекшн(GENERAL_SECURITY_EXCEPTION)!";

    public UpdateLocationCommand(SendBotMessageService sendBotMessageService, GoogleApiService googleApiService) {
        this.sendBotMessageService = sendBotMessageService;
        this.googleApiService = googleApiService;
    }

    @Override
    public void execute(Update update) {
        String commandFromUser = getMessage(update);
        if (commandFromUser.equalsIgnoreCase(UPDATE_LOCATION.getCommandName())) {
            log.trace("User send command with no parameters.");
            sendBotMessageService.sendMessage(getChatId(update), HOW_TO_USE_MESSAGE);
            return;
        }
        String[] commandByParts = commandFromUser.split(" ");
        String gamesId = commandByParts[1];

        try {
            int gamesAmount = googleApiService.getGamesFromGoogleSheet().size();
            try {
                Integer.parseInt(gamesId);
            } catch (NumberFormatException e) {
                sendBotMessageService.sendMessage(getChatId(update), NUMBER_FORMAT_EXCEPTION_MESSAGE);
                return;
            }
            if (Integer.parseInt(gamesId) > gamesAmount) {
                sendBotMessageService.sendMessage(getChatId(update), OUT_OF_RANGE_GAME_ID_MESSAGE);
                return;
            }


            String gamesNewLocation = String.join(" ", Arrays.copyOfRange(commandByParts, 2, commandByParts.length));

            if (gamesNewLocation.length() > 20) {
                sendBotMessageService.sendMessage(getChatId(update), TO_LONG_LOCATION_NAME_MESSAGE);
                return;
            }

            String storedLocation = googleApiService.updateGameLocation(gamesId, gamesNewLocation);
            sendBotMessageService.sendMessage(getChatId(update), String.format(LOCATION_CHANGED_MESSAGE, gamesId, storedLocation));
        } catch (GoogleApiOnExecuteException e) {
            log.error(e.getMessage());
            sendBotMessageService.sendMessage(getChatId(update), GENERAL_SECURITY_EXCEPTION_MESSAGE);
        } catch (GoogleApiIOException e) {
            log.error(e.getMessage());
            sendBotMessageService.sendMessage(getChatId(update), IO_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
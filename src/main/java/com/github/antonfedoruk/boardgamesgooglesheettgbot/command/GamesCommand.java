package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Games {@link Command}.
 */
public class GamesCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final GoogleApiService googleApiService;

    public static final String GAMES_NOT_FOUND_MESSAGE = "Ой, не можу знайти таблицю з іграми, напишіть гімнюкю що писав цей код, що це гівнокод!";
    public static final String SHEETS_SERVICE_EXCEPTION_MESSAGE = "Не можу приєднатись до гугл таблиці... Можливо помилка з Google API";
    public static final String GAMES_FOUND_MESSAGE = "Шукаю настолочки, якими вже обзавились";

    @Value("${resources.gamescommand.photo.pathname}")
    private String PHOTO_PATHNAME;

    public GamesCommand(SendBotMessageService sendBotMessageService, GoogleApiService googleApiService) {
        this.sendBotMessageService = sendBotMessageService;
        this.googleApiService = googleApiService;
    }

    @Override
    public void execute(Update update) {
        Map<String, Game> gamesFromGoogleSheet = null;

        try {
            try {
                gamesFromGoogleSheet = googleApiService.getGamesFromGoogleSheet();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
                sendBotMessageService.sendMessage(CommandUtils.getChatId(update), SHEETS_SERVICE_EXCEPTION_MESSAGE);
                return;
            }

            if (!gamesFromGoogleSheet.isEmpty()) {
                sendBotMessageService.sendMessage(CommandUtils.getChatId(update), GAMES_FOUND_MESSAGE);
                prepareAndSendPicturesOfGames(update, gamesFromGoogleSheet);
            }
        } catch (
                AWTError error) { //#Exception in thread ... java.awt.AWTError: Can't connect to X11 window server using ':0.0' as the value of the DISPLAY variable.
            error.printStackTrace();
            String message = "<b>Список ігор</b>: \n";
            String gameList = gamesFromGoogleSheet.values().stream().sorted()
                    .map(game -> "<b>" + game.getName() + "</b> (<i>" + game.getNumberOfPlayers() + "</i> | <u> " + game.getLastLocation() + "</u>)")
                    .collect(Collectors.joining("\n"));
            sendBotMessageService.sendMessage(CommandUtils.getChatId(update), message + gameList);
        }
        if (gamesFromGoogleSheet.isEmpty()) {
            sendBotMessageService.sendMessage(CommandUtils.getChatId(update), GAMES_NOT_FOUND_MESSAGE);
        }
    }

    private void prepareAndSendPicturesOfGames(Update update, Map<String, Game> gamesFromGoogleSheet) {
        final int defaultAmountOfGamesOn1Photo = 25;
        for (int i = 0; i < gamesFromGoogleSheet.size(); i = i + defaultAmountOfGamesOn1Photo) {
            String gameList = gamesFromGoogleSheet.values().stream().sorted().skip(i).limit(defaultAmountOfGamesOn1Photo)
                    .map(game -> "<tr style='border-bottom: 1px solid #000'>" +
                                     "<td style='text-align:center; border-bottom: 1px solid #000'>" + game.getId() + "</td>" +
                                     "<td style='border-bottom: 1px solid #000'><b>" + game.getName() + "</b></td>" +
                                     "<td style='text-align:center; border-bottom: 1px solid #000'>" + game.getNumberOfPlayers() + "</td> " +
                                     "<td style='text-align:center; border-bottom: 1px solid #000'><u>" + game.getLastLocation() + "</u></td>" +
                                 "</tr>")
                    .collect(Collectors.joining());

            String htmlMessageWithTable = "<h2 style='text-align:center'>Наші настолочки #" + (i / defaultAmountOfGamesOn1Photo + 1) + "</h1>" +
                    "<table style='border: 1px solid; width: 420px'>" +
                    "<tr>" +
                        "<td style='border: 1px solid'><b>ID</b></td> " +
                        "<td style='border: 1px solid'><b>Назва</b></td> " +
                        "<td style='text-align:center; border: 1px solid'><b>К-ть гравців</b></td> " +
                        "<td style='text-align:center; border: 1px solid'><b>Де шукати гру</b></td>" +
                    "</tr>" +
                    gameList +
                    "</table>";

            int gamesOnThePicture = Math.min((gamesFromGoogleSheet.size() - i), defaultAmountOfGamesOn1Photo);
            final int heightOfSingleRow = 29;
            final int heightOfTableHeader = 80;
            int width = 555, height = heightOfTableHeader + gamesOnThePicture * heightOfSingleRow;
            System.out.println("Games" + (i / defaultAmountOfGamesOn1Photo + 1) + ".png    -> height=" + height);

            BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration()
                    .createCompatibleImage(width, height);

            Graphics graphics = image.createGraphics();

            JEditorPane jep = new JEditorPane("text/html", htmlMessageWithTable);
            jep.setSize(width, height);
            jep.setBackground(new Color(249356214));
            jep.print(graphics);

            String pathname = PHOTO_PATHNAME + "Games" + (i / defaultAmountOfGamesOn1Photo + 1) + ".png";

            try {
                ImageIO.write(image, "png", new File(pathname));
                sendBotMessageService.sendPhoto(CommandUtils.getChatId(update), new File(pathname));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
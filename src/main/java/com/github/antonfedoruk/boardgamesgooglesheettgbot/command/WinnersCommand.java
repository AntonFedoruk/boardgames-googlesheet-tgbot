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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.WINNERS;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getChatId;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getMessage;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.UpdateLocationCommand.IO_OR_GENERAL_SECURITY_EXCEPTION_MESSAGE;

/**
 * Winner {@link Command}.
 */
@GoogleAPICommand
public class WinnersCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(WinnersCommand.class);
    private final SendBotMessageService sendBotMessageService;
    private final GoogleApiService googleApiService;

    private final String PHOTO_PATHNAME;

    public static final String HOW_TO_USE_MESSAGE = "Щоб переглянути історію перемог по певній грі скористайтесь наступною командою: \n" +
            WINNERS.getCommandName() + " <i>#_гри</i>\n" +
            "Приклад: \n" + WINNERS.getCommandName() + " 17\n" +
            "П.С.: підгледіть <i>#_гри</i> можна скориставшись командою /games";
    public static final String NO_WIN_RECORDS_MESSAGE = "Ще не було записів перемог <b>%s</b>.";
    public static final String WIN_RECORDS_FOUND_MESSAGE = "Обробляю записи про перемоги...";

    public WinnersCommand(SendBotMessageService sendBotMessageService, GoogleApiService googleApiService) {
        this.sendBotMessageService = sendBotMessageService;
        this.googleApiService = googleApiService;
        this.PHOTO_PATHNAME = googleApiService.getGetPhotoPathname();
    }

    @Override
    public void execute(Update update) {
        String commandFromUser = getMessage(update);
        if (commandFromUser.equalsIgnoreCase(WINNERS.getCommandName())) {
            sendBotMessageService.sendMessage(getChatId(update), HOW_TO_USE_MESSAGE);
            return;
        }

        String[] commandByParts = commandFromUser.split(" ");
        String gamesId = commandByParts[1];

        try {
            Map<String, Game> gamesFromGoogleSheet = googleApiService.getGamesFromGoogleSheet();
            String gameName = gamesFromGoogleSheet.get(gamesId).getName();
            List<WinRecord> winRecordsForGameFromGoogleSheet = googleApiService.getWinRecordsForGameFromGoogleSheet(gameName);
            if (winRecordsForGameFromGoogleSheet.isEmpty()){
                sendBotMessageService.sendMessage(getChatId(update), String.format(NO_WIN_RECORDS_MESSAGE, gameName));
            } else {
                sendBotMessageService.sendMessage(getChatId(update), WIN_RECORDS_FOUND_MESSAGE);
                prepareAndSendPicturesOfWinRecords(update, winRecordsForGameFromGoogleSheet, gameName);
            }
        } catch (GoogleApiException e) {
            log.error(e.getMessage(), e);
            sendBotMessageService.sendMessage(getChatId(update), IO_OR_GENERAL_SECURITY_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    private void prepareAndSendPicturesOfWinRecords(Update update, List<WinRecord> winRecordsForGameFromGoogleSheet, String gameName) {
        final int defaultAmountOfRecordsOn1Photo = 10;
        for (int i = 0; i < winRecordsForGameFromGoogleSheet.size(); i = i + defaultAmountOfRecordsOn1Photo) {
            String gameList = winRecordsForGameFromGoogleSheet.stream().sorted(Comparator.comparing(WinRecord::getDate).reversed()).skip(i).limit(defaultAmountOfRecordsOn1Photo)
                    .map(winRecord -> "<tr style='border-bottom: 1px solid #000'>" +
                                          "<td style='text-align:center; border-bottom: 1px solid #000'>" + winRecord.getDate() + "</td>" +
                                          "<td style='border-bottom: 1px solid #000'><b>" + winRecord.getWinner() + "</b></td>" +
                                          "<td style='text-align:center; border-bottom: 1px solid #000'><i>" + winRecord.getScore() + "</i></td>" +
                                          "<td style='border-bottom: 1px solid #000'>" + winRecord.getPlayers() + "</td> " +
                                          "<td style='border-bottom: 1px solid #000'><u>" + winRecord.getNotes() + "</u></td>" +
                                      "</tr>")
                    .collect(Collectors.joining());

            String htmlMessageWithTable = "<h2 style='text-align:center'>Записи перемог #" + (i / defaultAmountOfRecordsOn1Photo + 1) + "</h1>" +
                    "<table style='border: 1px solid; width: 600px'>" +
                    "<tr>" +
                        "<td style='text-align:center; border: 1px solid'><b>Дата</b></td> " +
                        "<td style='text-align:center; border: 1px solid'><b>Переможець</b></td> " +
                        "<td style='text-align:center; border: 1px solid'><b>Переможні очки</b></td> " +
                        "<td style='text-align:center; border: 1px solid'><b>Інші гравців</b></td> " +
                        "<td style='text-align:center; border: 1px solid'><b>Примітки</b></td>" +
                    "</tr>" +
                    gameList +
                    "</table>";

            int recordsOnThePicture = Math.min((winRecordsForGameFromGoogleSheet.size() - i), defaultAmountOfRecordsOn1Photo);
            final int heightOfSingleRow = 55;
            final int heightOfTableHeader = 80;
            int width = 790, height = heightOfTableHeader + recordsOnThePicture * heightOfSingleRow;

            BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration()
                    .createCompatibleImage(width, height);
            Graphics graphics = image.createGraphics();
            JEditorPane jep = new JEditorPane("text/html", htmlMessageWithTable);
            jep.setSize(width, height);
            jep.setBackground(new Color(0x90E2EA));
            jep.print(graphics);

            String pathname = PHOTO_PATHNAME + "Win" + (i / defaultAmountOfRecordsOn1Photo + 1) + ".png";
            try {
                ImageIO.write(image, "png", new File(pathname));
                sendBotMessageService.sendPhoto(CommandUtils.getChatId(update), new File(pathname));
            } catch (IOException e) {
                log.error("Exception occurs as it new image can't be created in '" + pathname + "'.");
            }
        }
    }
}
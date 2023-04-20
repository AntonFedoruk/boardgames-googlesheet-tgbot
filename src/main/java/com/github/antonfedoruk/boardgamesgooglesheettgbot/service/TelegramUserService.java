package com.github.antonfedoruk.boardgamesgooglesheettgbot.service;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.repository.entity.TelegramUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * {@link Service} for handling {@link TelegramUser} entity.
 */
public interface TelegramUserService {

    /**
     * Save provided {@link TelegramUser} entity.
     *
     * @param telegramUser provided telegram user.
     */
    void save(TelegramUser telegramUser);

    /**
     * Retrieve all active {@link TelegramUser}.
     *
     * @return the collection of the active {@link TelegramUser} objects.
     */
    List<TelegramUser> findAllActiveUsers();

    /**
     * Find {@link TelegramUser} by chatId.
     *
     * @param userId provided by Chat ID
     * @return {@link TelegramUser} with provided chat ID or null otherwise.
     */
    Optional<TelegramUser> findByUserId(Long userId);

    /**
     * Find {@link TelegramUser} by userName.
     *
     * @param userName provided by Chat ID
     * @return {@link TelegramUser} with provided chat ID or null otherwise.
     */
    Optional<TelegramUser> findByUserName(String userName);

    /**
     * Retrieve all inactive {@link TelegramUser}
     *
     * @return the collection of the inactive {@link TelegramUser} objects.
     */
    List<TelegramUser> findAllInActiveUsers();

    /**
     * Retrieve all {@link TelegramUser} without granted access to Goggle Sheets.
     *
     * @return the collection of {@link TelegramUser} without access to Goggle Sheets.
     */
    List<TelegramUser> findAllUsersWithoutAccessToGoogleSheets();

    /**
     * Retrieve all {@link TelegramUser} with granted access to Goggle Sheets.
     *
     * @return the collection of {@link TelegramUser} with access to Goggle Sheets.
     */
    List<TelegramUser> findAllUsersWithAccessToGoogleSheets();
}

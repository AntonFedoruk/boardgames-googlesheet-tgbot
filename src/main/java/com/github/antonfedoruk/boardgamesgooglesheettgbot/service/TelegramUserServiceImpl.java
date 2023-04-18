package com.github.antonfedoruk.boardgamesgooglesheettgbot.service;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.repository.TelegramUserRepository;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.repository.entity.TelegramUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link TelegramUserService}.
 */
@Slf4j
@Service
public class TelegramUserServiceImpl implements TelegramUserService {
    private final TelegramUserRepository telegramUserRepository;

    public TelegramUserServiceImpl(TelegramUserRepository telegramUserRepository) {
        this.telegramUserRepository = telegramUserRepository;
    }

    @Override
    public void save(TelegramUser telegramUser) {
        telegramUserRepository.save(telegramUser);
        log.trace("TelegramUserRepository saved user'" + telegramUser+ "'.");
    }

    @Override
    public List<TelegramUser> findAllActiveUsers() {
        List<TelegramUser> allActiveUsers = telegramUserRepository.findAllByActiveTrue();
        log.trace("TelegramUserRepository found 'active' users.");
        return allActiveUsers;
    }

    @Override
    public Optional<TelegramUser> findByUserId(Long userId) {
        Optional<TelegramUser> userById = telegramUserRepository.findById(userId);
        log.trace("TelegramUserRepository found user by id(" + userId + "): '" + (userById.isPresent() ? userById.get().getUserName() : "No_user_with_such_ID") + "'.");
        return userById;
    }

    @Override
    public List<TelegramUser> findAllInActiveUsers() {
        List<TelegramUser> allInactiveUsers = telegramUserRepository.findAllByActiveFalse();
        log.trace("TelegramUserRepository found 'inactive' users.");
        return allInactiveUsers;
    }
}
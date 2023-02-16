package com.github.antonfedoruk.boardgamesgooglesheettgbot.repository;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.repository.entity.TelegramUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Integration-level testing for {@link TelegramUserRepository}.
 */
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class TelegramUserRepositoryIT {
    @Autowired
    private TelegramUserRepository telegramUserRepository;

    @Sql(scripts = {"/sql/clearDbs.sql", "/sql/addUsers.sql"})
    @Test
    @DisplayName("Should properly find all active users")
    void shouldProperlyFindAllActiveUsers() {
        // when
        List<TelegramUser> allActiveUsers = telegramUserRepository.findAllByActiveTrue();
        // then
        Assertions.assertEquals(5, allActiveUsers.size());
    }

    @Sql(scripts = {"/sql/clearDbs.sql"})
    @Test
    @DisplayName("Should properly save telegram user")
    void shouldProperlySaveTelegramUser() {
        // given
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setUserId(33L);
        telegramUser.setUserName("user33");
        telegramUser.setActive(false);
        telegramUserRepository.save(telegramUser);
        // when
        Optional<TelegramUser> saved = telegramUserRepository.findById(telegramUser.getUserId());
        // then
        Assertions.assertTrue(saved.isPresent());
        Assertions.assertEquals(telegramUser, saved.get());
    }
}
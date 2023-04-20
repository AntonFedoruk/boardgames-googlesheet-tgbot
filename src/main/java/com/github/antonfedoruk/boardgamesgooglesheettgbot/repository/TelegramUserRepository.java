package com.github.antonfedoruk.boardgamesgooglesheettgbot.repository;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.repository.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Repository} for handling with {@link TelegramUser} entity.
 */
@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    List<TelegramUser> findAllByActiveTrue();

    List<TelegramUser> findAllByActiveFalse();

    List<TelegramUser> findAllByHasGoogleAccessFalse();

    Optional<TelegramUser> findByUserName(String userName);

    List<TelegramUser> findAllByHasGoogleAccessTrue();
}
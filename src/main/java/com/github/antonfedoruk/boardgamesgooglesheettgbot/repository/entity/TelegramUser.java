package com.github.antonfedoruk.boardgamesgooglesheettgbot.repository.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Telegram User entity.
 */
@Data
@Entity
@Table(name = "tg_user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramUser {
    @Id
    @Column(name = "tg_user_id")
    Long userId;

    @Column(name = "tg_user_name")
    String userName;

    @Column(name = "active")
    boolean active;

    @Column(name = "has_google_access")
    boolean hasGoogleAccess;
}
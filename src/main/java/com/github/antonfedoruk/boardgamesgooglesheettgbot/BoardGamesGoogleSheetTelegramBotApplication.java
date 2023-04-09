package com.github.antonfedoruk.boardgamesgooglesheettgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BoardGamesGoogleSheetTelegramBotApplication {

	public static void main(String[] args) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(BoardGamesGoogleSheetTelegramBotApplication.class);
		builder.headless(false); //	java.awt.headlessexception fixed  ... -Djava.awt.headless=false didn't work
		ConfigurableApplicationContext context = builder.run(args);
	}
}
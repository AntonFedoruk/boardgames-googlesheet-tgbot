package com.github.antonfedoruk.boardgamesgooglesheettgbot.logs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testing of Logging into the file")
public class LoggingTest {
    private static final Logger log = LoggerFactory.getLogger(LoggingTest.class);

    @Test
    @DisplayName("Check if parts that should be logged are added to the logfile.")
    public void checkIfPartsThatShouldBeLoggedAddedToTheLogfile() throws IOException {
        //given
        // logging pattern: %d %-5level [%t] [%replace(user:%X{user}; command:%X{message}){'user:; command:','-'}] %logger{0} [%file:%line] : %replace(%.-500msg){'\n',''}%n
        String username = "user123";
        String enteredCommaand = "/command1 param1 x x x";
        String expectedLogMessagePartMsg = "User " + username + " entered command.";
        String expectedLogMessagePartMDC = "[user:" + username + "; command:" + enteredCommaand + "]";
        MDC.put("user", username);
        MDC.put("message", enteredCommaand);
        //when
        log.trace("User {} entered command.", username);
        String actualLogMessage = getLatestLogMessageFromFile("logs/app.log");
        //then
        assertTrue(actualLogMessage.contains(expectedLogMessagePartMsg));
        assertTrue(actualLogMessage.contains(expectedLogMessagePartMDC));
        MDC.remove("user");
        MDC.remove("message");
    }

    private static String getLatestLogMessageFromFile(String filePath) throws IOException {
        String latestLogMessage = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                latestLogMessage = line;
            }
        }
        return latestLogMessage;
    }
}

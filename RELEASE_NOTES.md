## Release Notes

### 0.8.2-SNAPSHOT
*   bugfix/ send message with win records once AWTError occurs.

### 0.8.1-SNAPSHOT
*   improved Readme.md file.

### 0.8.0-SNAPSHOT
*   restricted access to certain bot commands for non-privileged users.
    * added @AdminCommand annotations(commands with such annotation can be used only by admins);
    * added /promote and /demote admin's command;
    * TelegramUser entity has been extended with hasGoogleAccess field.

### 0.7.0-SNAPSHOT
*   added Logging to the project:
    * added logging at all stages of the project;
    * added FILE-ROLLING appender (log file location '/log/app.log');
    * implemented Decorator pattern (LoggingCommandDecorator) to the Command.
*   wrapped 'GeneralSecurityException | IOException' into GoogleApiException;
*   minor bugfix.

### 0.6.0-SNAPSHOT
*   implemented winning statistic:
    * added /win command;
    * added /winners command.
*   modified /start and /help command.

### 0.5.0-SNAPSHOT
*   added /updatelocation command.

### 0.4.0-SNAPSHOT
*   integrate Google Sheets API to the project:
    * added GOauthController with Oauth2 authorization;
    * added view templates;
*   added GamesCommand;
*   updated Docker's part.
  
### 0.3.0-SNAPSHOT
*   added deployment process to the project.

### 0.2.0-SNAPSHOT
*   added Command pattern for handling Telegram Bot commands.

### 0.1.0-SNAPSHOT
*   added stub telegram bot;
*   added skeleton BGGSTGB Spring Boot project.
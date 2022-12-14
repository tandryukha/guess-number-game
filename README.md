# Getting Started
Backend for a guess the number game. Communication with players is done via web sockets.

**Game process:**

1) The server starts a round of the game and gives 10 seconds to place a bet for the players on numbers from 1 to 10 with the **amount of the bet**

2) After the time expires, the server generates a random number from 1 to 10

3) If the player guesses the number, a message is sent to him that he won with a winnings of 9.9 times the stake

4) If the player loses receives a message about the loss

5) All players receive a message with a list of winning players in which there is a nickname and the amount of winnings

6) The process is repeated

Technologies: Java 11, Gradle, Spring Boot

# How to run game server locally
1. Install Docker Desktop
2. Navigate to project root folder
2. docker-compose run

# Local development
 Run `./gradlew build` to run tests and package jar

# Customization
Game parameters may be customized in application.yaml

# Implementation notes
Simple-threaded game server on spring boot that can accept many simultaneous players at the same time.

# Further improvements
- Write test players and run E2E tests
- Optimize docker image size
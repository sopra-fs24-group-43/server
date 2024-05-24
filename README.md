# SoPra FS24 Group 43

<h3 align="center">
  <br>
  <img src="/ReadMeImages/logo18.png" height="200"></a>
</h3>

## Introduction

Freitagsmaler is a game where one user draws a word, while the other players try to guess what word they 
are drawing, similar to games such as Pictionary. Users can either create an account or play as a guest user.
In order to play, players must either create or join a lobby, where they can change the game settings, such
as number of rounds or turn duration. The guessers see what the drawer is drawing in real time, and can use the
chat window to both just chat and guess the word. Once the time has run out or all players have guessed correctly,
each player gets points, and at the end of the game a final leaderboard is displayed, showing who won. 

This is the repository for the backend part of the implementation. The frontend can be found 
[here](https://github.com/sopra-fs24-group-43/client).

## Technolgies

The backend is written in Java and uses the spring boot framework and gradle to build the application. In order 
to keep track of the users a JPA repository is used. For server-client communication we use REST API, as well as 
Websockets, which use the STOMP messaging protocol. The words are given by an external API called 
[Datamuse](https://www.datamuse.com/api/)

## High-Level Components

### [UserService](https://github.com/sopra-fs24-group-43/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java) and [UserController](https://github.com/sopra-fs24-group-43/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/UserController.java)

These components keep track of and handle all user related tasks outside the game itself. It uses REST to handle 
requests from the client.

### [WebsocketController](https://github.com/sopra-fs24-group-43/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/WebSocketController.java) and [Game](https://github.com/sopra-fs24-group-43/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Game.java)

Once a user joins a lobby, a player is created and added to the game, representing that user. From this point on, 
Communication is handled through websockets, as many features require the server to send information to the 
clients unrequested.

## Launch & Deployment

### Build

    ./gradlew build

### Run

    ./gradlew bootRun

### Test

    ./gradlew test

## Roadmap

- Custom word lists
- Audio Feedback on events such as a correct guess, turn end, etc.
- An achievements system

## Authors and acknowledgement

SoPra FS24 Group 43 consists of  [Markiian Dobosh](https://github.com/MarkiianDobosh), 
[Dominique Heller](https://github.com/dominiqueheller), [Simon Klipp](https://github.com/simonkli), 
[FLorian Mattmüller](https://github.com/FloMatt12), and [Robin Stirnimann](https://github.com/RobinStirnimann)

Special thanks to our teaching assistant [Marco Leder](https://github.com/marcoleder).

## License

Apache-2.0 license

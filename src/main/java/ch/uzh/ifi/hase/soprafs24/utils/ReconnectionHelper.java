package ch.uzh.ifi.hase.soprafs24.utils;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.QuestionToSend;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.ReconnectionDTO;

public class ReconnectionHelper {
    private WebSocketService webSocketService;

    public ReconnectionHelper(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    public void reconnectionhelp(int userId, boolean reconOrReload) { //reconOrReload true -> only send canvad, false -> reconnect and if accepted send canvas and intigrate
        if (PlayerRepository.findByUserId(userId) == null) {
            System.out.println("alertrecon: was not a player before reconnecting");
        }
        else {  //what if gameId = -1
            Player player = PlayerRepository.findByUserId(userId);
            int gameId = player.getGameId();
            System.out.println("alertrecon: userId and gameId: " + userId + ", " + gameId);
            if (gameId == -1) {
                System.out.println("alertrecon: was a guest player with gameId = -1");
                return;  //halts
            }
            Game game = GameRepository.findByGameId(gameId);
            if (game == null) {
                System.out.println("alertrecon: real player had a gameId that cant be tracked to a game"); //this should never happen
                return; //halts
            }
            if (game.getPlayers().containsKey(userId) && game.getGameStarted()) {
                if (reconOrReload) {
                    QuestionToSend questionToSend = new QuestionToSend("sendcanvasforrecon");
                    questionToSend.setUserId(userId);
                    this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);
                }
                else {
                    System.out.println("alertrecon: was a player before reconnecting and game is still running");
                    ReconnectionDTO reconnectionDTO = new ReconnectionDTO();
                    reconnectionDTO.setType("ReconnectionDTO");
                    reconnectionDTO.setGameId(player.getGameId());
                    reconnectionDTO.setRole(player.getRole());
                    this.webSocketService.sendMessageToClients("/topic/landing/alertreconnect/" + userId, reconnectionDTO);  //add gameStarted variable?
                }
            }
            else {
                System.out.println("alertrecon: was a player before reconnecting but his game is not started or ended");

            }
        }
    }
}

package ch.uzh.ifi.hase.soprafs24.websocket;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.service.TimerService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.QuestionToSend;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@Component
public class WebSocketEventListener {
    WebSocketService webSocketService;
    TimerService timerService;

    public WebSocketEventListener(WebSocketService webSocketService, TimerService timerService) {
        this.webSocketService = webSocketService;
        this.timerService = timerService;
    }
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        //System.out.println(headerAccessor);
        Integer userId = (Integer) headerAccessor.getSessionAttributes().get("userId");
        //GameRepository.printAllAll();
        if (userId == null) {
            //System.out.println("User had connection without userId in SessAttr");
        }
        else {
            System.out.println("userId: "+userId);
            Player player = PlayerRepository.findByUserId(userId);
            if (player == null) { //is he a player already?
                System.out.println("User is not a player");
            }
            else {
                int gameId = player.getGameId();
                System.out.println("gameId of user: " + gameId);
                System.out.println("player role: "+player.getRole());

                if (gameId == -1) {  //is he a guestplayer so no full player yet?
                    System.out.println("User is a guest with gameId = -1");
                }
                else {
                    Game game = GameRepository.findByGameId(gameId);  //can be null!!!
                    System.out.println("gameStarted: "+game.getGameStarted());
                    Boolean reload = (Boolean) headerAccessor.getSessionAttributes().get("reload");
                    System.out.println("reload: "+reload);

                    if (!game.getGameStarted()) {  //has the game already started?

                        if (reload == null || !reload) {  //was it a disconnect? -> give no time to reconnect
                            if (player.getRole().equals("admin")) {  //admin or player
                                game.deletegame(gameId);
                                QuestionToSend questionToSend = new QuestionToSend();
                                questionToSend.setType("deletegame");
                                this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);  //for the players in the Lobby
                                this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames

                            }
                            else {
                                game.leavegame(userId, gameId);
                            }
                        }
                        else {  //was it a reload -> give time to reconnect
                            if (player.getRole().equals("admin")) {  //admin or player
                                timerService.doTimerForReloadDisc(gameId, userId, "inLobbyAdmin");  //deletegame
                            }
                            else {
                                timerService.doTimerForReloadDisc(gameId, userId, "inLobbyPlayer");  //leavegame

                            }
                        }
                    }
                    else {
                        if (reload == null || !reload) {  //was it a reload or a disconnect?
                            game.lostConnectionToPlayer(userId, gameId);
                        }
                        else {
                            timerService.doTimerForReloadDisc(gameId, userId, "inGame"); //needs a action field
                        }
                    }

                }
            }
        }
    }
}

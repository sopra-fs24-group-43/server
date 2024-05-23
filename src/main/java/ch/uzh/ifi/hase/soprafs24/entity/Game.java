package ch.uzh.ifi.hase.soprafs24.entity;
import java.util.*;


import ch.uzh.ifi.hase.soprafs24.external_api.GetWordlist;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;



import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TimerRepository;
import ch.uzh.ifi.hase.soprafs24.service.TimerService;
import ch.uzh.ifi.hase.soprafs24.utils.PointCalculatorDrawer;
import ch.uzh.ifi.hase.soprafs24.utils.PointCalculatorGuesser;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Answer;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;


import java.util.concurrent.atomic.AtomicInteger;

import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LobbyInfo;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.QuestionToSend;
import lombok.*;

import ch.uzh.ifi.hase.soprafs24.controller.WebSocketController;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import org.springframework.web.client.RestTemplate;

/*
public class Game {
     public String totalRounds;
     public String totalPlayers;
     public String roundLength;
     public Game (String r,String p, String l) {
         totalRounds = r;
         totalPlayers = p;
         roundLength = l;

     }
}
*/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Game {
    private boolean gameStarted;  //is set to true once get startgame was called
    private RandomGenerators randomGenerators;
    private HashMap<Integer, Player> players; //
    private Player admin; //
    private int gameId; //not done yet
    private Date creationDate; //
    private List<String> wordList; //init null, is a List not a ArrayList!!!
    //Settings (all accessible to admin, the ones we dont implement yet can just be a default value )
    private int currentWordIndex;
    private int maxPlayers; //
    private int maxRounds; //
    private int turnLength; //in seconds
    private String gamePassword; //not done yet, can be left for changesettings
    private List<String> genres; //
    private ArrayList<Integer> wordLength; //not sure if necessary
    private String lobbyName; //


    private HashMap<Player, Integer> points;
    private HashMap<Player, Integer> pointsOfCurrentTurn;
    private LeaderBoardDTO leaderboardDTO;
    //variables used to keep track of the game state
    private ArrayList<Answer> answers;
    private String actualCurrentWord;
    private int answersReceived;
    private int Drawer; //identified with index in drawingOrder
    private ArrayList<Integer> drawingOrder; //identified with userId
    private ArrayList<Integer> drawingOrderLeavers; // at the index of the leaver will be a 0
    private int currentRound; //incremented once currentturn = connectedPlayers and startturn is called
    private int currentTurn; //incremented on startturn
    private HashMap<Integer, Player> connectedPlayers; //someone might disconnect and then we have to skip his turn (not needed for M3 so just = players)
    private Boolean endGame;
    private Boolean isInGuessingPhase;
    private int timeLeftInTurn;
    private int currentCorrectGuesses;
    private int remainingTime;
    private HashMap<String, Boolean> playerCorrectGuesses;
    private HashMap<String, Integer> playerIdByName;
    private Boolean roundIsActive;
    private int playersOriginally;

    private String gamePhase; // "inLobby" = after creategame, "started" = after startgame,"choosing" = after nextturn, "drawing" = after sendchosenword, "leaderbaord" = after endturn (sendguess condition and timerService condition)
    private WebSocketService webSocketService;
    private TimerService timerService;
    private HashMap<String,List<String>> wordlists;
    private String reason;
    private GetWordlist getWordlist;
    //private int nr_genres;

     public Game(Player admin, WebSocketService webSocketService, TimerService timerService, RandomGenerators randomGenerators, GetWordlist getWordlist) {
        this.gameStarted = false;
        this.endGame = false;
        this.randomGenerators = randomGenerators;
        this.admin = admin;
        this.players = new HashMap<Integer, Player>();
        this.players.put(admin.getUserId(), admin);
        this.creationDate = new Date();
        this.wordList = new ArrayList<>();
        this.maxPlayers = 5;
        this.maxRounds = 5;
        this.turnLength = 60;
        this.gamePassword = this.randomGenerators.PasswordGenerator();
        this.lobbyName = this.admin.getUsername() + "'s lobby";
        this.points = new HashMap<Player, Integer>();
        this.pointsOfCurrentTurn = new HashMap<Player, Integer>();
        this.answersReceived = 0;
        this.currentRound = 0;
        this.currentTurn = 0;
        this.connectedPlayers = new HashMap<Integer, Player>();
        this.connectedPlayers.put(admin.getUserId(), admin);
        this.drawingOrder = new ArrayList<Integer>();
        this.drawingOrderLeavers = new ArrayList<Integer>();
        this.currentCorrectGuesses = 0;
        this.remainingTime = 0;
        this.playerCorrectGuesses = new HashMap<String, Boolean>();
        this.playerIdByName = new HashMap<String, Integer>();
        this.playerIdByName.put(admin.getUsername(), admin.getUserId());
        this.roundIsActive = false;
        this.gamePhase = "inLobby";
        this.webSocketService = webSocketService;
        this.timerService = timerService;
        this.answers = new ArrayList<Answer>();
        this.wordlists = new HashMap<>();
        this.genres = new ArrayList<>();
        this.getWordlist = getWordlist;

    }
    public Boolean getGameStarted() {
        return this.gameStarted;
    }
    public void setGameStarted(Boolean gameStarted){
        this.gameStarted = gameStarted;
    }
    public void addPlayer(Player player) {
        this.players.put(player.getUserId(), player);
        this.playerIdByName.put(player.getUsername(), player.getUserId());
    }
    public void setGameId(int gameId) {this.gameId = gameId;}

    public int addAnswer(Answer answer) {
        this.answers.add(answer);
        this.answersReceived++;
        if(this.gamePhase == "inLobby"){
            return 3;
        }
        String name = answer.getUsername();
        Player player = players.get(playerIdByName.get(name));
        Player drawer = players.get(drawingOrder.get(Drawer));
        if(!this.getGamePhase().equals("drawing") || name.equals(drawer.getUsername())){
            return 0;}
        if (this.playerCorrectGuesses.get(name)){
            return 2;
        }
        if (compareAnswer(answer.getAnswerString()) == 1){
            this.currentCorrectGuesses++;
            this.pointsOfCurrentTurn.put(player, PointCalculatorGuesser.calculate(turnLength, timeLeftInTurn, currentCorrectGuesses));
            this.pointsOfCurrentTurn.put(drawer, PointCalculatorDrawer.calculate(turnLength, timeLeftInTurn, currentCorrectGuesses) + pointsOfCurrentTurn.get(drawer));
            this.playerCorrectGuesses.put(name, true);
            return 1;
        }
        return 0;
    }

    public int compareAnswer(String answer) {
        if(answer.equalsIgnoreCase(this.actualCurrentWord.replaceAll("\"",""))){
            return 1;
        } else {
            return 0;
        }
    }

    public void removePlayer(int userId){  //only use for leave game!
        this.playerIdByName.remove(players.get(userId).getUsername());
        this.players.remove(userId);
        this.connectedPlayers.remove(userId);
    }

    public void updateGameSettings(GameSettingsDTO gameSettingsDTO) {
        if (gameSettingsDTO.getMaxPlayers() != null) {
            this.maxPlayers = gameSettingsDTO.getMaxPlayers();
        }
        if (gameSettingsDTO.getMaxRounds() != null) {
            this.maxRounds = gameSettingsDTO.getMaxRounds();
        }
        if (gameSettingsDTO.getTurnLength() != null) {
            this.turnLength = gameSettingsDTO.getTurnLength();
        }
        if (gameSettingsDTO.getGamePassword() != null) {
            this.gamePassword = gameSettingsDTO.getGamePassword();
        }
        if (gameSettingsDTO.getLobbyName() != null) {
            this.lobbyName = gameSettingsDTO.getLobbyName();
        }
        if (gameSettingsDTO.getGenres() != null) {
            this.genres = gameSettingsDTO.getGenres();
        }
    }

    public GameSettingsDTO getGameSettingsDTO() {
        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setType("GameSettingsDTO");
        gameSettingsDTO.setMaxPlayers(this.maxPlayers);
        gameSettingsDTO.setMaxRounds(this.maxRounds);
        gameSettingsDTO.setTurnLength(this.turnLength);
        gameSettingsDTO.setGamePassword(this.gamePassword);
        gameSettingsDTO.setLobbyName(this.lobbyName);
        gameSettingsDTO.setGenres(this.genres);
        return gameSettingsDTO;
    }

    public List<String> setWordList(List<String> genres) {//genres: "Science", "Philosophy", "Sport", "Animal", "Plant", "life", "human"
        //ArrayList<Integer> listlengths = new ArrayList<>();
        //ArrayList<String> genres = new ArrayList<>();
        //genres.addAll(selected_genres);
        //genres.addAll(Arrays.asList("Science", "Philosophy", "Sport", "Animal", "Plant", "life", "human"));
        ArrayList<String> tempWordList = new ArrayList<>();
        for (int i = 0; i < genres.size(); i++) {
            //this.wordlists.put(genres.get(i), getWordlist.getWordlist2(genres.get(i)));
            tempWordList.addAll(getWordlist.getWordlist2(genres.get(i)));
            //listlengths.add(getWordlist.getWordlist2(genres.get(i)).size());
        }
        tempWordList = randomGenerators.DoShuffle(tempWordList);  //instead of Collections.shuffle(wordList);

        //System.out.println(wordList);
        //int nr_words = this.maxRounds*this.playersOriginally*3;
        //this.nr_genres = nr_words/40;

        //List<String> wordlist1 = getWordlist.getWordlist2(genre);
        //Collections.shuffle(wordlist1);//list was ordered in relevance to genre, so shuffling induces unrelated words...
        //List<String> wordlist2 = wordlist1.subList(0,nr);
        //List<String> wordlist2 = wordList.subList(0,nr_words);
        return tempWordList;
    }
/*
    public List<String> shufflewordList() {
        ArrayList<String> wordpool = new ArrayList<String>();
        List<String> wordpool2;
        Collections.addAll(wordpool, "wedding", "interaction", "cheek", "quantity",
                "manufacturer", "city", "assignment", "tale", "actor", "bonus", "ratio", "energy",
                "son", "property", "collection", "theory", "procedure", "possession", "recommendation", "sister",
                "currency", "diamond", "stranger", "cabinet", "variation", "dad", "winner", "sir",
                "student", "event", "studio", "library", "highway", "category", "friendship", "camera",
                "quality", "society", "thought", "atmosphere", "signature", "television", "audience",
                "entry", "reception", "revolution", "hearing", "army", "conversation", "cancer");
        Collections.shuffle(wordpool);
        wordpool2 = wordpool.subList(0,25);

        return wordpool2;
    }
*/
    public void startGame() {
        //this.wordList=shufflewordList();
        //ArrayList<String> list = new ArrayList<>();
        //list.add("Science");
        //list.add("Animal");
        //this.genres = list;
        this.wordList = setWordList(this.genres);
        this.gameStarted = true;
        this.gamePhase = "started";
        this.players.forEach((id, player) -> {
            this.points.put(player, 0);
            this.pointsOfCurrentTurn.put(player, 0);
            this.drawingOrder.add(id);
            this.drawingOrderLeavers.add(id);
            this.playerCorrectGuesses.put(player.getUsername(), false);
            this.connectedPlayers.put(id, player);
        });
        this.Drawer = -1;
        this.currentWordIndex = 1;
        this.currentRound = 1;
        this.currentTurn = 0;
        this.playersOriginally = players.size();
    }
    public void terminategame(int gameId, String reason) { //used if a player leaves the game and there are too little players or the admin left
        //allowed reasons: "admin left", "too few players", "normal"
        System.out.println("terminategame");
        Game game = GameRepository.findByGameId(gameId);
        game.setEndGame(true);
        game.setReason(reason);
        game.setCurrentRound(game.getMaxRounds());
        game.setCurrentTurn(game.getPlayersOriginally());
        game.setGamePhase("leaderboard");
        LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();
        leaderboardDTO.setReason(reason);

        GameStateDTO gameStateDTO = game.receiveGameStateDTO();
        TimerRepository.haltTimer(gameId);
        GameRepository.printAllAll();
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", leaderboardDTO); //endturn
        timerService.doTimer(20,1, gameId, "/topic/games/" + gameId + "/general", "leaderboard"); //timer to look at leaderboard
    }
    public void intigrateIntoGame(int userId, int gameId) {  //when players disconnect from running game and then reconnect to it
        System.out.println("intigrateIntoGame, gameId, userId: "+ gameId + ", "  + userId);
        Game game = GameRepository.findByGameId(gameId);
        if (game == null) {
            System.out.println("game was null when intigrating");
            return;
        }
        Player player = PlayerRepository.findByUserId(userId);
        HashMap<Integer, Player> connectedPlayers = game.getConnectedPlayers();
        connectedPlayers.put(userId, player);
        game.setConnectedPlayers(connectedPlayers);
        for (int i = 0; i < game.getDrawingOrderLeavers().size(); i++){  //setting the 0 in DrawingOrderLeavers back to userId
            if (game.getDrawingOrderLeavers().get(i) == 0){
                ArrayList<Integer> DrawingOrderLeavers = game.getDrawingOrderLeavers();
                DrawingOrderLeavers.set(i, userId);
                game.setDrawingOrderLeavers(DrawingOrderLeavers);
                GameStateDTO gameStateDTO = game.receiveGameStateDTO();
                this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
                System.out.println("changed back all the settings");
                break;
            }
        }
    }
    public void lostConnectionToPlayer(int userId, int gameId) {
        Game game = GameRepository.findByGameId(gameId);
        System.out.println("executing lostConnectionToPlayer: "+ userId);
        Player player = game.getPlayers().get(userId);
        game.getConnectedPlayers().remove(userId);
        if (game.getAdmin().getUserId() == player.getUserId()) { //if leaver was admin
            this.terminategame(gameId, "admin left");
        }
        if (game.getConnectedPlayers().size() <= 1) { //if only 1 connectedplayer left
            this.terminategame(gameId, "too few players");
        }
        for (int id : game.getDrawingOrderLeavers() ) //setting leaver's index to 0 in DrawingOrderLeavers
            if (id == userId) {
                ArrayList<Integer> DrawingOrderLeavers = game.getDrawingOrderLeavers();
                System.out.println("DrawingOrderLeavers before change: "+DrawingOrderLeavers);
                int indexOfDrawer = DrawingOrderLeavers.indexOf(id);
                DrawingOrderLeavers.set(indexOfDrawer, 0);
                game.setDrawingOrderLeavers(DrawingOrderLeavers);
                System.out.println("connectedPlayers: "+game.getConnectedPlayers());
                System.out.println("DrawingOrderLeavers: "+game.getDrawingOrderLeavers());
                System.out.println("DrawingOrder: "+game.getDrawingOrderLeavers());
                System.out.println("his index in it: "+ indexOfDrawer);
                System.out.println("is he drawer: "+(indexOfDrawer == game.getDrawer()));
                System.out.println("GamePhase: "+ game.getGamePhase());
                GameStateDTO gameStateDTO = game.receiveGameStateDTO();
                this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
                if (indexOfDrawer == game.getDrawer() && (game.getGamePhase().equals("choosing") || game.getGamePhase().equals("drawing"))) {
                    //if this, then endturn
                    TimerRepository.haltTimer(gameId);   //halt timer
                    System.out.println("endturn by lostConnectionToPlayer");
                    if (game.getCurrentRound()==game.getMaxRounds() && game.getCurrentTurn()== game.getPlayersOriginally()) {  //endturn, gamePhase = leaderboard
                        game.setEndGame(true);
                    }
                    LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();
                    game.setGamePhase("leaderboard");
                    if (game.getEndGame()){
                        leaderboardDTO.setReason("normal");
                    }
                    this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", leaderboardDTO);
                    if (game.getEndGame()) { //more time to look at Podium (endGame)
                        timerService.doTimer(20,1, gameId, "/topic/games/" + gameId + "/general", "leaderboard"); //timer to look at leaderboard
                    }
                    else {
                        timerService.doTimer(5,1, gameId, "/topic/games/" + gameId + "/general", "leaderboard"); //timer to look at leaderboard
                    }
                }

            }
    }

    public void leavegame(int playerId, int gameId) {
        System.out.println("leavegame, playerId, gameId: "+playerId + ", "+ gameId);
        Game game = GameRepository.findByGameId(gameId);
        game.removePlayer(playerId);
        Player player = PlayerRepository.findByUserId(playerId);
        boolean wasAdmin = (player.getRole() == "admin");
        int currentPlayerCount = game.getPlayers().size();
        PlayerRepository.removePlayer(player.getUserId(), gameId);
        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("leavegame");
        //questionToSend.setLeaver(player);
        //questionToSend.setWasAdmin(wasAdmin); //what should happen if player was the admin? (delete game or give admin to other player?)
        //questionToSend.setCurrentPlayerCount(currentPlayerCount);
        LobbyInfo lobbyInfo = new LobbyInfo();
        lobbyInfo.setType("getlobbyinfo");
        lobbyInfo.setGameId(gameId);
        lobbyInfo.setPlayers(game.getPlayers());
        lobbyInfo.setGameSettingsDTO(game.getGameSettingsDTO());
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", lobbyInfo);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);
        this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames
    }
    public void deletegame(int gameId) {  //does nothing if game doesnt exist
        System.out.println("deletegame gameId: "+gameId);
        GameRepository.removeGame(gameId);
        HashMap<Integer, Player> players = PlayerRepository.findUsersByGameId(gameId); //<gameId, Player>
        players.forEach((key, value) -> {
            PlayerRepository.removePlayer(value.getUserId(), key);
        });
        PlayerRepository.removeGameId(gameId);

    }
    public void nextturn(int  gameId) {
        Game game = GameRepository.findByGameId(gameId);
        String turnOrRound;
        if (game.getCurrentTurn()==game.getConnectedPlayers().size()) {
            turnOrRound = "Round";
            game.setCurrentTurn(1);
            game.setCurrentRound(game.getCurrentRound()+1);
            choosenextdrawer(gameId);
            int currentWordIndex;
            if (game.getGamePhase().equals("started")) {
                currentWordIndex = 1;
            }
            else {
                currentWordIndex = game.getCurrentWordIndex() + 3;
            }
            game.setCurrentWordIndex(currentWordIndex);
        } else {
            turnOrRound = "Turn";
            game.setCurrentTurn(game.getCurrentTurn() + 1);
            choosenextdrawer(gameId);
            int currentWordIndex;
            if (game.getGamePhase().equals("started")) {
                currentWordIndex = 1;
            }
            else {
                currentWordIndex = game.getCurrentWordIndex() + 3;
            }
            game.setCurrentWordIndex(currentWordIndex);
        }
        pointsOfCurrentTurn.replaceAll((p, v) -> 0);
        playerCorrectGuesses.replaceAll((p, v) -> false);
        currentCorrectGuesses = 0;
        game.setGamePhase("choosing");
        System.out.println("NEXTTURN (T,R,I,action): " + game.getCurrentTurn() + ", "+ game.getCurrentRound() + ", " + game.getCurrentWordIndex() +", "+turnOrRound);


    }
    public void choosenextdrawer(int gameId) {
        Game game = GameRepository.findByGameId(gameId);
        int Drawer = (game.getDrawer()+1);
        Drawer = Drawer % game.getDrawingOrderLeavers().size();
        System.out.println("DrawingOrderLeavers: "+game.getDrawingOrderLeavers());
        while (game.getDrawingOrderLeavers().get(Drawer) == 0) { //runs forever
            System.out.println("changed Drawer from " + (Drawer-1) + "to " + Drawer + "but" + Drawer + "was not connected");
            Drawer = Drawer+1; //should be Drawer
            Drawer = Drawer % game.getDrawingOrderLeavers().size();
        }
        System.out.println("changed Drawer from " + (Drawer-1) + "to " + Drawer);
        game.setDrawer(Drawer);
    }

    public String getCurrentWord(){
        return this.wordList.get(this.currentWordIndex);
    }

    public GameStateDTO receiveGameStateDTO() {
        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setType("GameStateDTO");
        gameStateDTO.setEndGame(this.endGame);
        gameStateDTO.setConnectedPlayers(this.connectedPlayers);
        gameStateDTO.setPlayersOriginally(this.playersOriginally);
        gameStateDTO.setCurrentRound(this.currentRound);
        gameStateDTO.setCurrentTurn(this.currentTurn);

        ArrayList<String> newthreeWords = new ArrayList<>();
        if (!wordList.isEmpty()) {
            newthreeWords.add(this.wordList.get(this.currentWordIndex - 1));
            newthreeWords.add(this.wordList.get(this.currentWordIndex));
            newthreeWords.add(this.wordList.get(this.currentWordIndex + 1));
        }
        gameStateDTO.setThreeWords(newthreeWords);
        gameStateDTO.setDrawer(this.Drawer);
        gameStateDTO.setDrawingOrder(this.drawingOrderLeavers);  //changed to drawingOrderLeavers
        gameStateDTO.setMaxRounds(this.maxRounds);
        gameStateDTO.setGamePhase(this.gamePhase);
        gameStateDTO.setActualCurrentWord(this.actualCurrentWord);
        return gameStateDTO;
    }

    public LeaderBoardDTO calculateLeaderboard() {
        LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
        leaderboardDTO.setType("leaderboard");
        leaderboardDTO.setEndGame(this.endGame);
        this.pointsOfCurrentTurn.forEach((key, value) -> {this.points.put(key, this.points.get(key)+value);});

        leaderboardDTO.setUserIdToPlayer(this.players);

        HashMap<Integer, Integer> map = new HashMap<>();
        HashMap<Integer, Integer> map2 = new HashMap<>();
        LinkedHashMap<Integer, Player> map3 = new LinkedHashMap<>();

        this.points.forEach((key, value) -> {map.put(key.getUserId(), value);});
        leaderboardDTO.setTotalPoints(map);
        this.points.forEach((key, value) -> {key.setTotalPoints(value);});

        this.pointsOfCurrentTurn.forEach((key, value) -> {map2.put(key.getUserId(), value);});
        leaderboardDTO.setNewlyEarnedPoints(map2);
        this.pointsOfCurrentTurn.forEach((key, value) -> {key.setNewlyEarnedPoints(value);});


        leaderboardDTO.setPodium(assignPodiumPosition());
        this.assignPodiumPosition().forEach((key, value) -> {
            PlayerRepository.findByUserId(key).setPodiumPosition(value);
        });

        int i = 1;
        while (i<leaderboardDTO.getPodium().size()+1) {
            Integer finalI = i;
            leaderboardDTO.getPodium().forEach((key, value) -> {
                if (finalI.equals(value)) {
                    map3.put(key,PlayerRepository.findByUserId(key));
                }
            });
            i=i+1;}
        leaderboardDTO.setUserIdToPlayerSorted(map3);

        return leaderboardDTO;

    }
    /*//old
    public LeaderBoardDTO calculateLeaderboard() {
        LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
        this.pointsOfCurrentTurn.forEach((key, value) -> {this.points.put(key, this.points.get(key)+value);});

        leaderboardDTO.setUserIdToPlayer(this.players);

        HashMap<Integer, Integer> map = new HashMap<>();
        HashMap<Integer, Integer> map2 = new HashMap<>();

        this.points.forEach((key, value) -> {map.put(key.getUserId(), value);});
        leaderboardDTO.setTotalPoints(map);
        this.points.forEach((key, value) -> {key.setTotalPoints(value);});

        this.pointsOfCurrentTurn.forEach((key, value) -> {map2.put(key.getUserId(), value);});
        leaderboardDTO.setNewlyEarnedPoints(map2);
        this.pointsOfCurrentTurn.forEach((key, value) -> {key.setNewlyEarnedPoints(value);});


        leaderboardDTO.setPodium(assignPodiumPosition());
        this.assignPodiumPosition().forEach((key, value) -> {
            PlayerRepository.findByUserId(key).setPodiumPosition(value);
        });

        return leaderboardDTO;

    }
*/
    public HashMap<Integer, Integer> assignPodiumPosition() {
        HashMap<Integer, Integer> map = new HashMap<>();
        this.points.forEach((key, value) -> {
            AtomicInteger i = new AtomicInteger();
            i.set(1);
            this.points.forEach((key2, value2) -> {
                if (value<value2) {
                    i.getAndIncrement();
                }
            });
            while (map.containsValue(i.get())) {
                i.getAndIncrement();
            }
            map.put(key.getUserId(), i.get());
        });
        return map;
    }

}

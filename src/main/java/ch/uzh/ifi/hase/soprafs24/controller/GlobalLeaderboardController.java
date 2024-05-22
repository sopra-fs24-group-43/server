package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.rest.dto.GlobalLeaderboardGetDTO;
import ch.uzh.ifi.hase.soprafs24.service.GlobalLeaderboardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalLeaderboardController {

    private final GlobalLeaderboardService globalLeaderboardService;

    public GlobalLeaderboardController(GlobalLeaderboardService globalLeaderboardService) {
        this.globalLeaderboardService = globalLeaderboardService;
    }

    @GetMapping("/globalLeaderboard")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GlobalLeaderboardGetDTO getGlobalLeaderboard(){
        return globalLeaderboardService.makeLeaderboard();
    }

}

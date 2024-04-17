package ch.uzh.ifi.hase.soprafs24.websockets.dto;

import java.util.Date;

public class ChatMessageDTO {
    
    private String content;
    private Long playerID;
    private Date timestamp;
    private String token;

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public Long getPlayerID(){
        return playerID;
    }

    public void setPlayerID(Long playerID){
        this.playerID = playerID;
    }

    public Date getDate(){
        return timestamp;
    }

    public void setTimestamp(Date timestamp){
        this.timestamp = timestamp;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }

}

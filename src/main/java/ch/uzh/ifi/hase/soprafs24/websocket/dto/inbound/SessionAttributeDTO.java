package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;

public class SessionAttributeDTO {
    private Integer userId;
    private Boolean reload;

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public Integer getUserId() {
        return this.userId;
    }
    public void setReload(Boolean reload) {this.reload = reload;}
    public Boolean getReload() {
        return this.reload;
    }
}

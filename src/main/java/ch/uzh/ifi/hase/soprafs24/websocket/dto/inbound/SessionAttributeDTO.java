package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;

public class SessionAttributeDTO {
    private int userId;
    private boolean reload;

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getUserId() {
        return this.userId;
    }
    public void setReload(boolean reload) {this.reload = reload;}
    public boolean getReload() {
        return this.reload;
    }
}

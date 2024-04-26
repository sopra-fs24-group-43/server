package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
@Service
@Transactional
public class WebSocketService {


    @Autowired
    protected SimpMessagingTemplate smesg;
    public void sendMessageToClients(String destination, Object dto) {
        this.smesg.convertAndSend(destination, dto);

    }

}
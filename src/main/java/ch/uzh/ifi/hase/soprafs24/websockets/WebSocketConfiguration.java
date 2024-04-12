package ch.uzh.ifi.hase.soprafs24.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableWebSocketMessageBroker;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer{

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws").withSocketJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic")
    }
}

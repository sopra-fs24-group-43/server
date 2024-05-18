
/*
package ch.uzh.ifi.hase.soprafs24.controller.wscontroller;




import ch.uzh.ifi.hase.soprafs24.controller.wscontroller.utils.TestStompPrincipal;
import ch.uzh.ifi.hase.soprafs24.websocket.PrincipalHandshake;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.InboundPlayer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {
        WebSocketControllerTest3.TestWebSocketConfig.class
})
public class WebSocketControllerTest3 {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private AbstractSubscribableChannel clientInboundChannel;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired private AbstractSubscribableChannel clientOutboundChannel;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired private AbstractSubscribableChannel brokerChannel;

    private TestChannelInterceptor clientOutboundChannelInterceptor;

    private TestChannelInterceptor brokerChannelInterceptor;

    @BeforeEach
    public void setUp() throws Exception {

        this.brokerChannelInterceptor = new TestChannelInterceptor();
        this.clientOutboundChannelInterceptor = new TestChannelInterceptor();

        this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
        this.clientOutboundChannel.addInterceptor(this.clientOutboundChannelInterceptor);
    }

    @Test
    public void creategameTest() throws Exception {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("app/landing/creategame");
        headers.setSessionId("0");
        headers.setUser(new TestStompPrincipal(UUID.randomUUID().toString()));
        headers.setSessionAttributes(new HashMap<>());
        InboundPlayer inboundPlayer = new InboundPlayer();
        inboundPlayer.setType("InboundPlayer");
        inboundPlayer.setUsername("Florian");
        inboundPlayer.setuserId(1);
        inboundPlayer.setGameId(1);
        ArrayList<Integer> friends = new ArrayList<Integer>();
        friends.add(2);
        friends.add(2);
        inboundPlayer.setFriends(friends);
        inboundPlayer.setRole("admin");
        Message<InboundPlayer> message = MessageBuilder.createMessage(inboundPlayer, headers.getMessageHeaders());

        this.clientOutboundChannelInterceptor.setIncludedDestinations("/app/landing/creategame");
        this.clientInboundChannel.send(message);

        Message<?> reply = this.clientOutboundChannelInterceptor.awaitMessage(5);
        assertNotNull(reply);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/app/landing/creategame", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$[0].company").assertValue(json, "Citrix Systems, Inc.");
        new JsonPathExpectationsHelper("$[1].company").assertValue(json, "Dell Inc.");
        new JsonPathExpectationsHelper("$[2].company").assertValue(json, "Microsoft");
        new JsonPathExpectationsHelper("$[3].company").assertValue(json, "Oracle");

    }


    @Configuration
    @EnableScheduling
    @ComponentScan(
            basePackages="ch.uzh.ifi.hase.soprafs24",
            excludeFilters = @ComponentScan.Filter(type= FilterType.ANNOTATION, value = Configuration.class)
    )
    @EnableWebSocketMessageBroker
    static class TestWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

        @Autowired
        Environment env;

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("ws").setAllowedOriginPatterns("*")
                    .setHandshakeHandler(new PrincipalHandshake())
                    .withSockJS();
        }
        @Override
        public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
            registration.setSendBufferSizeLimit(512 * 1024); //521K = 512 * 1024, 1024 = Kilo, 512K is the default
            registration.setMessageSizeLimit(128 * 1024); //64K is the default
        }
        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
//			registry.enableSimpleBroker("/queue/", "/topic/");
            registry.enableStompBrokerRelay( "/topic/");
            registry.setApplicationDestinationPrefixes("/app");
        }
    }
}
*/
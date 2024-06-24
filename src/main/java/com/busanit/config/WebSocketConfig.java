package com.busanit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커가 "/queue"로 시작하는 주소로 메세지를 라우팅하도록 설정합니다.
        registry.enableSimpleBroker("/queue", "/Topic");
        // 어플리케이션의 메시지 핸들러가 "/app"으로 시작하는 주소를 처리하도록 설정합니다.
        registry.setApplicationDestinationPrefixes("/app");
//        registry.setApplicationDestinationPrefixes("/aaa");
        // 특정 사용자에게 메시지를 보내기 위한 프리픽스 설정
        registry.setUserDestinationPrefix("/user");
    }

}



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

    // WebSocket 엔드포인트를 설정하는 메서드
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        "/queue"와 "/Topic"으로 시작하는 메시지 목적지에 대해 내장된 메시지 브로커를 활성화
        registry.enableSimpleBroker("/queue", "/Topic");
//        애플리케이션의 메시지 핸들러로 라우팅할 목적지의 접두사를 "/app"으로 설정
        registry.setApplicationDestinationPrefixes("/app");
        // 특정 사용자에게 보내는 메시지의 접두사를 "/user"로 설정
        registry.setUserDestinationPrefix("/user");
    }

}



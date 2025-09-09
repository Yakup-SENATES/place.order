package jacop.place.order.service;

import org.springframework.stereotype.Component;

// bir kafka broker client varlığı assume edilmiştir
@Component
public interface MessageBrokerClient {
    void publish(String topic, String payload);
}
package jacop.place.order.service;

import jacop.place.order.repo.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutBoxPublisher {

    private final OutboxRepository outboxRepository;
    private final MessageBrokerClient brokerClient;

    @Scheduled(fixedDelay = 5000) // every 5 sec
    @Transactional
    public void publishPendingEvent() {
        var events = outboxRepository.findAllByPublished(false);

        for (var event : events) {
            try {
                brokerClient.publish(event.getType(), event.getPayload());
                event.setPublished(true);
                outboxRepository.save(event);
            } catch (Exception e) {
                // log & retry next run
                System.err.println("Failed to publish: " + e.getMessage());
            }
        }

    }


}

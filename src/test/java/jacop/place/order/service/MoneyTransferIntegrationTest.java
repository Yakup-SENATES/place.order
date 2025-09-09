package jacop.place.order.service;

import jacop.place.order.entity.Account;
import jacop.place.order.entity.OutboxEvent;
import jacop.place.order.repo.AccountRepository;
import jacop.place.order.repo.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MoneyTransferIntegrationTest {

    TransferService transferService = mock(TransferService.class);
    AccountRepository accountRepository = mock(AccountRepository.class);
    OutboxRepository outboxRepository = mock(OutboxRepository.class);
    OutBoxPublisher outBoxPublisher = mock(OutBoxPublisher.class);

    @MockitoBean
    MessageBrokerClient messageBrokerClient; // we will catch the published message

    @Test
    void endToEntTransferFlow() {
        // given
        Account sender = accountRepository.save(new Account("Yakup", 1000));
        Account receiver = accountRepository.save(new Account("Gokhan", 500));

        // when - make transfer
        transferService.transfer(sender.getId(), receiver.getId(), 250);

        //then - db must updated
        Account updatedSender = accountRepository.findById(sender.getId()).orElseThrow();
        Account updatedReceiver = accountRepository.findById(receiver.getId()).orElseThrow();

        assertEquals(updatedSender.getBalance(), 750);
        assertEquals(updatedReceiver.getBalance(), 750);

        //and - outbox must saved
        //when(outboxRepository.save(any())).thenReturn()
        var events = outboxRepository.findAll();
        assertThat(events).hasSize(1);
        OutboxEvent outboxEvent = events.get(0);

        assertThat(outboxEvent.isPublished()).isFalse();

        // when - call scheduler
        outBoxPublisher.publishPendingEvent();

        // then broker should get the event
        verify(messageBrokerClient,times(1) ).publish(eq("TRANSFER_COMPLETED"), contains("\"amount\":200"));

        // and: outbox flag must updated
        OutboxEvent updatedEvent = outboxRepository.findById(outboxEvent.getId()).orElseThrow();
        assertThat(updatedEvent.isPublished()).isTrue();


    }

}
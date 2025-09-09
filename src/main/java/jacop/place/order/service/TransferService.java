package jacop.place.order.service;

import jacop.place.order.entity.Account;
import jacop.place.order.entity.OutboxEvent;
import jacop.place.order.repo.AccountRepository;
import jacop.place.order.repo.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final OutboxRepository outboxRepository;

    @Transactional
    public void transfer(Long senderId, Long receiverId, int amount) {

        Account sender = accountRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender not Found"));
        Account receiver = accountRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Receiver not Found"));

        // domain logic
        sender.debit(amount);
        receiver.credit(amount);

        // outbox event record
        String payload = """
            {
              "senderId": %d,
              "receiverId": %d,
              "amount": %d
            }
            """.formatted(senderId, receiverId, amount);

        outboxRepository.save(new OutboxEvent("TRANSFER_COMPLETED", payload));

    }


}

package com.bank.core.events;

import com.bank.transactions.models.Transaction;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class NewTransactionEvent extends ApplicationEvent {
    private final Transaction transaction;

    public NewTransactionEvent(Object source, Transaction transaction) {
        super(source);
        this.transaction = transaction;
    }
}

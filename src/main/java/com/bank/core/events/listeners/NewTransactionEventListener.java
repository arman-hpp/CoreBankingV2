package com.bank.core.events.listeners;

import com.bank.core.events.NewTransactionEvent;
import com.bank.core.services.NotifierService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NewTransactionEventListener implements ApplicationListener<NewTransactionEvent> {
    private final NotifierService _notifierService;

    public NewTransactionEventListener(NotifierService notifierService) {
        _notifierService = notifierService;
    }

    @Override
    public void onApplicationEvent(NewTransactionEvent event) {
        _notifierService.Notify(event.getTransaction().toString());
    }
}
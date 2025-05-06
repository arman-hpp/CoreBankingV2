package com.bank.core.configs;

import org.hibernate.Hibernate;
import org.modelmapper.Condition;
import org.modelmapper.spi.MappingContext;

public class HibernateInitializedCondition<S, D> implements Condition<S, D> {
    @Override
    public boolean applies(MappingContext<S, D> context) {
        return Hibernate.isInitialized(context.getSource());
    }
}

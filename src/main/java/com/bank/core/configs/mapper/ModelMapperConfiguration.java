package com.bank.core.configs.mapper;

import com.bank.users.dtos.UserDto;
import com.bank.users.models.User;
import org.hibernate.Hibernate;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        var mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.getConfiguration().setPropertyCondition(new HibernateInitializedCondition<>());
        mapper.addMappings(new PropertyMap<User, UserDto>() {
            @Override
            protected void configure() {
                skip(destination.getPassword());  // Ignore password field
            }
        });
        return mapper;
    }

    private static class HibernateInitializedCondition<S, D> implements Condition<S, D> {
        @Override
        public boolean applies(MappingContext<S, D> context) {
            return Hibernate.isInitialized(context.getSource());
        }
    }
}


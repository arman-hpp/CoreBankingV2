package com.bank.configs;

import com.bank.dtos.users.UserDto;
import com.bank.models.users.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
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
}


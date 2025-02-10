package com.coms309.duality.configurations;

import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.websockets.NotificationServer;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    private final PersonRepository personRepository;

    public NotificationConfig(PersonRepository personRepository) {
            this.personRepository = personRepository;
        }

        @PostConstruct
        public void init() {
            NotificationServer.setPersonRepository(personRepository);
        }
    }

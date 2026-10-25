package lv.bootcamp.shelter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Component;

@Component
public class SecurityLoginInfoLogger implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(SecurityLoginInfoLogger.class);

    private final SecurityProperties securityProperties;

    public SecurityLoginInfoLogger(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Shelter UI sign-in username: {}", securityProperties.getUser().getName());
    }
}

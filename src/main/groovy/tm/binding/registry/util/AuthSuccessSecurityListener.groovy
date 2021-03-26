package tm.binding.registry.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent

class AuthSuccessSecurityListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private static final Logger log = LoggerFactory.getLogger(AuthSuccessSecurityListener.class);

    @Override
    void onApplicationEvent(AuthenticationSuccessEvent event) {
        log.info("AUTHENTICATION SUCCESS: ")
    }

}

package tm.binding.registry.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.security.core.Authentication

import javax.servlet.http.HttpServletRequest

/**
 * A class for implementing custom methods for use in TBR @Secured annotations used to secure methods.
 * <br/><br/>
 */
class TBRSecurity {

    static Logger log = LoggerFactory.getLogger(TBRSecurity.class);

    boolean hasLock(Authentication auth, HttpServletRequest request) {
        log.info("Calling TfamSecurity.hasLock(${auth.principal.username}, ${request.getServletPath()})!")

        return true;
    }


}

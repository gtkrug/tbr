package tm.binding.registry


import grails.plugin.springsecurity.annotation.Secured


class ErrorController {

    def springSecurityService

    @Secured("permitAll")
    def notAuthorized401(){
        log.warn("User[@|red ${springSecurityService.currentUser ?: request.remoteAddr}|@] has requested unauthorized page: " +
                "${request.getAttribute('javax.servlet.error.request_uri')}")
    }//end notAuthorized401

}//end ErrorController()

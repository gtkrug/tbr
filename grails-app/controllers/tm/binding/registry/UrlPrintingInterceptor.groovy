package tm.binding.registry

class UrlPrintingInterceptor {

    UrlPrintingInterceptor(){
        matchAll()
    }

    def springSecurityService

    boolean before(){
        try {
            if (controllerName != 'assets') {
                log.info("URL[@|cyan ${controllerName}|@:@|green ${actionName}|@${params.id ? ':' + params.id : ''}] [user:@|yellow ${springSecurityService.currentUser ?: 'anonymous'}|@]")
            }
        }catch(Throwable t){}
        return true;
    }

    boolean after() {
        return true;
    }

    void afterView() {
        // no-op
    }

}

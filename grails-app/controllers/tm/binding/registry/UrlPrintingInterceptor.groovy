package tm.binding.registry

class UrlPrintingInterceptor {

    UrlPrintingInterceptor(){
        matchAll()
    }

    boolean before(){
        try {
            log.debug("UrlPrintingInterceptor: controller [${controllerName}]")

            if (controllerName == 'registrant' || controllerName == 'contact' || controllerName == 'email') {

                if (session.getAttribute('user')) {
                    redirect(controller: 'error', action: 'notAuthorized401')
                    return false
                }
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

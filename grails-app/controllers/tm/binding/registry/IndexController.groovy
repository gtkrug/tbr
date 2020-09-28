package tm.binding.registry

class IndexController {

    def springSecurityService

    def index() {
        log.info "Loading home page for user: @|cyan ${springSecurityService.currentUser ?: 'anonymous'}|@"

        [
                firstTimeLogin: (UserRole.countByRole(Role.findByAuthority(Role.ROLE_ADMIN)) == 0),
                user : springSecurityService.currentUser
        ]
    }

    def initialize()  {
        log.info "Initializing home page for user: @|cyan ${springSecurityService.currentUser ?: 'anonymous'}|@"

    }

}

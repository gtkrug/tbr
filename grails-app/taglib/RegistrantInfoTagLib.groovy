package tm.binding.registry

import javax.servlet.ServletException


class RegistrantInfoTagLib {
    def springSecurityService
    def administrationService

    static defaultEncodeAs = 'raw'

    def registrantName = {attrs, body ->

        User user = springSecurityService.currentUser

        if (!user) {
            throw new ServletException("Error getting current logged-in user.")
        }

        out << user.contact.lastName + ", " + user.contact.firstName

    }

    def isRegistrant = {attrs, body ->

        User user = springSecurityService.currentUser

        if (!user) {
            throw new ServletException("Error getting current logged-in user.")
        }

        Registrant registrant = Registrant.findByUser(user)

        if (registrant) {
            out << body()
        }
    }

    def ifReadOnly = {attrs, body ->

        Long orgId = attrs.orgId?.toInteger() as Long
        boolean readOnly = administrationService.isReadOnly(orgId)

        if (readOnly) {
            out << body()
        }
    }

    def ifNotReadOnly = {attrs, body ->

        Long orgId = attrs.orgId?.toInteger() as Long
        boolean isNotReadOnly = !administrationService.isReadOnly(orgId)

        if (isNotReadOnly) {
            out << body()
        }
    }

}

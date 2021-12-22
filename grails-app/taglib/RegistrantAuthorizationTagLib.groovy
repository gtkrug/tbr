package tm.binding.registry

import javax.servlet.ServletException


class RegistrantInfoTagLib {
    def springSecurityService

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
}

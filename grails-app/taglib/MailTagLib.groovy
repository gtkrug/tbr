package tm.binding.registry

import javax.servlet.ServletException


class MailTagLib {
    def emailService

    static defaultEncodeAs = 'raw'

    def isMailEnabled = {attrs, body ->

        if (emailService.mailEnabled()) {
            out << body()
        }
    }

    def isMailDisabled = {attrs, body ->

        if (!emailService.mailEnabled()) {
            out << body()
        }
    }
}

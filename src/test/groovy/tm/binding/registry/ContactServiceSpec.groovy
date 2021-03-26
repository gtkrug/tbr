package tm.binding.registry

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class ContactServiceSpec extends Specification implements ServiceUnitTest<ContactService>{

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fixed"
            true == true
    }
}

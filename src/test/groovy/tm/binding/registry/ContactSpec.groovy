package tm.binding.registry

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ContactSpec extends Specification implements DomainUnitTest<Contact> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fixed"
            true == true
    }
}

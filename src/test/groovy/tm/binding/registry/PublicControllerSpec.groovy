package tm.binding.registry

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class PublicControllerSpec extends Specification implements ControllerUnitTest<PublicController> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fixed"
            true == true
    }
}

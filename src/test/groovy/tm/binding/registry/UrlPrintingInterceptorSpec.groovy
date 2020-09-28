package tm.binding.registry

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class UrlPrintingInterceptorSpec extends Specification implements InterceptorUnitTest<UrlPrintingInterceptor> {

    def setup() {
    }

    def cleanup() {

    }

    void "Test urlPrinting interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"urlPrinting")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}

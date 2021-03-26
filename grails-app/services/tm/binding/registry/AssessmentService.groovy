package tm.binding.registry

import grails.gorm.transactions.Transactional

@Transactional
class AssessmentService {

    def serviceMethod() {

    }

    def getTrustmarks(String... args)  {

        def trustmarks = []

        return trustmarks;
    }
}

package tm.binding.registry

import java.time.LocalDateTime

class TrustmarkAssessmentToolUri {

    String uri
    LocalDateTime statusSuccessTimestamp
    LocalDateTime querySuccessTimestamp

    static constraints = {
        uri nullable: true
        statusSuccessTimestamp nullable: true
        querySuccessTimestamp nullable: true
    }

    static belongsTo = [
            assessmentRepository: AssessmentRepository
    ]

    static mapping = {
        table name: 'trustmark_assessment_tool_uri'
        uri type: 'text'
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id : this.id,
                uri : this.uri,
                statusSuccessTimestamp: this.statusSuccessTimestamp,
                querySuccessTimestamp : this.querySuccessTimestamp
        ]

        return json
    }
}

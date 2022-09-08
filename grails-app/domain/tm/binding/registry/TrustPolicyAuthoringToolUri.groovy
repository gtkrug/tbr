package tm.binding.registry

import java.time.LocalDateTime

class TrustPolicyAuthoringToolUri {

    String uri
    LocalDateTime statusSuccessTimestamp
    LocalDateTime querySuccessTimestamp

    static constraints = {
        uri nullable: true
        statusSuccessTimestamp nullable: true
        querySuccessTimestamp nullable: true
    }

    static mapping = {
        table name: 'trust_policy_authoring_tool_uri'
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

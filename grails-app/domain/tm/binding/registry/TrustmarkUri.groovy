package tm.binding.registry

import java.time.LocalDateTime

class TrustmarkUri {

    String uri
    String hash
    String content
    LocalDateTime retrievalTimestamp

    String assessmentRepositoryUrl
    String trustmarkRecipientIdentifierUrl

    static constraints = {
        uri nullable: true
        hash nullable: true
        content nullable: true
        retrievalTimestamp nullable: true
        trustmarkRecipientIdentifierUrl nullable: true
        assessmentRepositoryUrl nullable: true
    }

    static mapping = {
        table name: 'trustmark_uri'
        uri type: 'text'
        hash type: 'text'
        content type: 'text'
        assessmentRepositoryUrl type: 'text'
        trustmarkRecipientIdentifierUrl type: 'text'
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id : this.id,
                uri : this.uri,
                hash : this.hash,
                content : this.content,
                retrievalTimestamp: this.retrievalTimestamp,
                trustmarkRecipientIdentifierUrl: this.trustmarkRecipientIdentifierUrl,
                assessmentRepositoryUrl: this.assessmentRepositoryUrl
        ]

        return json
    }
}

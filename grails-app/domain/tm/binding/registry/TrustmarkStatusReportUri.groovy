package tm.binding.registry

import java.time.LocalDateTime

class TrustmarkStatusReportUri {

    String uri
    String hash
    String content
    LocalDateTime retrievalTimestamp

    static constraints = {
        uri nullable: true
        hash nullable: true
        content nullable: true
        retrievalTimestamp nullable: true
    }

    static mapping = {
        table name: 'trustmark_status_report_uri'
        uri type: 'text'
        hash type: 'text'
        content type: 'text'
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id : this.id,
                uri : this.uri,
                hash : this.hash,
                content : this.content,
                retrievalTimestamp: this.retrievalTimestamp,
        ]

        return json
    }
}

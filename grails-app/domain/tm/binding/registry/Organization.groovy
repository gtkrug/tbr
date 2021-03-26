package tm.binding.registry

class Organization {

    String name
    String displayName
    String siteUrl
    String description

    static hasMany = [
        registrants: Registrant
      , providers: Provider
      , assessmentRepos: AssessmentRepository
      , trustmarkRecipientIdentifier: TrustmarkRecipientIdentifier
    ]

    static constraints = {
        name nullable: false
        siteUrl nullable: false
        displayName nullable: false
        description nullable: true
        registrants nullable: true
        providers nullable: true
    }

    static mapping = {
        table name: 'organization'
        name column: 'name'
        siteUrl column: 'site_url'
        displayName column: 'display_name'
        description column: 'description', type: 'text'
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id: this.id,
                name: this.name,
                displayName: this.displayName,
                description: this.description,
                siteUrl: this.siteUrl,
                providers: this.providers,
                assessmentRepos: this.assessmentRepos,
                trustmarkRecipientIdentifier: this.trustmarkRecipientIdentifier,
                registrants: this.registrants
        ]
        return json;
    }

}

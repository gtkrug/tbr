package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.model.ContactKindCode

class Organization {

    String name
    String displayName
    String siteUrl
    String description

    static hasMany = [
        registrants: Registrant
      , contacts: Contact
      , providers: Provider
      , assessmentRepos: AssessmentRepository
      , trustmarkRecipientIdentifiers: TrustmarkRecipientIdentifier
    ]

    static constraints = {
        name nullable: false
        siteUrl nullable: false
        displayName nullable: false
        description nullable: true
        registrants nullable: true
        providers nullable: true
        registrants cascade: "all-delete-orphan"
        contacts cascade: "all-delete-orphan"
        providers cascade: "all-delete-orphan"
        assessmentRepos cascade: "all-delete-orphan"
        trustmarkRecipientIdentifiers cascade: "all-delete-orphan"
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
                trustmarkRecipientIdentifiers: this.trustmarkRecipientIdentifiers,
                registrants: this.registrants
        ]
        return json;
    }

}

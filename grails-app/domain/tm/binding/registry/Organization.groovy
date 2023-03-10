package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.model.ContactKindCode

class Organization {

    String name
    String displayName
    String siteUrl
    String description

    static hasMany = [
        contacts: Contact
      , providers: Provider
      , assessmentRepos: AssessmentRepository
      , trustmarkRecipientIdentifiers: TrustmarkRecipientIdentifier
      , partnerSystemsTips: PartnerSystemsTip
      , trustmarks: Trustmark
    ]

    static constraints = {
        name nullable: false
        siteUrl nullable: false
        displayName nullable: false
        description nullable: true
        providers nullable: true
        contacts cascade: "all-delete-orphan"
        providers cascade: "all-delete-orphan"
        assessmentRepos cascade: "all-delete-orphan"
        trustmarkRecipientIdentifiers cascade: "all-delete-orphan"
        partnerSystemsTips cascade: "all-delete-orphan"
    }

    static mapping = {
        table name: 'organization'
        name column: 'name'
        siteUrl column: 'site_url'
        displayName column: 'display_name'
        description column: 'description', type: 'text'
        trustmarks cascade: "all-delete-orphan"
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id: this.id,
                name: this.name,
                displayName: this.displayName,
                description: this.description,
                siteUrl: this.siteUrl
        ]

        if (shallow) {
            json.put("providers", this.providers)
            json.put("assessmentRepos", this.assessmentRepos)
        }

        if (this.trustmarkRecipientIdentifiers && this.trustmarkRecipientIdentifiers.size() > 0) {
            def jsonTrustmarkRecipientIdentifiers = []
            this.trustmarkRecipientIdentifiers.each { trustmarkRecipientIdentifier ->
                jsonTrustmarkRecipientIdentifiers.add(trustmarkRecipientIdentifier.trustmarkRecipientIdentifierUrl)
            }
            json.put("trustmarkRecipientIdentifiers", jsonTrustmarkRecipientIdentifiers);
        }

        if (this.partnerSystemsTips && this.partnerSystemsTips.size() > 0) {
            def jsonPartnerSystemsTips = []
            this.partnerSystemsTips.each { partnerSystemsTip ->
                jsonPartnerSystemsTips.add(partnerSystemsTip.partnerSystemsTipIdentifier)
            }
            json.put("partnerOrganizationTips", jsonPartnerSystemsTips)
        }

        return json;
    }

}

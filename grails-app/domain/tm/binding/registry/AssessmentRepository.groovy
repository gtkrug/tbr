package tm.binding.registry

class AssessmentRepository {

    String repoUrl

    static belongsTo = [
        organization: Organization
    ]

    static hasMany = [
        trustmark: Trustmark
    ]

    static constraints = {
        repoUrl nullable: false
        trustmark nullable: true
    }

    static mapping = {
        table name: 'assessment_repo'
        repoUrl column: 'repo_url'
    }
}

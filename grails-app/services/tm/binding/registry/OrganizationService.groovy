package tm.binding.registry

import grails.gorm.transactions.Transactional

@Transactional
class OrganizationService {

    def serviceMethod(String... args) {
        log.info("service -> ${args[0]}")
    }

    def add(String... args) {
        log.info("add -> ${args[0]}")

        Organization organization = new Organization(
                name: args[0]
                , displayName: args[1]
                , siteUrl: args[2]
                , description: args[3]
        )
        organization.save(true)
        return organization
    }

    def get(String... args) {
        log.info("get -> ${args[0]}")

        return Organization.get(Integer.parseInt(args[0]))
    }

    def update(String... args) {
        log.info("update -> ${args[0]} ${args[1]} ${args[2]} ${args[3]}")

        Organization organization = Organization.get(Integer.parseInt(args[0]))

        if(organization != null)  {
            organization.siteUrl = args[1]
            organization.description = args[2]
            organization.displayName = args[3]
            organization.save(true)
        }

        return organization
    }

    def delete(String... args) {
        log.info("delete -> ${args[0]}")

        List<String> ids = args[0].split(":")
        Organization organization = new Organization()

        try  {
            ids.forEach({s ->
                if(s.length() > 0)  {
                    organization = Organization.get(Integer.parseInt(s))
                    organization.delete()
                }
            })
            return organization

        } catch (NumberFormatException nfe)  {
            log.error("Invalid organization Id!")
        }
    }

    def list(String... args) {
        log.info("list -> ${args[0]}")
        def organizations = []

        if(args[0] == "ALL")  {
            Organization.findAll().forEach({o -> organizations.add(o.toJsonMap())})
            organizations.sort( {o1, o2 ->
                o1.name <=> o2.name
            })
        } else {
            Organization.findAllByName(args[0]).forEach({o -> organizations.add(o.toJsonMap())})
            organizations.sort( {o1, o2 ->
                o1.name <=> o2.name
            })
        }
        return organizations
    }

    def repos(String... args) {
        log.info("repos -> ${args[0]}")
        def repos = []

        Organization organization = Organization.get(Integer.parseInt(args[0]))
        organization.assessmentRepos.forEach({o -> repos.add(o)})

        return repos
    }

    def addRepos(String... args) {
        log.info("add repos -> ${args[0]} ${args[1]}")

        Organization organization = Organization.get(Integer.parseInt(args[0]))
        AssessmentRepository assessmentRepository = new AssessmentRepository(repoUrl: args[1], organization: organization)

        assessmentRepository.save(true)

        return assessmentRepository
    }

    def deleteRepos(String... args) {
        log.info("delete repos -> ${args[0]} ${args[1]}")

        Organization organization = Organization.get(Integer.parseInt(args[0]))

        AssessmentRepository assessmentRepository = AssessmentRepository.get(Integer.parseInt(args[1]))

        organization.assessmentRepos.remove(assessmentRepository)
        assessmentRepository.delete()

        organization.save(true)
        return organization
    }

    def trustmarkRecipientIdentifiers(String... args) {
        log.info("trustmarkRecipientIdentifiers -> ${args[0]}")
        def trustmarkRecipientIdentifiers = []

        Organization organization = Organization.get(Integer.parseInt(args[0]))
        organization.trustmarkRecipientIdentifier.forEach({o -> trustmarkRecipientIdentifiers.add(o)})

        return trustmarkRecipientIdentifiers
    }

    def addTrustmarkRecipientIdentifier(String... args) {
        log.info("add trustmarkRecipientIdentifier -> ${args[0]} ${args[1]}")

        Organization organization = Organization.get(Integer.parseInt(args[0]))
        TrustmarkRecipientIdentifier trustmarkRecipientIdentifier = new TrustmarkRecipientIdentifier(
                trustmarkRecipientIdentifierUrl: args[1], organization: organization)

        trustmarkRecipientIdentifier.save(true)

        return trustmarkRecipientIdentifier
    }

    def deleteTrustmarkRecipientIdentifier(String... args) {
        log.info("delete trustmarkRecipientIdentifier -> ${args[0]} ${args[1]}")

        Organization organization = Organization.get(Integer.parseInt(args[0]))

        TrustmarkRecipientIdentifier trustmarkRecipientIdentifier = TrustmarkRecipientIdentifier.get(Integer.parseInt(args[1]))

        organization.trustmarkRecipientIdentifier.remove(trustmarkRecipientIdentifier)
        trustmarkRecipientIdentifier.delete()

        organization.save(true)
        return organization
    }
}

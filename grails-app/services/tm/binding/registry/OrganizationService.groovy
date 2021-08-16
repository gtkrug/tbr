package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.impl.io.IOUtils
import grails.gorm.transactions.Transactional
import org.json.JSONObject

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

    def getRepo(String... args) {
        log.info("repos -> ${args[0]}")

        Organization organization = Organization.get(Integer.parseInt(args[0]))

        Integer repoId = Integer.parseInt(args[1])

        AssessmentRepository repo = organization.assessmentRepos.find { element ->
            element.id == repoId
        }

        return repo
    }

    def addRepos(String... args) {
        log.info("add repos -> ${args[0]} ${args[1]}")

        String statusMessage = ""

        // check the status of the repo url
        if (checkTATStatusUrl(args[1])) {

            Organization organization = Organization.get(Integer.parseInt(args[0]))
            AssessmentRepository assessmentRepository = new AssessmentRepository(repoUrl: args[1], organization: organization)

            assessmentRepository.save(true)
        } else {
            statusMessage = "Trustmark Assessment Tool not found at URL: ${args[1]}"
        }

        return statusMessage
    }

    /**
     * update the repo
     * @param args
     * @return
     */
    def updateRepo(String... args) {
        log.info("update -> ${args[0]}")

        AssessmentRepository repo = null

        repo = AssessmentRepository.get(Integer.parseInt(args[0]))
        repo.repoUrl = args[1]
        repo.save(true)

        return repo
    }

    def deleteRepos(String... args) {
        log.info("delete -> ${args[0]}")

        List<String> ids = args[0].split(":")

        Organization organization = new Organization()
        AssessmentRepository repo = new AssessmentRepository()

        // if origanization id is provided
        if (args[1]) {
            organization = Organization.get(Integer.parseInt(args[1]))
            try {
                ids.forEach({ s ->
                    if (s.length() > 0) {
                        repo = AssessmentRepository.get(Integer.parseInt(s))
                        organization.removeFromAssessmentRepos(repo)
                        repo.delete()
                    }
                })
                organization.save(true)
            } catch (NumberFormatException nfe) {
                log.error("Invalid Repo Id!")
            }
        } else {

            try {
                ids.forEach({ s ->
                    if (s.length() > 0) {
                        repo = AssessmentRepository.get(Integer.parseInt(s))

                        // get all organizations that share this repo
                        def organizations = Organization.withCriteria(uniqueResult: false) {
                            repos {
                                inList("id", [repo.id])
                            }
                        }

                        // remove the repo from all organizations found in previous search
                        organizations.each { org ->
                            org.removeFromAssessmentRepos(repo)
                            org.save(true)
                        }

                        // now delete repo
                        repo.delete()
                    }
                })

            } catch (NumberFormatException nfe) {
                log.error("Invalid repo Id!")
            }
        }
        return organization
    }


    def trustmarkRecipientIdentifiers(String... args) {
        log.info("trustmarkRecipientIdentifiers -> ${args[0]}")
        def trustmarkRecipientIdentifiers = []

        Organization organization = Organization.get(Integer.parseInt(args[0]))
        organization.trustmarkRecipientIdentifiers.forEach({o -> trustmarkRecipientIdentifiers.add(o)})

        return trustmarkRecipientIdentifiers
    }

    def addTrustmarkRecipientIdentifier(String... args) {
        log.info("add trustmarkRecipientIdentifier -> ${args[0]} ${args[1]}")

        Organization organization = Organization.get(Integer.parseInt(args[0]))
        TrustmarkRecipientIdentifier trustmarkRecipientIdentifier = new TrustmarkRecipientIdentifier(
                trustmarkRecipientIdentifierUrl: args[1], organization: organization)

        Organization.withTransaction {
            organization.trustmarkRecipientIdentifiers.add(trustmarkRecipientIdentifier)
            trustmarkRecipientIdentifier.save(true)

            organization.save(true)
        }

        return trustmarkRecipientIdentifier
    }

    def getTrustmarkRecipientIdentifier(String... args) {
        log.info("repos -> ${args[0]}")

        Organization organization = Organization.get(Integer.parseInt(args[0]))

        Integer trustmarkRecipientIdentifierId = Integer.parseInt(args[1])

        TrustmarkRecipientIdentifier trid = organization.trustmarkRecipientIdentifiers.find { element ->
            element.id == trustmarkRecipientIdentifierId
        }

        return trid
    }

    def updateTrustmarkRecipientIdentifier(String... args) {
        log.info("update -> ${args[0]}")

        TrustmarkRecipientIdentifier trid = null

        trid = TrustmarkRecipientIdentifier.get(Integer.parseInt(args[0]))
        trid.trustmarkRecipientIdentifierUrl = args[1]
        trid.save(true)

        return trid
    }

    def deleteTrustmarkRecipientIdentifiers(String... args) {

        log.info("delete -> ${args[0]}")

        List<String> ids = args[0].split(":")

        Organization organization = new Organization()
        TrustmarkRecipientIdentifier trid = new TrustmarkRecipientIdentifier()

        // if organization is provided
        if (args[1]) {
            organization = Organization.get(Integer.parseInt(args[1]))
            try {
                ids.forEach({ s ->
                    if (s.length() > 0) {
                        trid = TrustmarkRecipientIdentifier.get(Integer.parseInt(s))
                        organization.trustmarkRecipientIdentifiers.remove(trid)
                        trid.delete()
                    }
                })
                organization.save(true)
            } catch (NumberFormatException nfe) {
                log.error("Invalid trustmark recipient identifier Id!")
            }
        } else {

            try {
                ids.forEach({ s ->
                    if (s.length() > 0) {
                        trid = TrustmarkRecipientIdentifier.get(Integer.parseInt(s))

                        // get all organizations that share this trustmark recipient identifier
                        def organizations = Organization.withCriteria(uniqueResult: false) {
                            trustmarkRecipientIdentifiers {
                                inList("id", [trid.id])
                            }
                        }

                        // remove the trustmark recipient identifier from all organizations found in previous search
                        organizations.each { org ->
                            org.trustmarkRecipientIdentifiers.remove(trid)
                            org.save(true)
                        }

                        // now delete trustmark recipient identifier
                        trid.delete()
                    }
                })

            } catch (NumberFormatException nfe) {
                log.error("Invalid trustmark recipient identifier Id!")
            }
        }
        return organization
    }


    private boolean checkTATStatusUrl(String tatStatusUrl) {
        try{

            JSONObject json = IOUtils.fetchJSON(ensureTrailingSlash(tatStatusUrl) + "public/status")

            if (json && json.getString("status") == "OK") {
                return true
            }

            return false
        }catch(Throwable t){
            log.error("Error contacting TAT status url: " + tatStatusUrl, t)
            return false;
        }
    }

    private String ensureTrailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }
}

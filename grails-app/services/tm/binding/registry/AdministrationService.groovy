package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.FactoryLoader
import edu.gatech.gtri.trustmark.v1_0.impl.io.IOUtils
import edu.gatech.gtri.trustmark.v1_0.io.TrustInteroperabilityProfileResolver
import edu.gatech.gtri.trustmark.v1_0.model.TrustInteroperabilityProfile
import grails.gorm.transactions.Transactional
import org.gtri.fj.data.Option
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.json.JSONObject
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

import javax.servlet.ServletException
import java.time.LocalDateTime
import tm.binding.registry.util.UrlUtilities

@Transactional
class AdministrationService {

    def serviceMethod(String... args) {
        log.info("serviceMethod -> ${args[0]}")

    }
    def addTag(String... args) {
        log.info("addTag -> ${args[0]}")

        Provider provider = Provider.get(Integer.parseInt(args[0]))
        provider.tags.add(args[1])
        provider.save(true)

        return args[0]
    }

    def addContactToProvider(Contact contact, String... args) {
        log.info("addContactToProvider -> ${args[0]}")

        Provider provider = Provider.get(Integer.parseInt(args[0]))
        if(contact != null)  {
            provider.contacts.add(contact)
            provider.save(true)
        }

        return args[0]
    }

    def removeContactFromProvider(Contact contact, String... args) {
        log.info("removeContactFromProvider -> ${args[0]}")

        Provider provider = Provider.get(Integer.parseInt(args[0]))
        if(contact != null)  {
            provider.contacts.remove(contact)
            provider.save(true)
        }

        return args[0]
    }

    def addAttribute(String... args) {
        log.info("addAttribute -> ${args[0]}")

        Provider provider = Provider.get(Integer.parseInt(args[2]))

        Attribute attribute = new Attribute(name: args[0], value: args[1])

        provider.attributes.add(attribute)

        return attribute
    }

    def addConformanceTargetTip(String... args) {
        log.info("addConformanceTargetTip -> ${args[1]}")

        Provider provider = Provider.get(Integer.parseInt(args[0]))
        ConformanceTargetTip tip = new ConformanceTargetTip()
        tip.conformanceTargetTipIdentifier = args[1]

        String tipName = tipNameFromUri(tip.conformanceTargetTipIdentifier)

        tip.name = tipName

        saveTpatUri(tip.conformanceTargetTipIdentifier)

        tip.provider = provider

        provider.conformanceTargetTips.add(tip)
        provider.save(true)

        return args[0]
    }

    def addPartnerSystemsTipForOrganization(String... args) {
        log.info("addPartnerSystemsTipForOrganization -> ${args[1]}")

        Organization organization = Organization.get(Integer.parseInt(args[0]))
        PartnerSystemsTip tip = new PartnerSystemsTip()
        tip.partnerSystemsTipIdentifier = args[1]

        tip.name = tipNameFromUri(tip.partnerSystemsTipIdentifier)

        organization.partnerSystemsTips.add(tip)
        organization.save(true)

        saveTpatUri(tip.partnerSystemsTipIdentifier)

        return args[0]
    }

    def addPartnerSystemsTipForSystem(String... args) {
        log.info("addPartnerSystemsTipForSystem -> ${args[1]}")

        Provider provider = Provider.get(Integer.parseInt(args[0]))
        PartnerSystemsTip tip = new PartnerSystemsTip()
        tip.partnerSystemsTipIdentifier = args[1]

        String tipName = tipNameFromUri(tip.partnerSystemsTipIdentifier)

        tip.name = tipName

        provider.partnerSystemsTips.add(tip)
        provider.save(true)

        saveTpatUri(tip.partnerSystemsTipIdentifier)

        return args[0]
    }

    private void saveTpatUri(String tipUri) {
        String tpatBaseUrl = UrlUtilities.artifactBaseUrl(tipUri)

        if (UrlUtilities.checkTPATStatusUrl(tpatBaseUrl)) {

            TrustPolicyAuthoringToolUri trustPolicyAuthoringToolUri = TrustPolicyAuthoringToolUri.findByUri(tpatBaseUrl)

            // Only add the TPAT URI iff it does not already exists
            if (!trustPolicyAuthoringToolUri) {
                TrustPolicyAuthoringToolUri tpatUri = new TrustPolicyAuthoringToolUri(uri: tpatBaseUrl,
                        statusSuccessTimestamp: LocalDateTime.now())

                tpatUri.save(true)
            }
        }
    }

    private String tipNameFromUri(String tipUri) {
        URL url = new URL(tipUri)

        TrustInteroperabilityProfileResolver resolver = FactoryLoader.getInstance(TrustInteroperabilityProfileResolver.class)

        try {
            TrustInteroperabilityProfile tip = resolver.resolve(url);

            if (tip) {
                // found tip
                return tip.name
            }
        } catch(Throwable t) {
            log.error("Error encountered processing TIP ${tipUri}: ${t.message}");
        }

        throw new ServletException("Unable to resolve TIP: ${tipUri}")
    }

    def get(String... args) {
        log.info("get -> ${args[0]}")

        User usr = User.get(args[0])
    }

    def update(String... args) {
        log.info("update -> ${args[0]}")

        User usr = User.get(args[0])
        usr.save()
    }

    def deleteAttributes(String... args) {
        log.info("delete -> ${args[0]}")

        List<String> ids = args[0].split(":")

        Provider provider = Provider.get(Integer.parseInt(args[1]))
        try  {
            ids.forEach({s ->
                if(s.length() > 0)  {
                    Attribute attribute = Attribute.get(Integer.parseInt(s))
                    provider.attributes.remove(attribute)
                    attribute.delete()
                }
            })
        } catch (NumberFormatException nfe)  {
            log.error("Invalid Attribute Id!")
        }
        return provider
    }

    def deleteTags(String... args) {
        log.info("delete -> ${args[0]}")

        List<String> ids = args[0].split(":")

        Provider provider = Provider.get(Integer.parseInt(args[1]))
        try  {
            ids.forEach({s ->
                if(s.length() > 0)  {
                    provider.tags.remove(s)
                }
            })
            provider.save(true)
        } catch (NumberFormatException nfe)  {
            log.error("Invalid Tag Id!")
        }
        return provider
    }

    def deleteConformanceTargetTips(String... args) {
        log.info("delete -> ${args[0]}")

        // conformance target tips IDs
        List<String> ids = args[0].split(":")

        Provider provider = Provider.get(Integer.parseInt(args[1]))
        try  {
            ids.forEach({s ->
                if(s.length() > 0)  {
                    Integer tipId = Integer.parseInt(s)
                    ConformanceTargetTip tip = ConformanceTargetTip.findById(tipId)

                    // first, delete any bound trustmarks associated to this tip
                    def boundTrustmarks = Trustmark.findAllByConformanceTargetTipId(tip.id)

                    if (boundTrustmarks && boundTrustmarks.size() > 0) {
                        boundTrustmarks*.delete()
                    }

                    provider.conformanceTargetTips.remove(tip)
                    tip.delete();
                }
            })
            provider.save(true)
        } catch (NumberFormatException nfe)  {
            log.error("Invalid ConformanceTargetTip Id!")
        } catch(Exception e) {
            log.error("Error deleting conformance target tip, error: ${e.message}")
        }
        return provider
    }

    def listTags(String... args) {
        log.info("listTags -> ${args[0]}")

        def tags = []
        try  {
            Provider provider = Provider.get(Integer.parseInt(args[0]))
            provider.tags.forEach({t -> tags.add(t)})
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace()
        }
        return tags
    }

    def listConformanceTargetTips(String... args) {
        log.info("listTags -> ${args[0]}")

        def conformanceTargetTips = []
        try  {
            Provider provider = Provider.get(Integer.parseInt(args[0]))
            provider.conformanceTargetTips.forEach({t -> conformanceTargetTips.add(t)})
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace()
        }
        return conformanceTargetTips
    }

    def listTrustmarks(String... args) {
        log.info("listTrustmarks -> ${args[0]}")

        def trustmarks = []
        try  {
            Provider provider = Provider.get(Integer.parseInt(args[0]))
            provider.trustmarks.forEach({t -> trustmarks.add(t)})
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace()
        }
        return trustmarks
    }

    def listTrustmarksByOrganization(String... args) {
        log.info("listTrustmarksByOrganization -> ${args[0]}")

        def trustmarks = []
        try  {
            Organization organization = Organization.get(Integer.parseInt(args[0]))
            organization.trustmarks.forEach({t -> trustmarks.add(t)})
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace()
        }
        return trustmarks
    }

    def listAttributes(String... args) {
        log.info("listAttributes -> ${args[0]}")

        def attributes = []
        try  {
            Provider provider = Provider.get(Integer.parseInt(args[0]))
            provider.attributes.forEach({a -> attributes.add(a)})
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace()
        }
        return attributes
    }

    def listContacts(String... args) {
        log.info("listContacts -> ${args[0]}")

        def contacts = []
        try  {
            Provider provider = Provider.get(Integer.parseInt(args[0]))
            provider.contacts.forEach({c -> contacts.add(c.toJsonMap())})
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace()
        }
        return contacts
    }

    boolean isAdmin() {
        boolean isAuthenticated = isLoggedIn()

        if (isAuthenticated) {
            Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

            return userOption.isSome() && userOption.some().isAdmin()
        }
        return false
    }

    boolean isOrgAdmin() {
        boolean isAuthenticated = isLoggedIn()

        if (isAuthenticated) {
            Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

            return userOption.isSome() && userOption.some().isOrgAdmin()
        }
        return false
    }

    boolean loggedInUserHasNoTbrRoles() {

        if (isLoggedIn()) {
            Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

            if (userOption.isSome()) {
                return userOption.some().hasNoTbrRoles()
            }
        }

        return false
    }
    
    boolean isReadOnly(Long orgId) {
        boolean isAuthenticated = isLoggedIn()

        if (!isAuthenticated) {
            return true
        } else {
            Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

             if (userOption.isSome()) {
                boolean hasNoRoles = loggedInUserHasNoTbrRoles()

                boolean isOrgAdmin = isOrgAdmin()

                boolean hasContact = userOption.some().contact != null
                boolean hasOrganization = false
                boolean orgIdsDiffer = false

                if (hasContact) {
                    hasOrganization = userOption.some().contact.organization != null
                    orgIdsDiffer = hasOrganization && userOption.some().contact.organization.id != orgId
                }

                boolean userOrgDiffers = isOrgAdmin && hasContact && hasOrganization && orgIdsDiffer
                boolean orgAdminNoOrganization = isOrgAdmin && !hasOrganization

                if (hasNoRoles || (isOrgAdmin && userOrgDiffers) || orgAdminNoOrganization) {
                    return true
                }
            }
        }
        return false
    }

    boolean isReadOnly() {
        boolean isAuthenticated = isLoggedIn()

        if (!isAuthenticated) {
            return true
        } else {
            Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

            if (userOption.isSome() && (userOption.some().hasNoTbrRoles() || isOrgAdmin())) {
                return true
            }
        }
        return false
    }

    boolean isLoggedIn() {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication()
                        instanceof AnonymousAuthenticationToken)
    }
}

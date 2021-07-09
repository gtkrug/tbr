package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.FactoryLoader
import edu.gatech.gtri.trustmark.v1_0.io.ParseException
import edu.gatech.gtri.trustmark.v1_0.io.TrustInteroperabilityProfileResolver
import edu.gatech.gtri.trustmark.v1_0.model.TrustInteroperabilityProfile
import grails.gorm.transactions.Transactional

import javax.servlet.ServletException

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
        tip.provider = provider

        provider.conformanceTargetTips.add(tip)
        provider.save(true)

        return args[0]
    }

    private String tipNameFromUri(String tipUri) {
        URL url = new URL(tipUri + "?format=xml");

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
}

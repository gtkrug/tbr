package tm.binding.registry

import grails.gorm.transactions.Transactional
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.Element
import org.dom4j.io.SAXReader

import java.nio.charset.StandardCharsets

@Transactional
class DeserializeService {

    final String CHILDREN = "./*"

    final String EXTENSIONS = "./md:Extensions/mdattr:EntityAttributes"
    final String ORGANIZATION = "./md:Organization"
    final String SP_SSO_DESCRIPTOR = "./md:SPSSODescriptor"
    final String IDP_SSO_DESCRIPTOR = "./md:IDPSSODescriptor"
    final String ENTITY_ID = "entityID"

    final String NAME = "Name"
    final String USE = "use"
    final String CONTACT_TYPE = "contactType"
    final String SUPPORT_PROTOCOL = "protocolSupportEnumeration"
    final String BINDING = "Binding"
    final String LOCATION = "Location"

    final String HTTPS = "https:"

    final String ADMINISTRATIVE = "administrative"
    final String SUPPORT = "support"
    final String TECHNICAL = "technical"
    final String SIGNING = "signing"
    final String ENCRYPTION = "encryption"

    final String REQUESTED_ATTRIBUTE = "RequestedAttribute"
    final String SERVICE_NAME = "ServiceName"
    final String ATTRIBUTE_CONSUMING_SERVICE = "AttributeConsumingService"
    final String ATTRIBUTE_VALUE = "AttributeValue"

    final String NAME_ID_FORMAT = "NameIDFormat"
    final String SINGLE_SIGNON_SERVICE = "SingleSignOnService"
    final String SINGLE_LOGOUT_SERVICE = "SingleLogoutService"
    final String ASSERTION_CONSUMER_SERVICE = "AssertionConsumerService"
    final String MANAGE_NAME_ID_SERVICE = "ManageNameIDService"
    final String ARTIFACT_RESOLUTION_SERVICE = "ArtifactResolutionService"

    final String TRUSTMARK_RECIPIENT_ID = "TrustmarkRecipientIdentifiers"
    final String CERTIFICATE = "X509Certificate"
    final String OWNER_AGENCY_NAME = "OwnerAgencyName"
    final String OWNER_CATEGORY_CODE = "OwnerAgencyOrganizationGeneralCategoryCode"
    final String OWNER_AGENCY_DESC = "OwnerAgencyDescriptionText"

    final String ENTITY_TAG = "EntityTags"

    final String PROVIDER_NAME = "ProviderName"
    final String ORG_NAME = "OrganizationName"
    final String ORG_DISPLAY_NAME = "OrganizationDisplayName"
    final String ORG_URL = "OrganizationURL"

    final String CONTACT_COMPANY = "Company"
    final String CONTACT_GIVEN_NAME = "GivenName"
    final String CONTACT_SURNAME = "SurName"
    final String CONTACT_EMAIL = "EmailAddress"
    final String CONTACT_PHONE = "TelephoneNumber"

    ContactService contactService

    /**
     * parse xml into it's organization and sub-components
     * @param xml
     * @return
     */
    def deserialize(String xml)    {
        Element rootNode = readXmlDocument(xml)

        Organization organization = parseOrganization((Element)rootNode.selectSingleNode(ORGANIZATION))

        Organization existingOrganization = Organization.findByNameAndSiteUrl(organization.name, organization.siteUrl)
        if(existingOrganization)  {
            organization = existingOrganization
        }

        def providers = findAllElementsByName(rootNode, SP_SSO_DESCRIPTOR)
        providers.forEach({p ->
            log.info("SERVICE PROVIDERS ${p.name} ${p.text}")
        })

        providers = findAllElementsByName(rootNode, IDP_SSO_DESCRIPTOR)
        providers.forEach({p ->
            log.info("IDENTITY PROVIDERS ${p.name} ${p.text}")
        })

        deserialize(rootNode, organization)

        logOrganization(organization)
        saveOrganization(organization)
    }

    /**
     * parse an xml string into it's organization and sub-components
     * @param xml
     * @return
     */
    def deserialize(String xml, Organization organization)    {
        Element rootNode = readXmlDocument(xml)

        deserialize(rootNode, organization)

        logOrganization(organization)
        saveOrganization(organization)
    }

    def deserialize(Element rootNode, Organization organization)  {

        String entityId = getAttributeByName(rootNode, ENTITY_ID)

        // get service provider, if any
        Provider provider = parseProvider((Element)rootNode.selectSingleNode(SP_SSO_DESCRIPTOR))
        if(provider)  {
            if(provider.name == null) {
                provider.name = "Service System"
            }
            provider.entityId = entityId
            provider.providerType = ProviderType.SERVICE_PROVIDER
            provider.organization = organization
            loadProviderAttributes(provider, (Element)rootNode.selectSingleNode(EXTENSIONS))
            loadProviderContacts(provider, rootNode)
            organization.providers.add(provider)
            organization.description = provider.findAttribute(OWNER_AGENCY_DESC)
        }

        // get identity provider, if any
        provider = parseProvider((Element)rootNode.selectSingleNode(IDP_SSO_DESCRIPTOR))
        if(provider) {
            if(provider.name == null) {
                provider.name = "Identity System"
            }
            provider.entityId = entityId
            provider.providerType = ProviderType.IDENTITY_PROVIDER
            provider.organization = organization
            loadProviderAttributes(provider, (Element)rootNode.selectSingleNode(EXTENSIONS))
            loadProviderContacts(provider, rootNode)
            organization.providers.add(provider)
            organization.description = provider.findAttribute(OWNER_AGENCY_DESC)
        }
    }

    /**
     * just for debug purposes
     * @param org
     * @return
     */
    def logOrganization(Organization org)  {
        log.info("Name -> ${org.name}")
        log.info("Display Name -> ${org.displayName}")
        log.info("Url -> ${org.siteUrl}")
        log.info("Description -> ${org.description}")
        org.providers.forEach({p ->
            log.info("name -> ${p.name}")
            log.info("entityId -> ${p.entityId}")
            log.info("type -> ${p.providerType}")
            log.info("enc cert -> ${p.encryptionCertificate}")
            log.info("sign cert -> ${p.signingCertificate}")
            p.protocols.forEach({r ->
                log.info("protocol -> ${r}")
            })
            p.contacts.forEach({ c ->
                log.info("contact -> ${c.lastName}, ${c.firstName},  ${c.email},  ${c.phone}")
            })
            p.tags.forEach({t ->
                log.info("tag -> ${t}")
            })
            p.attributes.forEach({a ->
                log.info("attr -> ${a.name},  ${a.value}")
            })
            p.nameFormats.forEach({s ->
                log.info("formats -> ${s}")
            })
            p.endpoints.forEach({e ->
                log.info("endpoints -> ${e.name} ${e.binding} ${e.url}")
                e.attributes.forEach({a ->
                    log.info("endpoints -> attributes -> ${a.name} ${a.value}")
                })
            })
            p.trustmarks.forEach({t ->
                log.info("trustmarks -> ${t.name} ${t.url}")
            })
        })
    }

    /**
     * save organization and all components to the database
     * @param org
     * @return
     */
    def saveOrganization(Organization org) {
        try {
            org.save(true)
        } catch (Exception e) {
            e.printStackTrace()
        }
        return org
    }

    /**
     * parse out an Attribute by name and child value
     * @param rootNode
     * @param attrName
     * @param attrValue
     * @param name
     * @return
     */
    def parseAttribute(Element rootNode, String attrName, String attrValue, String name)  {
        Element elem = findElementByAttributeName(rootNode, attrName, attrValue, name)
        if(elem)  {
            Attribute attribute = new Attribute(name: attrValue)
            attribute.value = elem.text
            return attribute
        }
        return null
    }

    /**
     * parse all attributes of a particular name
     * @param rootNode
     * @param attrName
     * @param name
     * @return
     */
    def parseAllAttributes(Element rootNode, String attrName, String name)  {
        List<Attribute> attributes = new ArrayList<>()
        def elems = findAllElementsByAttribute(rootNode, attrName)
        elems.forEach({e ->
            Element el = findElementByName((Element)e, name)
            attributes.add(new Attribute(name: ((Element)e).attributeValue(attrName), value: el.text))
        })
        return attributes
    }

    /**
     * parse out the Organization components
     * @param rootNode
     * @return
     */
    def parseOrganization(Element rootNode)  {
        Organization organization = new Organization(
                providers: []
        )
        Element elem = findElementByName(rootNode, ORG_NAME)
        if(elem)
            organization.name = elem.text
        elem = findElementByName(rootNode, ORG_DISPLAY_NAME)
        if(elem)
            organization.displayName = elem.text
        elem = findElementByName(rootNode, ORG_URL)
        if(elem)
            organization.siteUrl = elem.text
        return organization
    }

    /**
     * parse out the Contact based on type
     * @param rootNode
     * @param type
     * @return
     */
    def parseContact(Element rootNode, String type)  {
        Contact contact = null
        Element elem = findElementByAttribute(rootNode, CONTACT_TYPE, type)
        if(elem)  {
            contact = new Contact()
            contact.type = ContactType.valueOf(type.toUpperCase())

            Element el = findElementByName(elem, CONTACT_COMPANY)
            if(el)
                log.info(el.text)
            el = findElementByName(elem, CONTACT_GIVEN_NAME)
            if(el)
                contact.firstName = el.text
            el = findElementByName(elem, CONTACT_SURNAME)
            if(el)
                contact.lastName = el.text
            el = findElementByName(elem, CONTACT_EMAIL)
            if(el)
                contact.email = el.text
            el = findElementByName(elem, CONTACT_PHONE)
            if(el)
                contact.phone = el.text
        }
        return contact
    }

    /**
     * parse out an endpoint from a specific node
     * @param rootNode
     * @param name
     * @return
     */
    def parseEndpoints(Element rootNode, String name, String eptName)  {
        List<Endpoint> endpoints = new ArrayList<>()

        def elems = findAllElementsByName(rootNode, name)
        if(!elems.isEmpty())  {
            elems.forEach({ e ->
                Endpoint endpoint = new Endpoint(
                        published: false
                      , attributes: []
                      , name: eptName
                )
                e.attributes().forEach({a ->
                    if(a.name == BINDING)  {
                        endpoint.binding = a.value
                    }
                    if(a.name == LOCATION)  {
                        endpoint.url = a.value
                    }
                })
                def els = findAllElementsByName(e, SERVICE_NAME)
                if(!els.isEmpty())  {
                    Attribute attribute = new Attribute(
                            name: SERVICE_NAME
                            , value: els[0].text
                    )
                    endpoint.attributes.add(attribute)
                }
                els = findAllElementsByName(e, REQUESTED_ATTRIBUTE)
                els.forEach({ el ->
                    Attribute attribute = new Attribute()
                    el.attributes().forEach({a ->
                        if(a.name == "Name")  {
                            attribute.name = a.value
                        }
                        if(a.name == "NameFormat")  {
                            attribute.value = a.value
                        }
                    })
                    endpoint.attributes.add(attribute)
                })
                endpoints.add(endpoint)
            })
        }

        return endpoints
    }

    /**
     * parse out a Provider component
     * @param rootNode
     * @return
     */
    def parseProvider(Element rootNode)  {
        if(rootNode == null)  {
            return null
        }
        Provider provider = new Provider(
                endpoints: []
                , assessmentRepos: []
                , attributes: []
                , metadata: []
                , protocols: []
                , contacts: []
                , nameFormats: []
                , trustmarks: []
                , tags: []
        )
        Element elem = findElementByAttributeName(rootNode, USE, ENCRYPTION, CERTIFICATE)
        if(elem)
            provider.encryptionCertificate = elem.text

        elem = findElementByAttributeName(rootNode, USE, SIGNING, CERTIFICATE)
        if(elem)
            provider.signingCertificate = elem.text

        rootNode.attributes().forEach({a ->
            if(a.name == SUPPORT_PROTOCOL)  {
                provider.protocols.addAll(a.value.split(" "))
            }
        })

        Element el = findElementByName(rootNode, PROVIDER_NAME)
        if(el)
            provider.name = el.text

        def elems = findAllElementsByName(rootNode, NAME_ID_FORMAT)
        if(!elems.isEmpty())  {
            elems.forEach({ e ->
                provider.nameFormats.add(e.text)
                })
        }
        loadProviderEndpoints(provider, rootNode)

        return provider
    }

    /**
     * load all of the defined endpoints into the provider component
     * @param provider
     * @param rootNode
     * @return
     */
    def loadProviderEndpoints(Provider provider, Element rootNode)  {
        List<Endpoint> endpoints = parseEndpoints(rootNode, SINGLE_SIGNON_SERVICE, "Single Sign On Service")

        endpoints.addAll(parseEndpoints(rootNode, SINGLE_LOGOUT_SERVICE, "Single Logout Service"))

        endpoints.addAll(parseEndpoints(rootNode, ASSERTION_CONSUMER_SERVICE, "Assertion Consumer Service"))

        endpoints.addAll(parseEndpoints(rootNode, MANAGE_NAME_ID_SERVICE, "Manage Name ID Service"))

        endpoints.addAll(parseEndpoints(rootNode, ARTIFACT_RESOLUTION_SERVICE, "Artifact Resolution Service"))

        endpoints.addAll(parseEndpoints(rootNode, ATTRIBUTE_CONSUMING_SERVICE, "Attribute Consuming Service"))

        provider.endpoints.addAll(endpoints)

        provider.endpoints.forEach({e ->
            e.provider = provider
        })
    }

    /**
     * load attributes into the provider component
     * @param provider
     * @param attributeNode
     * @return
     */
    def loadProviderAttributes(Provider provider, Element attributeNode)  {
        def attributes = findAllElementsByAttributeName(attributeNode, NAME, ENTITY_TAG, ATTRIBUTE_VALUE)
        if(attributes)
            attributes.forEach({a ->
                provider.tags.add(a.text)
            })

        Attribute attribute = parseAttribute(attributeNode, NAME, OWNER_AGENCY_NAME, ATTRIBUTE_VALUE)
        if(attribute)  {
            provider.attributes.add(attribute)
        }

        attribute = parseAttribute(attributeNode, NAME, OWNER_CATEGORY_CODE, ATTRIBUTE_VALUE)
        if(attribute)  {
            provider.attributes.add(attribute)
        }

        attribute = parseAttribute(attributeNode, NAME, OWNER_AGENCY_DESC, ATTRIBUTE_VALUE)
        if(attribute)  {
            provider.attributes.add(attribute)
        }

        attribute = parseAttribute(attributeNode, NAME, TRUSTMARK_RECIPIENT_ID, ATTRIBUTE_VALUE)
        if(attribute)  {
            provider.attributes.add(attribute)
        }

        List<Attribute> list = parseAllAttributes(attributeNode, NAME, ATTRIBUTE_VALUE)
        list.forEach({a ->
            if(a.name.startsWith(HTTPS) && a.value.startsWith(HTTPS))  {   // trustmarks
                Trustmark trustmark = new Trustmark(
                        status: "ACTIVE"
                        , provisional: false
                        , url: a.value
                        , name: a.name
                        , provider: provider
                )
                provider.trustmarks.add(trustmark)
            }
        })

    }

    /**
     * load any contacts into the provider component
     * @param provider
     * @param rootNode
     * @return
     */
    def loadProviderContacts(Provider provider, Element rootNode)  {
        Contact techContact = parseContact(rootNode, TECHNICAL)
        if(techContact)  {
            checkForExistingContact(techContact, provider)
        }

        Contact admContact = parseContact(rootNode, ADMINISTRATIVE)
        if(admContact)  {
            checkForExistingContact(admContact, provider)
        }

        Contact supContact = parseContact(rootNode, SUPPORT)
        if(supContact)  {
            checkForExistingContact(supContact, provider)
        }
    }

    /**
     * find the first element with an attribute name and value, return it's child by name
     * @param rootNode
     * @param attrName
     * @param attrValue
     * @param name
     * @return
     */
    def findElementByAttributeName(Element rootNode, String attrName, String attrValue, String name)  {
        Element elem = findElementByAttribute(rootNode, attrName, attrValue)
        return findElementByName(elem, name)
    }

    /**
     * find all attributes by name and value then return child by name
     * @param rootNode
     * @param attrName
     * @param attrValue
     * @param name
     * @return
     */
    def findAllElementsByAttributeName(Element rootNode, String attrName, String attrValue, String name)  {
        Element elem = findElementByAttribute(rootNode, attrName, attrValue)
        return findAllElementsByName(elem, name)
    }

    /**
     * find all elements by attribute name
     * @param rootNode
     * @param name
     * @return
     */
    def findAllElementsByAttribute(Element rootNode, String name)  {
        if(rootNode == null)  {
            return null
        }
        def elems = []
        List<org.dom4j.Node> nodes = rootNode.selectNodes(CHILDREN)
        if( nodes != null)  {
            nodes.forEach({ n ->
                Element e = (Element) n
                e.attributes().forEach({a ->
                    if(a.name == name )  {
                        elems.add(e)
                    }  else {
                        def els = findAllElementsByAttribute(e, name)
                        if(els.size() > 0)
                            elems.addAll(els)
                    }
                })
            })
            return elems
        }
    }

    /**
     * find and return all elements of a particular name
     * @param rootNode
     * @param name
     * @return
     */
    def findAllElementsByName(Element rootNode, String name)  {
        if(rootNode == null)  {
            return null
        }
        def elems = []
        List<org.dom4j.Node> nodes = rootNode.selectNodes(CHILDREN)
        if( nodes != null)  {
            nodes.forEach({ n ->
                Element e = (Element) n
                if(n.name == name)  {
                    elems.add(e)
                }  else {
                    def els = findAllElementsByName(e, name)
                    if(els.size() > 0)
                        elems.addAll(els)
                }
            })
        }
        return elems
    }

    /**
     * find the first element by attribute name and value
     * @param rootNode
     * @param name
     * @param value
     * @return
     */
    def findElementByAttribute(Element rootNode, String name, String value)  {
        if(rootNode == null)  {
            return null
        }
        Element elem = null
        List<org.dom4j.Node> nodes = rootNode.selectNodes(CHILDREN)
        if( nodes != null)  {
            nodes.forEach({ n ->
                Element e = (Element) n
                e.attributes().forEach({a ->
                    if(a.name == name && a.value.endsWith(value) )  {
                        elem = e
                    }  else {
                        Element el = findElementByAttribute(e, name, value)
                        if(el)
                            elem = el
                    }
                })
            })
            return elem
        }
    }

    /**
     * find the first element of a particular name
     * @param rootNode
     * @param name
     * @return
     */
    def findElementByName(Element rootNode, String name)  {
        if(rootNode == null)  {
            return null
        }
        Element elem = null
        List<org.dom4j.Node> nodes = rootNode.selectNodes(CHILDREN)
        if( nodes != null)  {
            nodes.forEach({ n ->
                Element e = (Element) n
                if(n.name == name)  {
                    elem = e
                }  else {
                    Element el = findElementByName(e, name)
                    if(el)
                        elem =  el
                }
            })
        }
        return elem
    }

    /**
     * returns the value of the attribute for a particular node
     * @param rootNode
     * @param name
     * @return
     */
    def getAttributeByName(Element rootNode, String name)  {
        String value = null
        rootNode.attributes().forEach({a ->
            if(a.name == name)  {
                value = a.value
            }
        })
        return value
    }

    /**
     * walk down the node list showing names and attributes
     * @param rootNode
     * @param args
     * @return
     */
    def parseElements(Element rootNode)  {
        if(rootNode == null)  {
            return null
        }
        log.info(": "+rootNode.name+"="+rootNode.text)
        rootNode.attributes().forEach({a ->
            log.info("! "+a.name+"="+a.value)
        })
        List<org.dom4j.Node> nodes = rootNode.selectNodes(CHILDREN)
        if( nodes != null)  {
            nodes.forEach({ n ->
                Element e = (Element) n
                log.info(": "+n.name+"="+n.text)
                e.attributes().forEach({a ->
                    log.info("! "+a.name+"="+a.value)
                })
                parseElements(e)
            })
        }
    }

    /**
     * transform xml string into a document and return the root node
     * @param xml
     * @return
     */
    def readXmlDocument(String xml)  {
        try  {
            SAXReader reader = new SAXReader()
            reader.setIgnoreComments(false)
            reader.setIncludeInternalDTDDeclarations(false)
            reader.setIncludeExternalDTDDeclarations(false)

            Document document = reader.read(new StringReader(xml))
            Element rootNode = document.getRootElement()
            return rootNode;
        }  catch(DocumentException de)  {
            de.printStackTrace()
        }
        return null;
    }

    /**
     * check if the contact is already existing
     * @param contact
     * @return
     */
    def checkForExistingContact(Contact contact, Provider provider)  {
        Contact existingContact = contactService.get(contact)
        if(existingContact)  {
            existingContact.organization = provider.organization
            provider.contacts.add(existingContact)
        }  else {
            contact.organization = provider.organization
            provider.contacts.add(contact)
        }
    }
}

package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.impl.io.xml.XmlSignatureImpl
import grails.gorm.transactions.Transactional
import grails.web.mapping.LinkGenerator
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.Element
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader

import org.dom4j.DocumentHelper
import org.dom4j.Namespace
import org.dom4j.QName
import org.dom4j.io.XMLWriter
import tm.binding.registry.util.TBRProperties

import javax.servlet.ServletException
import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.time.Duration
import java.time.Instant
import org.apache.commons.lang.StringUtils

// TEMP
import javax.xml.XMLConstants
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.*
import java.net.URL
import org.xml.sax.SAXException
import java.io.IOException
import java.time.LocalDateTime

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
    final String ATTRIBUTE = "Attribute"
    final String ATTRIBUTE_VALUE = "AttributeValue"

    final String NAME_FORMAT = "NameFormat"

    // SSODescriptorType
    final String ARTIFACT_RESOLUTION_SERVICE = "ArtifactResolutionService"
    final String SINGLE_LOGOUT_SERVICE = "SingleLogoutService"
    final String MANAGE_NAME_ID_SERVICE = "ManageNameIDService"
    final String NAME_ID_FORMAT = "NameIDFormat"

    // SPSSODescriptorType
    final String ASSERTION_CONSUMER_SERVICE = "AssertionConsumerService"
    final String ATTRIBUTE_CONSUMING_SERVICE = "AttributeConsumingService"

    // IDPSSODescriptorType
    final String SINGLE_SIGNON_SERVICE = "SingleSignOnService"

    final String ARTIFACT_RESOLUTION_SERVICE_DISPLAY_NAME = "Artifact Resolution Service"
    final String SINGLE_LOGOUT_SERVICE_DISPLAY_NAME = "Single Logout Service"
    final String MANAGE_NAME_ID_SERVICE_DISPLAY_NAME = "Manage Name ID Service"
    final String SINGLE_SIGNON_SERVICE_DISPLAY_NAME = "Single Sign On Service"
    final String ASSERTION_CONSUMER_SERVICE_DISPLAY_NAME = "Assertion Consumer Service"
    final String ATTRIBUTE_CONSUMING_SERVICE_DISPLAY_NAME = "Attribute Consuming Service"


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

    final String CONTACT_PERSON = "ContactPerson"
    final String CONTACT_TYPE = "contactType"
    final String CONTACT_COMPANY = "Company"
    final String CONTACT_GIVEN_NAME = "GivenName"
    final String CONTACT_SURNAME = "SurName"
    final String CONTACT_EMAIL = "EmailAddress"
    final String CONTACT_PHONE = "TelephoneNumber"

    final String TIP_REFERENCE_NAME = "https://nief.org/attribute-registry/attributes/entity/nief/TrustInteroperabilityProfileReference/1.0/"

    // schemas
    public static final String SAML2_METADATA_SCHEMA = "xml-schema/saml-schema-metadata-2.0.xsd";
    public static final String SAML2_ATTRIBUTE_SCHEMA = "xml-schema/saml-schema-metadata-2.0.xsd";
    public static final String DIGITAL_SIGNATURE_SCHEMA = "xml-schema/saml-schema-metadata-2.0.xsd";
    public static final String ENCRYPTION_SCHEMA = "xml-schema/saml-schema-metadata-2.0.xsd";

    public static final String SAML2_ATTRIBUTE_NAME_FORMAT_URI = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";

    ContactService contactService

    LinkGenerator grailsLinkGenerator

    def resourceLocator

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
            provider.providerType = ProviderType.SAML_SP
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
            provider.providerType = ProviderType.SAML_IDP
            provider.organization = organization
            loadProviderAttributes(provider, (Element)rootNode.selectSingleNode(EXTENSIONS))
            loadProviderContacts(provider, rootNode)
            organization.providers.add(provider)
            organization.description = provider.findAttribute(OWNER_AGENCY_DESC)
        }
    }

    /**
     * parse an xml string into it's provider and sub-components
     * @param xml
     * @return
     */
    def deserialize(String xml, Provider provider) {
        def messageMap = [:]

        Element rootNode = null

        rootNode = readXmlDocument(xml)

        if (rootNode) {
            messageMap = deserialize(rootNode, provider)

            saveProvider(provider)
        } else {
            messageMap["ERROR"] = "Error parsing metadata..."
        }

        return messageMap
    }

    def deserialize(Element rootNode, Provider provider)  {

        Map messageMap = [:]

        String entityId = getAttributeByName(rootNode, ENTITY_ID)

        Element spElement = (Element) rootNode.selectSingleNode(SP_SSO_DESCRIPTOR)
        Element idpElement = (Element) rootNode.selectSingleNode(IDP_SSO_DESCRIPTOR)

        if (!spElement && !idpElement) {
            throw new DocumentException("Unable to upload ${entityId}. There are no service provider or identity provider descriptors.")
        }

        // get service provider, if any
        if (provider.providerType == ProviderType.SAML_SP) {
            if (!spElement) {
                throw new DocumentException("Unable to upload ${entityId}. There are no service provider descriptors.")
            }
            provider = parseProvider(spElement, provider)

            if (idpElement) {
                messageMap["WARNING"] = "An identity provider descriptor was available for ${entityId} but was ignored."
            }
        } else if (provider.providerType == ProviderType.SAML_IDP) {
            if (!idpElement) {
                throw new DocumentException("Unable to upload ${entityId}. There are no identity provider descriptors.")
            }
            provider = parseProvider(idpElement, provider)

            // load IDP SAML attributes
            loadIDPSamlAttributes(provider, idpElement)

            if (spElement) {
                messageMap["WARNING"] = "A service provider descriptor was available for ${entityId} but was ignored."
            }
        }

        if (provider) {
            if (provider.name == null) {
                provider.name = "Service System"
            }
            provider.entityId = entityId

            // load extension attributes
            loadProviderAttributes(provider, (Element) rootNode.selectSingleNode(EXTENSIONS))

            loadProviderContacts(provider, rootNode)

            // reset the generated metadata
            provider.saml2MetadataXml = ""

            messageMap["SUCCESS"] = "Successfully uploaded SAML metadata for " + entityId
        }

        return messageMap
    }

    def saveProvider(Provider provider) {
        try {
            Provider.withTransaction {
                provider.save(failOnError: true, flush: true)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        return provider
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
     * parse all attributes of a particular name
     * @param rootNode
     * @param attrName
     * @param name
     * @return
     */
    def parseAllIdpAttributes(Element rootNode, String attrName)  {
        List<String> attributes = new ArrayList<>()
        def elems = findAllElementsByAttribute(rootNode, attrName)
        elems.forEach({e ->
            def name = ((Element)e).attributeValue(attrName)

            attributes.add(name)
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
                        if(a.name == NAME)  {
                            attribute.name = a.value
                        }
                        if(a.name == NAME_FORMAT)  {
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

        return parseProvider(rootNode, provider)
    }

    def parseProvider(Element rootNode, Provider provider)  {
        if(rootNode == null)  {
            return null
        }

        Element elem = findElementByAttributeName(rootNode, USE, ENCRYPTION, CERTIFICATE)
        if(elem)
            provider.encryptionCertificate = elem.text

        elem = findElementByAttributeName(rootNode, USE, SIGNING, CERTIFICATE)
        if(elem)
            provider.signingCertificate = elem.text

        // remove previous protocols
        if (provider.protocols) {
            provider.protocols.clear()
        }

        rootNode.attributes().forEach({a ->
            if(a.name == SUPPORT_PROTOCOL)  {
                provider.protocols.addAll(a.value.split(" "))
            }
        })

        Element el = findElementByName(rootNode, PROVIDER_NAME)
        if(el)
            provider.name = el.text

        // remove previous name formats
        if (provider.nameFormats) {
            provider.nameFormats.clear()
        }

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
        // remove previous provider endpoints
        if (provider.endpoints) {
            provider.endpoints.clear()
        }

        List<Endpoint> endpoints = parseEndpoints(rootNode, SINGLE_SIGNON_SERVICE, SINGLE_SIGNON_SERVICE_DISPLAY_NAME)

        endpoints.addAll(parseEndpoints(rootNode, SINGLE_LOGOUT_SERVICE, SINGLE_LOGOUT_SERVICE_DISPLAY_NAME))

        endpoints.addAll(parseEndpoints(rootNode, ASSERTION_CONSUMER_SERVICE, ASSERTION_CONSUMER_SERVICE_DISPLAY_NAME))

        endpoints.addAll(parseEndpoints(rootNode, MANAGE_NAME_ID_SERVICE, MANAGE_NAME_ID_SERVICE_DISPLAY_NAME))

        endpoints.addAll(parseEndpoints(rootNode, ARTIFACT_RESOLUTION_SERVICE, ARTIFACT_RESOLUTION_SERVICE_DISPLAY_NAME))

        endpoints.addAll(parseEndpoints(rootNode, ATTRIBUTE_CONSUMING_SERVICE, ATTRIBUTE_CONSUMING_SERVICE_DISPLAY_NAME))

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

        // remove previous provider attributes
        if (provider.attributes) {
            provider.attributes.clear()
        }

        def attributes = findAllElementsByAttributeName(attributeNode, NAME, ENTITY_TAG, ATTRIBUTE_VALUE)
        if(attributes)
            attributes.forEach({a ->
                provider.tags.add(a.text)
            })

        Attribute attribute = parseAttribute(attributeNode, NAME, OWNER_CATEGORY_CODE, ATTRIBUTE_VALUE)
        if(attribute)  {
            provider.attributes.add(attribute)
        }
    }

    /**
     * load attributes into the provider component
     * @param provider
     * @param attributeNode
     * @return
     */
    def loadIDPSamlAttributes(Provider provider, Element attributeNode)  {

        // remove previous provider attributes
        if (provider.idpAttributes) {
            provider.idpAttributes.clear()
        }

        List<String> list = parseAllIdpAttributes(attributeNode, NAME)
        list.forEach({attribute ->
            provider.idpAttributes.add(attribute)
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
            Map<String, String> nsContext = new HashMap<>()
            nsContext.put("md","urn:oasis:names:tc:SAML:2.0:metadata")
            nsContext.put("mdattr","urn:oasis:names:tc:SAML:metadata:attribute")

            // avoid changing the dom4j singleton document factory, create a new one
            org.dom4j.DocumentFactory factory = new org.dom4j.DocumentFactory()
            factory.setXPathNamespaceURIs(nsContext)

            SAXReader reader = new SAXReader()
            reader.setIgnoreComments(false)
            reader.setIncludeInternalDTDDeclarations(false)
            reader.setIncludeExternalDTDDeclarations(false)

            reader.setDocumentFactory(factory)

            Document document = reader.read(new StringReader(xml))
            Element rootNode = document.getRootElement()
            return rootNode;
        }  catch(DocumentException de)  {
            log.info("Error parsing metadata.")
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

    // serialize
    def serialize(Provider provider) {
        Document xmlDoc = DocumentHelper.createDocument();
        Namespace saml2MetadataNs = new Namespace("md","urn:oasis:names:tc:SAML:2.0:metadata")
        Namespace digitalSignatureNs = new Namespace("ds","http://www.w3.org/2000/09/xmldsig#")
        Namespace encryptionNs = new Namespace("xenc","http://www.w3.org/2001/04/xmlenc#")
        Namespace saml2AssertionNs = new Namespace("saml","urn:oasis:names:tc:SAML:2.0:assertion")
        Namespace rootNs = new Namespace("","http://www.w3.org/2001/XMLSchema");
        Namespace attributeExtensionsNs = new Namespace("mdattr","urn:oasis:names:tc:SAML:metadata:attribute")

        // create new guid for ID
        String ID = "_" + UUID.randomUUID().toString().toUpperCase()

        // compute cacheDuration
        // The time interval in format "PnYnMnDTnHnMnS"
        // P indicates the period (required)
        // nY indicates the number of years
        // nM indicates the number of months n
        // D indicates the number of days
        // T indicates the start of a time section (required if you are going to specify hours, minutes, or seconds)
        // nH indicates the number of hours
        // nM indicates the number of minutes
        // nS indicates the number of seconds
        int cacheDurationPeriod = TBRProperties.getSaml2MetadataCacheDurationPeriod()
        String cachedDuration = "PT" + cacheDurationPeriod * 60 + "M"

        // compute validUntil
        Instant instant = Instant.ofEpochMilli(new Date().getTime())
        int periodOfValidity = TBRProperties.getSaml2MetadataValidUntilPeriod()
        instant = instant.plus(Duration.ofDays(periodOfValidity))
        String validUntil = instant.toString()

        // ROOT Element
        Element root = xmlDoc.addElement(new QName("EntityDescriptor", saml2MetadataNs))
                .addNamespace("mdattr","urn:oasis:names:tc:SAML:metadata:attribute")
                .addNamespace("saml","urn:oasis:names:tc:SAML:2.0:assertion")
                .addAttribute("ID", ID)
                .addAttribute("entityID", provider.entityId)
                .addAttribute("cacheDuration", cachedDuration) // 24 hours
                .addAttribute("validUntil", validUntil)

        // add attribute extensions
        Element extensions = root.addElement(new QName("Extensions", saml2MetadataNs))
        Element entityAttributes = extensions.addElement(new QName("EntityAttributes", attributeExtensionsNs))

        // add system name attribute
        Element systemNameAttribute = entityAttributes.addElement(new QName(ATTRIBUTE, saml2AssertionNs))
                .addAttribute(NAME, "https://nief.org/attribute-registry/attributes/entity/nief/SystemName/1.0/")
                .addAttribute(NAME_FORMAT, SAML2_ATTRIBUTE_NAME_FORMAT_URI)

        systemNameAttribute.addElement(new QName(ATTRIBUTE_VALUE, saml2AssertionNs))
                .setText(provider.name)

        // add trustmark recipient identifier attributes for organizations and systems
        if (provider.organization.trustmarkRecipientIdentifiers.size() > 0 || provider.trustmarkRecipientIdentifiers.size() > 0) {
            Element trustmarkRecipientIdentifiersAttribute = entityAttributes.addElement(new QName(ATTRIBUTE, saml2AssertionNs))
                    .addAttribute(NAME, "https://nief.org/attribute-registry/attributes/entity/trustmark/TrustmarkRecipientIdentifiers/1.0/")
                    .addAttribute(NAME_FORMAT, SAML2_ATTRIBUTE_NAME_FORMAT_URI)

            provider.organization.trustmarkRecipientIdentifiers.each { orgTri ->
                trustmarkRecipientIdentifiersAttribute.addElement(new QName(ATTRIBUTE_VALUE, saml2AssertionNs))
                        .setText(orgTri.trustmarkRecipientIdentifierUrl)
            }

            provider.trustmarkRecipientIdentifiers.each { sysTri ->
                trustmarkRecipientIdentifiersAttribute.addElement(new QName(ATTRIBUTE_VALUE, saml2AssertionNs))
                        .setText(sysTri.trustmarkRecipientIdentifierUrl)
            }
        }

        // add Partner System TIPs
        if (provider.partnerSystemsTips.size() > 0) {
            Element partnerSystemsTipsAttribute = entityAttributes.addElement(new QName(ATTRIBUTE, saml2AssertionNs))
                    .addAttribute(NAME, "https://nief.org/attribute-registry/attributes/entity/trustmark/TrustInteroperabilityProfiles/1.0/")
                    .addAttribute(NAME_FORMAT, SAML2_ATTRIBUTE_NAME_FORMAT_URI)

            provider.partnerSystemsTips.each { partnerSystemsTip ->
                partnerSystemsTipsAttribute.addElement(new QName(ATTRIBUTE_VALUE, saml2AssertionNs))
                        .setText(partnerSystemsTip.partnerSystemsTipIdentifier)
            }
        }

        // add trustmarks
        if (provider.trustmarks.size() > 0) {
            provider.trustmarks.each { tm ->
                Element trustmarksAttribute = entityAttributes.addElement(new QName(ATTRIBUTE, saml2AssertionNs))
                        .addAttribute(NAME, tm.trustmarkDefinitionURL)
                        .addAttribute(NAME_FORMAT, SAML2_ATTRIBUTE_NAME_FORMAT_URI)

                trustmarksAttribute.addElement(new QName(ATTRIBUTE_VALUE, saml2AssertionNs))
                        .setText(tm.url)
            }
        }

        // add attributes
        addAttributeToMetadata(entityAttributes, saml2AssertionNs,
                               "https://nief.org/attribute-registry/attributes/entity/gfipm/OwnerAgencyName/2.0/", provider.organization.name)

        if (StringUtils.isNotEmpty(provider.organization.description)) {
            addAttributeToMetadata(entityAttributes, saml2AssertionNs,
                    "https://nief.org/attribute-registry/attributes/entity/gfipm/OwnerAgencyDescriptionText/2.0/", provider.organization.description)
        }

        provider.attributes.each { attrb ->

            String name = attrb.name

            if (name == OWNER_CATEGORY_CODE) {
                name = "https://nief.org/attribute-registry/attributes/entity/gfipm/OwnerAgencyOrganizationGeneralCategoryCode/2.0/"
            }

            addAttributeToMetadata(entityAttributes, saml2AssertionNs, name, attrb.value)
        }

        // add tag attributes
        if (provider.tags.size() > 0) {
            Element tagAttributes = entityAttributes.addElement(new QName(ATTRIBUTE, saml2AssertionNs))
                    .addAttribute(NAME, "https://nief.org/attribute-registry/attributes/entity/nief/EntityTags/1.0/")
                    .addAttribute(NAME_FORMAT, SAML2_ATTRIBUTE_NAME_FORMAT_URI)

            provider.tags.each { tag ->
                tagAttributes.addElement(new QName(ATTRIBUTE_VALUE, saml2AssertionNs))
                .setText(tag)
            }
        }

        // add RoleDescriptor (IDP or SP)
        if (provider.providerType == ProviderType.SAML_SP) {
            Element roleDescriptor = root.addElement(new QName("SPSSODescriptor", saml2MetadataNs))

            if (provider.protocols.size() > 0) {
                String protocols = ""
                provider.protocols.each { prot ->
                    protocols += prot + " "
                }

                roleDescriptor.addAttribute("protocolSupportEnumeration", protocols.trim())
            }

            // singning key
            if (StringUtils.isNotEmpty(provider.signingCertificate)) {
                Element signingKey = roleDescriptor.addElement(new QName("KeyDescriptor", saml2MetadataNs))
                        .addAttribute("use", "signing")

                Element signingKeyInfo = signingKey.addElement(new QName("KeyInfo", digitalSignatureNs))
                        .addElement(new QName("X509Data", digitalSignatureNs))
                        .addElement(new QName("X509Certificate", digitalSignatureNs))
                        .setText(provider.signingCertificate)
            }

            // encrypting key
            if (StringUtils.isNotEmpty(provider.encryptionCertificate)) {
                Element encryptingKey = roleDescriptor.addElement(new QName("KeyDescriptor", saml2MetadataNs))
                        .addAttribute("use", "encryption")

                Element encryptingKeyInfo = encryptingKey.addElement(new QName("KeyInfo", digitalSignatureNs))
                        .addElement(new QName("X509Data", digitalSignatureNs))
                        .addElement(new QName("X509Certificate", digitalSignatureNs))
                        .setText(provider.encryptionCertificate)
            }

            // endpoints

            // to track indexed enpoints count
            Integer assertionConsumerServiceCounter = 0
            Integer attributeConsumingServiceCounter = 0
            Integer artifactResolutionServiceCounter = 0 // base

            def endpoints = provider.endpoints.findAll{endp -> endp.name == ARTIFACT_RESOLUTION_SERVICE_DISPLAY_NAME}
            // base
            endpoints.each { endp ->
                Element artifactResolutionService = roleDescriptor.addElement(new QName(ARTIFACT_RESOLUTION_SERVICE, saml2MetadataNs))
                        .addAttribute("Binding", endp.binding)
                        .addAttribute("Location", endp.url)
                        .addAttribute("index", artifactResolutionServiceCounter.toString())
                artifactResolutionServiceCounter++
            }

            endpoints = provider.endpoints.findAll{endp -> endp.name == SINGLE_LOGOUT_SERVICE_DISPLAY_NAME}
            // base
            endpoints.each { endp ->
                Element singleLogoutService = roleDescriptor.addElement(new QName(SINGLE_LOGOUT_SERVICE, saml2MetadataNs))
                        .addAttribute("Binding", endp.binding)
                        .addAttribute("Location", endp.url)
            }

            endpoints = provider.endpoints.findAll{endp -> endp.name == MANAGE_NAME_ID_SERVICE_DISPLAY_NAME}
            // base
            endpoints.each { endp ->
                Element managedNameIdService = roleDescriptor.addElement(new QName(MANAGE_NAME_ID_SERVICE, saml2MetadataNs))
                        .addAttribute("Binding", endp.binding)
                        .addAttribute("Location", endp.url)
            }

            // name id formats
            provider.nameFormats.each { format ->
                Element nameIdFormat = roleDescriptor.addElement(new QName("NameIDFormat", saml2MetadataNs))
                        .setText(format)
            }

            endpoints = provider.endpoints.findAll{endp -> endp.name == ASSERTION_CONSUMER_SERVICE_DISPLAY_NAME}
            endpoints.each { endp ->
                Element assertionConsumerService = roleDescriptor.addElement(new QName(ASSERTION_CONSUMER_SERVICE, saml2MetadataNs))
                        .addAttribute("Binding", endp.binding)
                        .addAttribute("Location", endp.url)
                        .addAttribute("index", assertionConsumerServiceCounter.toString())
                assertionConsumerServiceCounter++
            }

            endpoints = provider.endpoints.findAll{endp -> endp.name == ATTRIBUTE_CONSUMING_SERVICE_DISPLAY_NAME}
            endpoints.each { endp ->
                Element attributeConsumingService = roleDescriptor.addElement(new QName(ATTRIBUTE_CONSUMING_SERVICE, saml2MetadataNs))
                        .addAttribute("Binding", endp.binding)
                        .addAttribute("Location", endp.url)
                        .addAttribute("index", attributeConsumingServiceCounter.toString())
                attributeConsumingServiceCounter++

                Element serviceName = attributeConsumingService.addElement(new QName("ServiceName", saml2MetadataNs))
                        .addAttribute("xml:lang", "en")
                        .setText(endp.serviceName)

                endp.attributes.each { endpAttrb ->
                    if (endpAttrb.name != SERVICE_NAME) {
                        Element endpointAttribute = attributeConsumingService.addElement(new QName(REQUESTED_ATTRIBUTE, saml2MetadataNs))
                                .addAttribute(NAME, endpAttrb.name)
                                .addAttribute(NAME_FORMAT, endpAttrb.value)
                    }
                }
            }

        } else if (provider.providerType == ProviderType.SAML_IDP) {

            Element roleDescriptor = root.addElement(new QName("IDPSSODescriptor", saml2MetadataNs))

            if (provider.protocols.size() > 0) {
                String protocols = ""
                provider.protocols.each { prot ->
                    protocols += prot + " "
                }

                roleDescriptor.addAttribute("protocolSupportEnumeration", protocols.trim())
            }

            // singning key
            if (StringUtils.isNotEmpty(provider.signingCertificate)) {
                Element signingKey = roleDescriptor.addElement(new QName("KeyDescriptor", saml2MetadataNs))
                        .addAttribute("use", "signing")

                Element signingKeyInfo = signingKey.addElement(new QName("KeyInfo", digitalSignatureNs))
                        .addElement(new QName("X509Data", digitalSignatureNs))
                        .addElement(new QName("X509Certificate", digitalSignatureNs))
                        .setText(provider.signingCertificate)
            }

            // encrypting key
            if (StringUtils.isNotEmpty(provider.encryptionCertificate)) {
                Element encryptingKey = roleDescriptor.addElement(new QName("KeyDescriptor", saml2MetadataNs))
                        .addAttribute("use", "encryption")

                Element encryptingKeyInfo = encryptingKey.addElement(new QName("KeyInfo", digitalSignatureNs))
                        .addElement(new QName("X509Data", digitalSignatureNs))
                        .addElement(new QName("X509Certificate", digitalSignatureNs))
                        .setText(provider.encryptionCertificate)
            }

            // endpoints
            Integer artifactResolutionServiceCounter = 0 // base

            def endpoints = provider.endpoints.findAll{endp -> endp.name == ARTIFACT_RESOLUTION_SERVICE_DISPLAY_NAME}
            // base
            endpoints.each { endp ->
                Element artifactResolutionService = roleDescriptor.addElement(new QName(ARTIFACT_RESOLUTION_SERVICE, saml2MetadataNs))
                        .addAttribute("Binding", endp.binding)
                        .addAttribute("Location", endp.url)
                        .addAttribute("index", artifactResolutionServiceCounter.toString())
                artifactResolutionServiceCounter++
            }

            endpoints = provider.endpoints.findAll{endp -> endp.name == SINGLE_LOGOUT_SERVICE_DISPLAY_NAME}
            // base
            endpoints.each { endp ->
                Element artifactResolutionService = roleDescriptor.addElement(new QName(SINGLE_LOGOUT_SERVICE, saml2MetadataNs))
                        .addAttribute("Binding", endp.binding)
                        .addAttribute("Location", endp.url)
            }

            endpoints = provider.endpoints.findAll{endp -> endp.name == MANAGE_NAME_ID_SERVICE_DISPLAY_NAME}
            // base
            endpoints.each { endp ->
                Element managedNameIdService = roleDescriptor.addElement(new QName(MANAGE_NAME_ID_SERVICE, saml2MetadataNs))
                        .addAttribute("Binding", endp.binding)
                        .addAttribute("Location", endp.url)
            }

            // name id formats
            provider.nameFormats.each { format ->
                Element nameIdFormat = roleDescriptor.addElement(new QName("NameIDFormat", saml2MetadataNs))
                        .setText(format)
            }

            endpoints = provider.endpoints.findAll{endp -> endp.name == SINGLE_SIGNON_SERVICE_DISPLAY_NAME}
            endpoints.each { endp ->
                Element signleSignonService = roleDescriptor.addElement(new QName(SINGLE_SIGNON_SERVICE, saml2MetadataNs))
                        .addAttribute("Binding", endp.binding)
                        .addAttribute("Location", endp.url)
            }

            // add IDP saml attributes
            if (provider.idpAttributes.size() > 0) {

                provider.idpAttributes.each { attrb ->
                    roleDescriptor.addElement(new QName(ATTRIBUTE, saml2AssertionNs))
                            .addAttribute(NAME, attrb)
                            .addAttribute(NAME_FORMAT, SAML2_ATTRIBUTE_NAME_FORMAT_URI)
                }
            }
        }

        // organization
        Element organization = root.addElement(new QName("Organization", saml2MetadataNs))

        organization.addElement(new QName(ORG_NAME, saml2MetadataNs))
            .addAttribute("xml:lang", "en")
            .setText(provider.organization.name)

        organization.addElement(new QName(ORG_DISPLAY_NAME, saml2MetadataNs))
                .addAttribute("xml:lang", "en")
                .setText(provider.organization.displayName)

        organization.addElement(new QName(ORG_URL, saml2MetadataNs))
                .addAttribute("xml:lang", "en")
                .setText(provider.organization.siteUrl)

        // contacts
        if (provider.contacts.size() > 0) {
            provider.contacts.each { contact ->
                Element contactPerson = root.addElement(new QName(CONTACT_PERSON, saml2MetadataNs))
                    .addAttribute(CONTACT_TYPE, contact.type.name().toLowerCase())

                contactPerson.addElement(new QName(CONTACT_COMPANY, saml2MetadataNs))
                        .setText(provider.organization.name)

                if (StringUtils.isNotEmpty(contact.firstName)) {
                    contactPerson.addElement(new QName(CONTACT_GIVEN_NAME, saml2MetadataNs))
                            .setText(contact.firstName)
                }

                if (StringUtils.isNotEmpty(contact.lastName)) {
                    contactPerson.addElement(new QName(CONTACT_SURNAME, saml2MetadataNs))
                            .setText(contact.lastName)
                }

                if (StringUtils.isNotEmpty(contact.email)) {
                    contactPerson.addElement(new QName(CONTACT_EMAIL, saml2MetadataNs))
                            .setText(contact.email)
                }

                if (StringUtils.isNotEmpty(contact.phone)) {
                    contactPerson.addElement(new QName(CONTACT_PHONE, saml2MetadataNs))
                            .setText(contact.phone)
                }
            }
        }

        // serialize to string
        OutputFormat outputFormat = OutputFormat.createPrettyPrint()
        StringWriter stringWriter = new StringWriter()
        XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat)
        xmlWriter.write(xmlDoc)
        xmlWriter.close()

//        log.info("Serialized Saml Metadata:")
//        log.info("${stringWriter.toString()}")

        SigningCertificate signingCertificate = queryDefaultCertificate()

        String signedSamlMetadata = signSamlMetadata(signingCertificate, provider, stringWriter.toString())

        provider.validUntilDate = instant
        provider.saml2MetadataXml = signedSamlMetadata

        // save the metadata url
        String saml2MetadataUrl = getMetadataUrl(provider.id)
        provider.saml2MetadataUrl = saml2MetadataUrl

        saveProvider(provider)

//        log.info("Signed Saml Metadata:")
//        log.info("${signedSamlMetadata}")
    }

    private void addAttributeToMetadata(Element entityAttributes, Namespace saml2AssertionNs, String name, String value) {
        Element attribute = entityAttributes.addElement(new QName(ATTRIBUTE, saml2AssertionNs))
                .addAttribute(NAME, name)
                .addAttribute(NAME_FORMAT, SAML2_ATTRIBUTE_NAME_FORMAT_URI)

        attribute.addElement(new QName(ATTRIBUTE_VALUE, saml2AssertionNs))
                .setText(value)
    }

    SigningCertificate queryDefaultCertificate() {
        SigningCertificate.all.find { it ->
            it.defaultCertificate == true
        }
    }

    String signSamlMetadata(SigningCertificate signingCertificate, Provider provider, String samlMetadataXml) {

        log.info("Generating XML signature for provider <${provider?.name}, ${provider?.entityId}>")

        // get the X509 certificate and private key
        X509CertificateService certService = new X509CertificateService()
        X509Certificate x509Certificate = certService.convertFromPem(signingCertificate.x509CertificatePem)
        PrivateKey privateKey = certService.getPrivateKeyFromPem(signingCertificate.privateKeyPem)

        // Generate XML Signature

        // get the signed trustmark's XML string
        XmlSignatureImpl xmlSignature = new XmlSignatureImpl()

        String referenceUri = "ID"
        String signedXml = xmlSignature.generateXmlSignature(x509Certificate, privateKey,
                referenceUri, samlMetadataXml)

        // Validate the SAML 2 metadata xml schema
        boolean validXmlSchema = validateSaml2MetadataSchema(signedXml)

        if (!validXmlSchema) {
            throw new ServletException("The SAML 2 metadata's XML failed schema validation.")
        }

        log.info("Successfully validated SAML 2 metadata XML")

        // validate the signature before saving
        boolean validXmlSignature = xmlSignature.validateXmlSignature(referenceUri, signedXml)

        if (!validXmlSignature) {
            throw new ServletException("The Trustmark's XML signature failed validation.")
        }

        Instant currentInstant = Instant.now();
        Date currentDate = Date.from(currentInstant);

        provider.lastTimeSAMLMetadataGeneratedDate = currentDate

        log.info("Successfully signed SAML 2 metadata XML at ${currentDate.toString()}")

        // save the signed trustmark's XML string to the db
        return signedXml
    }

    public boolean validateSaml2MetadataSchema(String metadataXml) {
        log.info("Validating metadata xml schema...")

        boolean result = false

        def saml2MetadataResourceFile = this.class.classLoader.getResource(SAML2_METADATA_SCHEMA).file
        def saml2attributeResourceFile = this.class.classLoader.getResource(SAML2_ATTRIBUTE_SCHEMA).file
        def digitalSignatureResourceFile = this.class.classLoader.getResource(DIGITAL_SIGNATURE_SCHEMA).file
        def encryptionResourceFile = this.class.classLoader.getResource(ENCRYPTION_SCHEMA).file

        Source metadataSchemaFile = new StreamSource(new File(saml2MetadataResourceFile))
        Source attributeSchemaFile = new StreamSource(new File(saml2attributeResourceFile))
        Source signatureSchemaFile = new StreamSource(new File(digitalSignatureResourceFile))
        Source encryptionSchemaFile = new StreamSource(new File(encryptionResourceFile))

        InputStream stream = new ByteArrayInputStream(metadataXml.getBytes(StandardCharsets.UTF_8))

        Source xmlStreamSource = new StreamSource(stream)

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        try {
            //Schema schema = schemaFactory.newSchema(schemaFile)
            List<Source> sources = new ArrayList<Source>()
            sources.add(metadataSchemaFile)
            sources.add(attributeSchemaFile)
            sources.add(signatureSchemaFile)
            sources.add(encryptionSchemaFile)

            Source[] schemas = sources.toArray()

            Schema schema = schemaFactory.newSchema(schemas);

            Validator validator = schema.newValidator()
            validator.validate(xmlStreamSource)

            result = true

            log.debug("Successfully validated SAML 2 metadata file...")
        } catch (SAXException e) {
            log.debug(xmlStreamSource.getSystemId() + " is NOT valid, reason:" + e)

        } catch (IOException e) {
            log.debug(xmlStreamSource.getSystemId() + " error:" + e)
        }

        return result
    }

    private String getMetadataUrl(int id) {
        StringBuilder sb = new StringBuilder()
        def baseAppUrl = TBRProperties.getProperties().getProperty("tf.base.url")
        sb.append(baseAppUrl)
        sb.append("/system/saml2Metadata/${id}")

        return sb.toString()
    }
}

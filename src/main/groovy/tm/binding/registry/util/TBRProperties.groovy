package tm.binding.registry.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource

class TBRProperties {

private static final Logger log = LoggerFactory.getLogger(TBRProperties.class)

public static final String BUNDLE_NAME = "tbr_config.properties"

final static String BASE_URL = "tf.base.url"

final static String NAME = "tbr.org.title"

private static Properties RESOURCES = new Properties()
static {
    try {
        log.info("Initializing Binding Registry Properties...")
        ClassPathResource classPathResource = new ClassPathResource(BUNDLE_NAME)
        InputStream inputStream = classPathResource.getInputStream()
        String propertiesTxt = inputStream.text
        if( log.isDebugEnabled() )
            log.debug("### OUTPUT OF ${BUNDLE_NAME}: \n"+propertiesTxt+"\n### END OF ${BUNDLE_NAME}")
        RESOURCES.load(new StringReader(propertiesTxt))
    } catch(Throwable t){
        log.error("Error reading Binding Registry Tool Config properties!", t)
        throw new RuntimeException("Cannot load Binding Registry Config properties: "+BUNDLE_NAME, t)
    }
}

static ResourceBundle getBundle(){return null;}

static Properties getProperties(){return RESOURCES;}

    static String getFilesDirectory(){
        return getString("registry.tool.filesdir", "/tmp/tfam")
    }

    private static String getString(String property){
        return getString(property, null);
    }

    private static String getString(String property, String defaultValue){
        Properties props = getProperties();
        if( props != null ){
            try{
                String val = props.getProperty(property);
                if( val != null && val.trim().length() > 0 ){
                    return val.trim();
                }else{
                    return defaultValue;
                }
            }catch(Throwable t){
                return defaultValue;
            }
        }else{
            return defaultValue;
        }
    }

    private static boolean getBoolean(String property){
        return getBoolean(property, null);
    }
    private static boolean getBoolean(String property, boolean defaultValue){
        String value = getString(property, null);
        if( value ){
            try{
                return Boolean.parseBoolean(value);
            }catch(Throwable T){
                return defaultValue;
            }
        }else{
            return defaultValue;
        }
    }

    static String getBaseUrl(){
        return getString(BASE_URL, "http://localhost:8082/tbr")
    }

    static String getTbrName() {
        return getString(NAME, "Trustmark Binding Registry")
    }

    static URL getRegistryUrl(){
        if (getString("registry.url") == null)  {
            return null;
        }
        return new URL(getString("registry.url"))
    }

    static void setRegistryUrl(String urls){
        String currentUrl = getString("registry.url");
        if(currentUrl == null)  {
            RESOURCES.setProperty("registry.url", urls);
        } else  {
            RESOURCES.setProperty("registry.url", currentUrl +"|" + urls);
        }
    }

    static Integer getSaml2MetadataValidUntilPeriod() {
        return Integer.parseInt(getString("saml2.metadata.valid.until", "7"))
    }

    static Integer getSaml2MetadataCacheDurationPeriod() {
        return Integer.parseInt(getString("saml2.metadata.cache.duration", "24"))
    }

    static String getAdminEmail() {
        return getString("org.contact.1.email", "help@trustmarkinitiative.org")
    }

    static String getPublicDocumentApi()  {
        return getProperties().getProperty(BASE_URL)+"/public/documents"
    }

    static boolean getIsApiClientAuthorizationRequired() {
        return getBoolean("api_client_authorization_required", false)
    }
}


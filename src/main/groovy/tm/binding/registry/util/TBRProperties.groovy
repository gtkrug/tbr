package tm.binding.registry.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource

class TBRProperties {

private static final Logger log = LoggerFactory.getLogger(TBRProperties.class)

public static final String BUNDLE_NAME = "tbr_config.properties"

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

}


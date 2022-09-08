package tm.binding.registry.util;

import edu.gatech.gtri.trustmark.v1_0.impl.io.IOUtils;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtilities {
    public static boolean checkTATStatusUrl(String tatStatusUrl) {
        try{

            JSONObject json = IOUtils.fetchJSON(ensureTrailingSlash(tatStatusUrl) + "public/status");


            if (json != null) {
                String status = (String)json.getString("status");
                if (status.equals("OK")) {
                    return true;
                }
            }

            return false;
        }catch(Throwable t){
            return false;
        }
    }

    public static boolean checkTPATStatusUrl(String tpatBaseUrl) {
        try {
            String tpatStatusUrl = ensureTrailingSlash(tpatBaseUrl) + "status";

            JSONObject json = IOUtils.fetchJSON(tpatStatusUrl);

            if (json!= null) {
                String baseUrl = (String)json.getString("baseUrl");
                if (baseUrl.equals(tpatBaseUrl)) {
                    return true;
                }
            }

            return false;
        }catch(Throwable t){
            return false;
        }
    }

    public static String artifactBaseUrl(String tipOrTdUri) throws MalformedURLException {
        URL url = new URL(tipOrTdUri);
        String path = url.getFile().substring(0, url.getFile().lastIndexOf('/'));

        String[] segments = path.split("/");

        // get second element since first is empty
        String firstFolder = segments[1];

        URL tpatBaseUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), ensureLeadingSlash(firstFolder));

        return tpatBaseUrl.toString();
    }

    private static String ensureLeadingSlash(String url) {
        return url.startsWith("/") ? url : "/" + url;
    }

    private static String ensureTrailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

}

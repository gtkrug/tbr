package tm.binding.registry

/**
 * Filter out Trustmark Definitions by URI for Organizations.
 */
public class OrganizationTrustmarkDefinitionUriFilter implements TrustmarkDefinitionUriFilter {

    public OrganizationTrustmarkDefinitionUriFilter() {
    }

    @Override
    boolean filter(String tdUri) {
        return true;
    }
}
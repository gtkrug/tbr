package tm.binding.registry

/**
 * Filter out Trustmark Definitions by URI for Systems.
 */
public class SystemTrustmarkDefinitionUriFilter implements TrustmarkDefinitionUriFilter {
    Set<String> tdSet;

    public SystemTrustmarkDefinitionUriFilter(Set<String> tdSet) {
        this.tdSet = tdSet;
    }

    @Override
    boolean filter(String tdUri) {
        return tdSet.contains(tdUri);
    }
}
package tm.binding.registry

/**
 * Filter out Trustmark Definitions by URI.
 */
public interface TrustmarkDefinitionUriFilter {
    boolean filter(String tdUri);
}
package tm.binding.registry;

import java.util.*;

public class OidcRelyingPartyMetadataProcessor extends OidcBaseMetadataProcessor {

    private static final Map<String, String> clientMetadataParametersToLabelsMap;

    public OidcRelyingPartyMetadataProcessor() {

    }

    static {
        // Start with the base OAuth 2.0 client params
        clientMetadataParametersToLabelsMap = new HashMap<>();

        // OIDC federation
        clientMetadataParametersToLabelsMap.put("client_registration_types", "Client Registration Types");
        clientMetadataParametersToLabelsMap.put("organization_name", "Organization Name");
        clientMetadataParametersToLabelsMap.put("signed_jwks_uri", "Signed JWKs URI");

        clientMetadataParametersToLabelsMap.put("scope", "Scope");

        // OIDC Client Registration
        clientMetadataParametersToLabelsMap.put("redirect_uris", "Redirect URIs");
        clientMetadataParametersToLabelsMap.put("response_types", "Response Types");
        clientMetadataParametersToLabelsMap.put("grant_types", "Grant Types");
        clientMetadataParametersToLabelsMap.put("application_type", "Application Type");
        clientMetadataParametersToLabelsMap.put("contacts", "Contacts");
        clientMetadataParametersToLabelsMap.put("client_name", "Client Name");
        clientMetadataParametersToLabelsMap.put("logo_uri", "Logo URI");
        clientMetadataParametersToLabelsMap.put("client_uri", "Client URI");
        clientMetadataParametersToLabelsMap.put("policy_uri", "Policy URI");
        clientMetadataParametersToLabelsMap.put("tos_uri", "Terms Of Service URI");
        clientMetadataParametersToLabelsMap.put("jwks_uri", "JSON Web Key Set URI");
        clientMetadataParametersToLabelsMap.put("jwks", "JSON Web Key Set");
        clientMetadataParametersToLabelsMap.put("sector_identifier_uri", "Sector Identifier URI");
        clientMetadataParametersToLabelsMap.put("subject_type", "Subject Type");

        clientMetadataParametersToLabelsMap.put("id_token_signed_response_alg", "ID Token Signed Response JWS Algorithm");
        clientMetadataParametersToLabelsMap.put("id_token_encrypted_response_alg", "ID Token Encrypted Response JWE Algorithm");
        clientMetadataParametersToLabelsMap.put("id_token_encrypted_response_enc", "ID Token Encrypted Response JWE Encryption Algorithm");

        clientMetadataParametersToLabelsMap.put("userinfo_signed_response_alg", "User Info Signed Response JWS Algorithm");
        clientMetadataParametersToLabelsMap.put("userinfo_encrypted_response_alg", "User Info Encrypted Response JWE Algorithm");
        clientMetadataParametersToLabelsMap.put("userinfo_encrypted_response_enc", "User Info Encrypted Response JWE Encryption Algorithm");

        clientMetadataParametersToLabelsMap.put("request_object_signing_alg", "Request Object Signing JWS Algorithm");
        clientMetadataParametersToLabelsMap.put("request_object_encryption_alg", "Request Object Signing JWE Algorithm");
        clientMetadataParametersToLabelsMap.put("request_object_encryption_enc", "Request Object Signing JWE Encryption Algorithm");

        clientMetadataParametersToLabelsMap.put("token_endpoint_auth_method", "Token Endpoint Authentication Method");
        clientMetadataParametersToLabelsMap.put("token_endpoint_auth_signing_alg", "Token Endpoint Authentication Signing JWS Algorithm");

        clientMetadataParametersToLabelsMap.put("default_max_age", "Default Maximum Auth Age");
        clientMetadataParametersToLabelsMap.put("require_auth_time", "Require Auth Time");
        clientMetadataParametersToLabelsMap.put("default_acr_values", "Default Auth Context Reference Values");
        clientMetadataParametersToLabelsMap.put("initiate_login_uri", "Initiate Login URI");

        clientMetadataParametersToLabelsMap.put("request_uris", "Request URIs");
    }

    Optional<Map<String, Object>> getLabelValueMap(String jsonMetadataString) {
        return super.getLabelValueMap(jsonMetadataString, OidcRelyingPartyMetadataProcessor.clientMetadataParametersToLabelsMap);
    }
}

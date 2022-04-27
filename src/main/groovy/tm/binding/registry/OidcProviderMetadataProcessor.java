package tm.binding.registry;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OidcProviderMetadataProcessor extends OidcBaseMetadataProcessor {

    private static final Map<String, String> providerMetadataParametersToLabelsMap;

    public OidcProviderMetadataProcessor() {
    }

    static {
        // Start with the base OAuth 2.0 client params
        providerMetadataParametersToLabelsMap = new HashMap<>();

        // OIDC federation
        providerMetadataParametersToLabelsMap.put("client_registration_types_supported", "Client Registration Types Supported");
        providerMetadataParametersToLabelsMap.put("organization_name", "Organization Name");
        providerMetadataParametersToLabelsMap.put("federation_registration_endpoint", "Federation Registration Endpoint");
        providerMetadataParametersToLabelsMap.put("request_authentication_methods_supported", "Request Authentication Methods Supported");
        providerMetadataParametersToLabelsMap.put("signed_jwks_uri", "Signed JSON Web Key Set URI");


        // OIDC Provider Discovery
        providerMetadataParametersToLabelsMap.put("issuer", "Issuer");
        providerMetadataParametersToLabelsMap.put("authorization_endpoint", "Authorization Endpoint");
        providerMetadataParametersToLabelsMap.put("token_endpoint", "Token Endpoint");
        providerMetadataParametersToLabelsMap.put("userinfo_endpoint", "User Info Endpoint");
        providerMetadataParametersToLabelsMap.put("jwks_uri", "JSON Web Key Set URI");
        providerMetadataParametersToLabelsMap.put("registration_endpoint", "Registration Endpoint");
        providerMetadataParametersToLabelsMap.put("scopes_supported", "Scopes Supported");
        providerMetadataParametersToLabelsMap.put("response_types_supported", "Response Types Supported");
        providerMetadataParametersToLabelsMap.put("response_modes_supported", "Response Modes Supported");
        providerMetadataParametersToLabelsMap.put("grant_types_supported", "Grant Types Supported");
        providerMetadataParametersToLabelsMap.put("acr_values_supported", "Authentication Context Class References Supported");
        providerMetadataParametersToLabelsMap.put("subject_types_supported", "Subject Types Supported");

        providerMetadataParametersToLabelsMap.put("id_token_signing_alg_values_supported", "ID Token JWS Signing Algorithms Supported");
        providerMetadataParametersToLabelsMap.put("id_token_encryption_alg_values_supported", "ID Token JWE (alg) Encryption Algorithms Supported");
        providerMetadataParametersToLabelsMap.put("id_token_encryption_enc_values_supported", "ID Token JWE (enc) Encryption Algorithms Supported");

        providerMetadataParametersToLabelsMap.put("userinfo_signing_alg_values_supported", "User Info JWS Signing Algorithms Supported");
        providerMetadataParametersToLabelsMap.put("userinfo_encryption_alg_values_supported", "User Info Token JWE (alg) Encryption Algorithms Supported");
        providerMetadataParametersToLabelsMap.put("userinfo_encryption_enc_values_supported", "User Info Token JWE (enc) Encryption Algorithms Supported");

        providerMetadataParametersToLabelsMap.put("request_object_signing_alg_values_supported", "Request Object JWS Signing Algorithms Supported");
        providerMetadataParametersToLabelsMap.put("request_object_encryption_alg_values_supported", "Request Object Token JWE (alg) Encryption Algorithms Supported");
        providerMetadataParametersToLabelsMap.put("request_object_encryption_enc_values_supported", "Request Object Token JWE (enc) Encryption Algorithms Supported");


        providerMetadataParametersToLabelsMap.put("token_endpoint_auth_methods_supported", "Token Endpoint Auth Methods Supported");
        providerMetadataParametersToLabelsMap.put("token_endpoint_auth_signing_alg_values_supported", "Token Endpoint JWS Signing Algorithms Supported");

        providerMetadataParametersToLabelsMap.put("display_values_supported", "Display Values Supported");
        providerMetadataParametersToLabelsMap.put("claim_types_supported", "Claim Types Supported");

        providerMetadataParametersToLabelsMap.put("claims_supported", "Claims Supported");
        providerMetadataParametersToLabelsMap.put("service_documentation", "Service Documentation");

        providerMetadataParametersToLabelsMap.put("claims_locales_supported", "Claims Locales Supported");
        providerMetadataParametersToLabelsMap.put("ui_locales_supported", "User Interface Locales Supported");
        providerMetadataParametersToLabelsMap.put("claims_parameter_supported", "Claims Parameter Supported");
        providerMetadataParametersToLabelsMap.put("request_parameter_supported", "Request Parameter Supported");

        providerMetadataParametersToLabelsMap.put("request_uri_parameter_supported", "Request URI Parameter Supported");

        providerMetadataParametersToLabelsMap.put("require_request_uri_registration", "Require Request URI Registration");
        providerMetadataParametersToLabelsMap.put("op_policy_uri", "OpenId Provider Policy URI");
        providerMetadataParametersToLabelsMap.put("op_tos_uri", "OpenId Provider Terms Of Service URI");

        // OIDC Session Management
        providerMetadataParametersToLabelsMap.put("check_session_iframe", "OpenID Provider iframe URL");
    }

    Optional<Map<String, Object>> getLabelValueMap(String jsonMetadataString) {
        return super.getLabelValueMap(jsonMetadataString, OidcProviderMetadataProcessor.providerMetadataParametersToLabelsMap);
    }
}

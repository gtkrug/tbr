package tm.binding.registry;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class OidcBaseMetadataProcessor {
    private static final Logger log = LoggerFactory.getLogger(UserPasswordEncoderListener.class);

    private JSONObject metadata = null;

    public OidcBaseMetadataProcessor() {
    }

    @Override
    public String toString() {
        if (metadata != null)  {
            return metadata.toString();
        }

        return "";
    }

    public void parseMetadata(String jsonMetadataString) {
        if (StringUtils.isEmpty(jsonMetadataString)) {
            throw new IllegalArgumentException("The OIDC metadata string cannot be null or an empty string.");
        }

        try {
            metadata = new JSONObject(jsonMetadataString);
        } catch (JSONException e) {
            log.error("Unexpected JSON error: ${e.getMessage()}");
        }
    }

    public Optional<String> getMetadataParameterValue(String metadataParameter) {
        if (metadata.has(metadataParameter)) {
            Optional<String> opt = Optional.of((String) metadata.get(metadataParameter));
            return opt;
        }

        return Optional.empty();
    }

    public Optional<List<String>> getMetadataParameterValues(String metadataParameter) {
        if (metadata.has(metadataParameter)) {
            org.json.JSONArray parameterValuesArray = (org.json.JSONArray) metadata.getJSONArray(metadataParameter);
            List<String> parameterValues = StreamSupport.stream(parameterValuesArray.spliterator(), false)
                    .map(s -> (String) s)
                    .collect(Collectors.toList());
            return Optional.of(parameterValues);
        }

        return Optional.empty();
    }

    abstract Optional<Map<String, Object>> getLabelValueMap(String jsonMetadataString);

    public Optional<Map<String, Object>> getLabelValueMap(String jsonMetadataString, Map<String, String> metadataPropertiesToLabelsMap) {
        if (StringUtils.isEmpty(jsonMetadataString)) {
            throw new IllegalArgumentException("The OIDC metadata string cannot be a null or empty string.");
        }

        final JSONObject metadata = new JSONObject(jsonMetadataString);

        Map<String, Object> resultMap = new HashMap<>();

        metadataPropertiesToLabelsMap.forEach((key, val) -> {
                if (metadata.has(key)) {
                    Optional<Object> opt = Optional.of((Object) metadata.get(key));

                    if (opt.isPresent()) {
                        if (opt.get().getClass() == JSONArray.class) {
                            org.json.JSONArray jsonArray = (org.json.JSONArray) metadata.getJSONArray(key);
                            List<String> values = StreamSupport.stream(jsonArray.spliterator(), false)
                                    .map(s -> (String) s)
                                    .collect(Collectors.toList());
                            resultMap.put(val, values);
                        } else {
                            resultMap.put(val, opt.get());
                        }
                    }
                }
            }
        );

        return Optional.of(resultMap);
    }

    public Optional<String> getValue(String key) {
        if (metadata.has(key)) {
            return Optional.of(metadata.getString(key));
        }

        return Optional.empty();
    }

    // add methods
    public void addStringValue(String key, String value) {
        if (metadata != null) {
            metadata.put(key, value);
        }
    }

    public void addStringParameter(String keyParent, String keyChild, String parameter) {
        if (metadata != null) {

            if (metadata.has(keyParent)) {
                metadata.getJSONObject(keyParent).put(keyChild, parameter);
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(keyChild, parameter);

                metadata.put(keyParent, jsonObject);
            }
        }
    }

    public void addListParameters(String keyParent, String keyChild, List<String> parameterList) {
        if (metadata != null) {
            JSONArray arr = new JSONArray();
            parameterList.forEach(p -> arr.put(p));

            if (metadata.has(keyParent)) {
                metadata.getJSONObject(keyParent).put(keyChild, arr);
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(keyChild, arr);

                metadata.put(keyParent, jsonObject);
            }
        }
    }

    public void addMapParameters(String keyParent, String keyChild, Map<String, String> parameterMap) {
        if (metadata != null) {
            JSONArray arr = new JSONArray();

            parameterMap.forEach((k, v) -> {
                JSONObject t = new JSONObject();
                t.put(k, v);
                arr.put(t);
            });

            if (metadata.has(keyParent)) {
                metadata.getJSONObject(keyParent).put(keyChild, arr);
            } else {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(keyChild, arr);

                metadata.put(keyParent, jsonObject);
            }
        }
    }
}

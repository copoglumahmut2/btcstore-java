package util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
public class JsonUtil<T> {

    public static String convertObjectToJson(Object object) {
        try {
            var objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return objectWriter.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Exception while converting json...", ExceptionUtils.getMessage(e));
            return StringUtils.EMPTY;
        }
    }

    public static String convertObjectToJsonWithoutPrettyPrinter(Object object) {
        try {
            var objectWriter = new ObjectMapper().writer();
            return objectWriter.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Exception while converting json(without pretty printer)...", ExceptionUtils.getMessage(e));
            return StringUtils.EMPTY;
        }
    }

    public static boolean isValidJSON(final String json) {
        boolean valid;
        if(StringUtils.isEmpty(json)){
            valid = Boolean.FALSE;
        } else{
            try {
                final JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json);
                while (parser.nextToken() != null) {}
                valid = Boolean.TRUE;
            } catch (Exception e) {
                valid = Boolean.FALSE;
            }
        }
        return valid;
    }

    public static <T> T convertJsonToObject(String json, Class tclass) {
        try {
            var objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
            final DeserializationConfig originalConfig = objectMapper.getDeserializationConfig();
            final DeserializationConfig newConfig = originalConfig.with(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            objectMapper.setConfig(newConfig);
            return (T) objectMapper.readValue(json, tclass);
        } catch (JsonProcessingException e) {
            log.error("Exception while mapping json...", ExceptionUtils.getMessage(e));
            return null;
        }
    }

}

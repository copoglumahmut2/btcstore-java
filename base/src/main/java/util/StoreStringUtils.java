package util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class StoreStringUtils extends StringUtils {

    public static final Map<String, String> turkishChars = new HashMap<>();
    public static final Map<String, String> turkishCharsHtmlEncoding = new HashMap<>();

    static {
        turkishChars.put("ç", "c");
        turkishChars.put("Ç", "C");
        turkishChars.put("ğ", "g");
        turkishChars.put("Ğ", "G");
        turkishChars.put("ö", "o");
        turkishChars.put("Ö", "O");
        turkishChars.put("ü", "u");
        turkishChars.put("Ü", "U");
        turkishChars.put("i", "ı");
        turkishChars.put("İ", "I");
        turkishChars.put("ş", "s");
        turkishChars.put("Ş", "S");
    }

    static {
        turkishCharsHtmlEncoding.put("ç", "&ccedil;");
        turkishCharsHtmlEncoding.put("Ç", "&Ccedil;");
        turkishCharsHtmlEncoding.put("ö", "&ouml;");
        turkishCharsHtmlEncoding.put("Ö", "&Ouml;");
        turkishCharsHtmlEncoding.put("ü", "&uuml;");
        turkishCharsHtmlEncoding.put("Ü", "&Uuml;");
        turkishCharsHtmlEncoding.put("ş", "&scedil;");
        turkishCharsHtmlEncoding.put("Ş", "&Scedil;");
    }


    /**
     * @param text
     * @return given text
     * @apiNote clear turkish character ÇÇÇÇ-->CCCC
     */

    public static String clearTurkishCharacter(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{Mn}", EMPTY);
    }


    /**
     * @param text
     * @return given text
     * @apiNote convert turkish character ccc-->ççç
     */

    public static String convertTurkishCharacter(String text) {
        for (var entry : turkishChars.entrySet()) {
            text = StringUtils.replace(text, entry.getValue(), entry.getKey());
        }
        return text;

    }

    public static String decodeBase64(String encoded) {
        var decodedByte = Base64.getDecoder().decode(encoded);
        return new String(decodedByte);
    }


    public static String escapeHtml(String text) {
        text = StringEscapeUtils.escapeHtml4(text);
        for (var entry : turkishCharsHtmlEncoding.entrySet()) {
            text = StringUtils.replace(text, entry.getValue(), entry.getKey());
        }
        return text;

    }

}

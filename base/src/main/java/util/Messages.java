package util;

import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {

    @Getter
    @Setter
    private static Locale messagesLocale;

    public static String getMessageForLocale(String messageKey, Locale locale) {
        return ResourceBundle.getBundle("messages", locale)
                .getString(messageKey);
    }

    public static String getMessageForLocale(String messageKey, Locale locale, Object... args) {
        try {
            String pattern = ResourceBundle.getBundle("messages", locale).getString(messageKey);
            return MessageFormat.format(pattern, args);
        } catch (MissingResourceException e) {
            return messageKey;
        }
    }

    public static String getMessageForIsoCode(String messageKey, String isoCode, Object... args) {
        Locale locale = (isoCode != null && !isoCode.isBlank())
                ? new Locale(isoCode.toLowerCase())
                : Locale.getDefault();
        return getMessageForLocale(messageKey, locale, args);
    }

    public static String getLocalizedMessageWithKey(String key){
        LocalizedException localizedException = new LocalizedException(key,
                getMessagesLocale()!=null ? getMessagesLocale() : Locale.getDefault());
        return localizedException.getLocalizedMessage();
    }
}
package util;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {

    @Getter
    @Setter
    private static Locale messagesLocale;

    public static String getMessageForLocale(String messageKey, Locale locale) {
        return ResourceBundle.getBundle("messages", locale)
                .getString(messageKey);
    }

    public static String getLocalizedMessageWithKey(String key){
        LocalizedException localizedException = new LocalizedException(key,
                getMessagesLocale()!=null ? getMessagesLocale() : Locale.getDefault());
        return localizedException.getLocalizedMessage();
    }


}
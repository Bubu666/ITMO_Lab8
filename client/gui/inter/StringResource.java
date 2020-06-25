package client.gui.inter;

import java.util.Locale;
import java.util.ResourceBundle;

public class StringResource {
    private static final Locale russianLocale = new Locale("ru", "RU");
    private static final Locale slovenianLocale = new Locale("sl", "Sl");
    private static final Locale polishLocale = new Locale("pl", "Pl");
    private static final Locale spanishLocale = new Locale("es", "GT");
    private static final Locale englishLocale = new Locale("en", "US");

    private static final ResourceBundle ruStrings = ResourceBundle.getBundle("strings", russianLocale, new UTF8Control());
    private static final ResourceBundle slStrings = ResourceBundle.getBundle("strings", slovenianLocale, new UTF8Control());
    private static final ResourceBundle plStrings = ResourceBundle.getBundle("strings", polishLocale, new UTF8Control());
    private static final ResourceBundle esStrings = ResourceBundle.getBundle("strings", spanishLocale, new UTF8Control());
    private static final ResourceBundle enStrings = ResourceBundle.getBundle("strings", englishLocale, new UTF8Control());

    private static ResourceBundle currentBundle = ruStrings;

    private static Language currentLanguage = Language.get(Language.Type.EN);

    public static ResourceBundle getBundle() {
        return currentBundle;
    }

    public static ResourceBundle updateBundle(Language language) {
        switch (language.type) {
            case ES:
                currentBundle = esStrings;
                break;
            case PL:
                currentBundle = plStrings;
                break;
            case RU:
                currentBundle = ruStrings;
                break;
            case SL:
                currentBundle = slStrings;
                break;
            case EN:
                currentBundle = enStrings;
                break;
        }
        currentLanguage = language;
        return currentBundle;
    }

    public static Language getLanguage() {
        return currentLanguage;
    }

    public static Locale getLocale() {
        return getLocale(currentLanguage);
    }

    public static Locale getLocale(Language language) {
        switch (language.type) {
            case ES:
                return spanishLocale;
            case PL:
                return polishLocale;
            case RU:
                return russianLocale;
            case SL:
                return slovenianLocale;
            default:
                return englishLocale;
        }
    }
}
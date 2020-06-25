package client.gui.inter;

import javafx.collections.FXCollections;

import java.util.ResourceBundle;
import java.util.Set;

public class Language {
    public final Type type;
    private String name;

    private static final Language ru;
    private static final Language en;
    private static final Language es;
    private static final Language pl;
    private static final Language sl;

    static {
        ru = new Language(Type.RU);
        sl = new Language(Type.SL);
        es = new Language(Type.ES);
        pl = new Language(Type.PL);
        en = new Language(Type.EN);
        update();
    }

    public Language(Type type) {
        this.type = type;
    }

    public static void update() {
        ResourceBundle res = StringResource.getBundle();
        ru.name = res.getString("c_ru");
        sl.name = res.getString("c_sl");
        es.name = res.getString("c_es");
        pl.name = res.getString("c_pl");
        en.name = res.getString("c_en");
    }

    public static Set<Language> getLanguages() {
        return FXCollections.observableSet(ru, sl, es, pl, en);
    }

    @Override
    public String toString() {
        return name;
    }

    public static Language get(Type type) {
        switch (type) {
            case ES:
                return es;
            case PL:
                return pl;
            case RU:
                return ru;
            case SL:
                return sl;
            default:
                return en;
        }
    }

    public enum Type {
        RU, ES, EN, PL, SL
    }
}
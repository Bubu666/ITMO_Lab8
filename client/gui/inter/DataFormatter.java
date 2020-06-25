package client.gui.inter;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class DataFormatter {
    private NumberFormat numberFormat;
    private DateFormat dateFormat;
    private DateFormat timeFormat;

    public DataFormatter(Locale locale) {
        numberFormat = NumberFormat.getNumberInstance(locale);
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
    }

    public String stringOf(Date date) {
        return dateFormat.format(date) + " " + timeFormat.format(date);
    }

    public String stringOf(double number) {
        return numberFormat.format(number);
    }

    public String stringOf(int number) {
        return numberFormat.format(number);
    }

    public double doubleOf(String number) throws ParseException {
        return numberFormat.parse(number).doubleValue();
    }

    public int intOf(String number) throws ParseException {
        return numberFormat.parse(number).intValue();
    }
}
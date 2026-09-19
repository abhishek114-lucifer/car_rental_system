package util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {

    public static String inr(BigDecimal amount) {
        if (amount == null) {
            return "₹0";
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(
                new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(2);

        return "₹" + formatter.format(amount);
    }
}

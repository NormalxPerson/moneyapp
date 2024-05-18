package appUtilities;

public class MoneyMathUtils {
    private MoneyMathUtils() {
    }


    public static String formatForDisplay(long cents) {
        return String.format("$%.2f", cents / 100.0);
    }

    public static long dollarsToCents(double dollars) {
        return Math.round(dollars * 100);
    }

    public static long roundToNearestCent(double value) {
        return Math.round(value * 100);
    }

    public static long parseStringDollarsToCents(String dollarAmount) throws NumberFormatException {
        dollarAmount = dollarAmount.replace("$", "").replace(",", "");
        double dollars = Double.parseDouble(dollarAmount);
        return roundToNearestCent(dollars);
    }
}

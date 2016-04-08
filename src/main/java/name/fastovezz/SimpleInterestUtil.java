package name.fastovezz;

import java.math.BigDecimal;
import java.util.Currency;

import static name.fastovezz.Money.money;

/**
 * Created by Maksym Fastovets.
 */
public final class SimpleInterestUtil {
    public static final String INTEREST_RATE_PA_EXCEPTION_MESSAGE = "Interest rate p.a. cannot be negative number.";
    public static final String INITIAL_INVESTMENT_AMOUNT_EXCEPTION_MESSAGE
            = "Initial investment amount cannot be negative number.";
    public static final String INVESTMENT_PERIOD_EXCEPTION_MESSAGE = "Investment period cannot be negative number.";

    public static Money simpleInterest(Money initialInvestmentAmount, double period, double interestRatePA) {
        final Currency currency = initialInvestmentAmount.getCurrency();

        checkArgument(initialInvestmentAmount.gte(money(currency, BigDecimal.ZERO)),
                INITIAL_INVESTMENT_AMOUNT_EXCEPTION_MESSAGE);

        checkArgument(period >= 0, INVESTMENT_PERIOD_EXCEPTION_MESSAGE);
        checkArgument(interestRatePA >= 0, INTEREST_RATE_PA_EXCEPTION_MESSAGE);

        return initialInvestmentAmount.multiply(period * interestRatePA);
    }

    public static Money gainedInvestmentAmount(Money initialInvestmentAmount, double period, double interestRatePA) {
        return initialInvestmentAmount.add(simpleInterest(initialInvestmentAmount, period, interestRatePA));
    }

    public static Money gainedInvestmentAmount(Money initialInvestmentAmount, double period) {

        return gainedInvestmentAmount(initialInvestmentAmount, period, interestRatePA(initialInvestmentAmount));
    }

    public static double interestRatePA(Money initialInvestmentAmount) {
        final Currency currency = initialInvestmentAmount.getCurrency();

        checkArgument(initialInvestmentAmount.gte(money(currency, BigDecimal.ZERO)),
                INITIAL_INVESTMENT_AMOUNT_EXCEPTION_MESSAGE);

        double interestRatePA;
        if (initialInvestmentAmount.lt(money(currency, 1000))) {
            interestRatePA = 0.01;
        } else if (initialInvestmentAmount.lt(money(currency, 10000))) {
            interestRatePA = 0.015;
        } else if (initialInvestmentAmount.lt(money(currency, 100000))) {
            interestRatePA = 0.025;
        } else {
            interestRatePA = 0.05;
        }

        return interestRatePA;
    }

    public static void checkArgument(boolean expression, String message) {
        if(!expression) {
            throw new IllegalArgumentException(message);
        }
    }

}

package name.fastovezz;

import org.junit.Test;

import java.util.Currency;
import java.util.Locale;

import static junit.framework.Assert.*;
import static name.fastovezz.Money.money;
import static name.fastovezz.SimpleInterestUtil.*;

/**
 * Created by Maksym Fastovets.
 */
public class SimpleInterestUtilTest {

    private static final Money[] INVESTMENT_AMOUNTS;
    private static final double[] INTEREST_RATES_PA;
    private static final Money[] GAINED_INVESTMENT_AMOUNTS;

    static {
        Locale.setDefault(Locale.UK);

        INVESTMENT_AMOUNTS = new Money[] {money(-1), money(), money(500), money(1000), money(8000),
                money(10000), money(11000), money(100000), money(100001), money(Double.MAX_VALUE)};

        INTEREST_RATES_PA = new double[] {0, 0.01, 0.01, 0.015, 0.015, 0.025, 0.025, 0.05, 0.05, 0.05};

        GAINED_INVESTMENT_AMOUNTS = new Money[] {money(), money(), money(500).multiply(1.01),
                money(1000).multiply(1.015), money(8000).multiply(1.015), money(10000).multiply(1.025),
                money(11000).multiply(1.025), money(100000).multiply(1.05), money(100001).multiply(1.05),
                money(Double.MAX_VALUE).multiply(1.05)};
    }
    
    @Test
    public void testInterestRatePA() {
        StringBuffer assertionFailedMessages = new StringBuffer();

        for(int i = 0; i < INVESTMENT_AMOUNTS.length; i++) {
            try {
                double interestRatePA = interestRatePA(INVESTMENT_AMOUNTS[i]);

                if(interestRatePA != INTEREST_RATES_PA[i]) {
                    String assertionFailedMessage = interestRatePA
                            + " is not correct interest rate p.a. for initial investment amount of "
                            + INVESTMENT_AMOUNTS[i] + ". Expected " + INTEREST_RATES_PA[i];

                    assertionFailedMessages.append(assertionFailedMessage);
                }

            } catch (IllegalArgumentException e) {
                assertEquals(e.getMessage(), INITIAL_INVESTMENT_AMOUNT_EXCEPTION_MESSAGE);
            }
        }

        assertEquals(assertionFailedMessages.toString(), 0, assertionFailedMessages.length());
    }

    @Test
    public void testInvestmentAmountNegativeNumber() {
        double period = 0;
        Money investmentAmount = money(-1000.001);
        double interestRatePA = 0;
        try {

            gainedInvestmentAmount(investmentAmount, period, interestRatePA);
            fail("Investment amount cannot be negative number.");

        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), INITIAL_INVESTMENT_AMOUNT_EXCEPTION_MESSAGE);
        }

    }

    @Test
    public void testPeriodNegativeNumber() {
        double period = -11;
        Money investmentAmount = money();
        double interestRatePA = 0;
        try {

            gainedInvestmentAmount(investmentAmount, period, interestRatePA);
            fail("Investment period cannot be negative number.");

        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), INVESTMENT_PERIOD_EXCEPTION_MESSAGE);
        }
    }

    @Test
    public void testInterestRatePANegativeNumber() {
        double period = 0;
        Money investmentAmount = money();
        double interestRatePA = -0.01;
        try {

            gainedInvestmentAmount(investmentAmount, period, interestRatePA);
            fail("Interest rate p.a. cannot be negative number.");

        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), INTEREST_RATE_PA_EXCEPTION_MESSAGE);
        }
    }

    @Test
    public void testGainedInvestment() {
        int period = 1; // one year
        StringBuffer assertionFailedMessages = new StringBuffer();

        for(int i = 0; i < INVESTMENT_AMOUNTS.length; i++) {
            try {
                Money gainedInvestmentAmount = gainedInvestmentAmount(INVESTMENT_AMOUNTS[i], period);

                if(!gainedInvestmentAmount.eq(GAINED_INVESTMENT_AMOUNTS[i])) {
                    String assertionFailedMessage = "\n" + gainedInvestmentAmount
                            + " is not correct gained investment amount for initial investment amount of "
                            + INVESTMENT_AMOUNTS[i] + ". Expected " + GAINED_INVESTMENT_AMOUNTS[i];

                    assertionFailedMessages.append(assertionFailedMessage);
                }


            } catch (IllegalArgumentException e) {
                assertEquals(e.getMessage(), INITIAL_INVESTMENT_AMOUNT_EXCEPTION_MESSAGE);
            }
        }

        assertEquals(assertionFailedMessages.toString(), 0, assertionFailedMessages.length());
    }

    @Test
    public void testMoneyComparisonOperations() {
        assertTrue(money(12).lt(money(12.000001)));
        assertTrue(money(12).lte(money(12.000001)));
        assertTrue(money(12).lte(money(12)));

        assertTrue(money(12).eq(money(12)));
        assertTrue(money(12).equals(money(12)));
        assertFalse(money(12).eq(money(12.000001)));
        assertFalse(money(12).equals(money(12.000001)));

        // my implementation of money is sensitive
        // only to six decimal places
        assertTrue(money(12).eq(money(12.0000001)));
        assertTrue(money(12).equals(money(12.0000001)));

        assertTrue(money(12).lt(money(12.0000006)));


        assertFalse(money(Currency.getInstance(Locale.US), 12).equals(money(Currency.getInstance(Locale.UK), 12)));

        try {
            money(Currency.getInstance(Locale.US), 12).eq(money(Currency.getInstance(Locale.UK), 12));
            fail("Money with different currencies cannot be compared!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Money.CANNOT_COMPARE_DIFFERENT_CURRENCIES);
        }

        try {
            money(12).eq(null);
            fail("Money cannot be compared to null!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Money.CANNOT_COMPARE_MONEY_TO_NULL);
        }

        assertTrue(money(12).gte(money(12)));
        assertTrue(money(12.000001).gte(money(12)));
        assertTrue(money(12.000001).gt(money(12)));
    }

    @Test
    public void testCreateMoney() {
        try {
            money(null, null);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Money.AMOUNT_CANNOT_BE_NULL);
        }

        try {
            money(null, 0);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Money.CURRENCY_CANNOT_BE_NULL);
        }
    }

    @Test
    public void testMoneyArithmeticOperations() {
        assertTrue(money(2.01).add(money(3.55)).eq(money(5.56)));
        assertTrue(money(2.01).add(money(-3.55)).eq(money(-1.54)));

        assertTrue(money(456.456128).subtract(money(13.528)).eq(money(442.928128)));
        assertTrue(money(-456.456128).subtract(money(13.528)).eq(money(-469.984128)));

        assertTrue(money(5652.45682).divide(452.84).eq(money(12.482238)));
        assertTrue(money(5652.45682).divide(-452.84).eq(money(-12.482238)));
        assertTrue(money(5652.45682).divide(0.84).eq(money(6729.115262)));
        assertTrue(money(5652.45682).divide(3).eq(money(1884.152273)));

        assertTrue(money(54.785).multiply(0.012).eq(money(0.65742)));
        assertTrue(money(54.785).multiply(10).eq(money(547.85)));

        try {
            money(Currency.getInstance(Locale.US), 12).add(money(Currency.getInstance(Locale.UK), 12));
            fail("Money with different currencies cannot be added!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Money.CANNOT_COMPARE_DIFFERENT_CURRENCIES);
        }

        try {
            money(12).add(null);
            fail("Money cannot be compared to null!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Money.MONEY_CANNOT_BE_NULL);
        }

        try {
            money(Currency.getInstance(Locale.US), 12).subtract(money(Currency.getInstance(Locale.UK), 12));
            fail("Money with different currencies cannot be subtracted!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Money.CANNOT_COMPARE_DIFFERENT_CURRENCIES);
        }

        try {
            money(12).subtract(null);
            fail("Money cannot be compared to null!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Money.MONEY_CANNOT_BE_NULL);
        }


//
//        // Invalid: cannot put underscores
//// adjacent to a decimal point
//        float pi1 = 3_.1415F;
//// Invalid: cannot put underscores
//// adjacent to a decimal point
//        float pi2 = 3._1415F;
//// Invalid: cannot put underscores
//// prior to an L suffix
//        long socialSecurityNumber1 = 999_99_9999_L;
//
//// OK (decimal literal)
//        int x1 = 5_2;
//// Invalid: cannot put underscores
//// At the end of a literal
//        int x2 = 52_;
//// OK (decimal literal)
//        int x3 = 5_______2;
//
//// Invalid: cannot put underscores
//// in the 0x radix prefix
//        int x4 = 0_x52;
//// Invalid: cannot put underscores
//// at the beginning of a number
//        int x5 = 0x_52;
//// OK (hexadecimal literal)
//        int x6 = 0x5_2;
//// Invalid: cannot put underscores
//// at the end of a number
//        int x7 = 0x52_;
    }

}

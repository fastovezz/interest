package name.fastovezz;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

import static name.fastovezz.SimpleInterestUtil.checkArgument;

/**
 * Created by Maksym Fastovets.
 */
public final class Money implements Comparable<Money> {
    public static final int DEFAULT_ROUNDING;
    public static final RoundingMode DEFAULT_ROUNDING_MODE;
    public static final int DEFAULT_NUMBER_OF_DECIMAL_PLACES;

    public static final Currency DEFAULT_CURRENCY;
    public static final Money DEFAULT_LOCALE_ZERO_MONEY;

    public static final String CURRENCY_CANNOT_BE_NULL = "Currency cannot be null.";
    public static final String CANNOT_COMPARE_DIFFERENT_CURRENCIES = "Cannot compare money of different currencies.";
    public static final String CANNOT_COMPARE_MONEY_TO_NULL = "Cannot compare money to null.";
    public static final String MONEY_CANNOT_BE_NULL = "Money cannot be null.";
    public static final String AMOUNT_CANNOT_BE_NULL = "Amount cannot be null.";

    static {
        // HALF_EVEN rounding behaviour (also known as banking rounding)
        // is suggested for use when treating BigDecimal as money amount.
        // Note that this is the rounding mode that minimizes cumulative
        // error when applied repeatedly over a sequence of calculations.
        DEFAULT_ROUNDING = BigDecimal.ROUND_HALF_EVEN;
        DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;

        // FIXME: my implementation of money is sensitive only to six decimal places
        DEFAULT_NUMBER_OF_DECIMAL_PLACES = 6;

        DEFAULT_CURRENCY = Currency.getInstance(Locale.getDefault());
        DEFAULT_LOCALE_ZERO_MONEY = new Money();
    }

    private final Currency currency;
    private final BigDecimal amount;

    public Money(Currency currency, BigDecimal amount) {
        checkArgument(amount != null, AMOUNT_CANNOT_BE_NULL);
        checkArgument(currency != null, CURRENCY_CANNOT_BE_NULL);

        this.currency = currency;
        this.amount = amount.setScale(DEFAULT_NUMBER_OF_DECIMAL_PLACES, DEFAULT_ROUNDING_MODE);
    }

    public Money(Currency currency, double amount) {
        this(currency, BigDecimal.valueOf(amount));
    }

    public Money(BigDecimal amount) {
        this(DEFAULT_CURRENCY, amount);
    }

    public Money(double amount) {
        this(BigDecimal.valueOf(amount));
    }

    public Money() {
        this(BigDecimal.ZERO);
    }

    public static Money money(Currency currency, BigDecimal amount) {
        return new Money(currency, amount);
    }

    public static Money money(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money money(Currency currency, double amount) {
        return new Money(currency, amount);
    }

    public static Money money(double amount) {
        return new Money(amount);
    }

    public static Money money() {
        return DEFAULT_LOCALE_ZERO_MONEY;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public int compareTo(Money that) {
        checkArgument(that != null, CANNOT_COMPARE_MONEY_TO_NULL);
        checkCurrency(that);

        return this.amount.compareTo(that.amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass()) {
            return false;
        }

        Money that = (Money) obj;

        if (this == that) {
            return true;
        }

        if (this.currency == null || this.amount == null || that.currency == null || that.amount == null) {
            return false;
        }

        return this.currency.equals(that.currency) && this.amount.equals(that.amount);

    }

    public boolean lte(Money that) {
        return this.compareTo(that) <= 0;
    }

    public boolean lt(Money that) {
        return this.compareTo(that) < 0;
    }

    public boolean eq(Money that) {
        return this.compareTo(that) == 0;
    }

    public boolean gt(Money that) {
        return this.compareTo(that) > 0;
    }

    public boolean gte(Money that) {
        return this.compareTo(that) >= 0;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;

        result = prime * result + currency.hashCode();
        result = prime * result + amount.hashCode();

        return result;
    }

    public Money add(Money that) {
        checkArgument(that != null, MONEY_CANNOT_BE_NULL);
        checkCurrency(that);

        return new Money(this.currency, this.amount.add(that.amount));
    }

    private void checkCurrency(Money that) {
        checkArgument(that.currency.equals(this.currency), CANNOT_COMPARE_DIFFERENT_CURRENCIES);
    }

    public Money subtract(Money that) {
        checkArgument(that != null, MONEY_CANNOT_BE_NULL);
        checkCurrency(that);

        return new Money(this.currency, this.amount.subtract(that.amount));
    }

    public Money divide(long divisor) {
        return new Money(this.currency, this.amount.divide(BigDecimal.valueOf(divisor), DEFAULT_ROUNDING));
    }

    public Money divide(double divisor) {
        return new Money(this.currency, this.amount.divide(BigDecimal.valueOf(divisor), DEFAULT_ROUNDING));
    }

    public Money multiply(long multiplicand) {
        return new Money(this.currency, this.amount.multiply(BigDecimal.valueOf(multiplicand)));
    }

    public Money multiply(double multiplicand) {
        return new Money(this.currency, this.amount.multiply(BigDecimal.valueOf(multiplicand)));
    }

    @Override
    public String toString() {
        return currency.getSymbol() + " " + amount.setScale(currency.getDefaultFractionDigits(), DEFAULT_ROUNDING_MODE);
    }

}

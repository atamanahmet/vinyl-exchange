package com.vinyl.VinylExchange.domain.money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyCalculator {
    private MoneyCalculator() {

    }

    public static long discounted(long priceKurus, int discountBP) {

        if (discountBP <= 0)
            return priceKurus;

        BigDecimal price = BigDecimal.valueOf(priceKurus);
        BigDecimal discount = BigDecimal.valueOf(discountBP)
                .divide(BigDecimal.valueOf(10_000), 4, RoundingMode.UNNECESSARY);

        BigDecimal result = price.multiply(BigDecimal.ONE.subtract(discount));

        return result.setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}

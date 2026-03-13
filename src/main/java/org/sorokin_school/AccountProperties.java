package org.sorokin_school;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountProperties {
    @Value("${account.default-amount:500}")
    private BigDecimal defaultAmount;

    public BigDecimal getDefaultAmount() {
        return defaultAmount;
    }

    @Value("${account.transfer-commission:0.02}")
    private double transferCommission;

    public double getTransferCommission() {
        return transferCommission;
    }
}

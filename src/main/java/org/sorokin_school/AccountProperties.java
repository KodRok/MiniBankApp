package org.sorokin_school;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountProperties {
    @Value("${account.default-amount:500}")
    private int defaultAmount;

    public int getDefaultAmount() {
        return defaultAmount;
    }

    @Value("${account.transfer-commission:0.02}")
    private double transferCommission;

    public double getTransferCommission() {
        return transferCommission;
    }
}

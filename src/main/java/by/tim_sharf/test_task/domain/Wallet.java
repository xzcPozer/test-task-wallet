package by.tim_sharf.test_task.domain;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID walletId;
    private BigDecimal balance;

    public Wallet(UUID walletId, BigDecimal balance) {
        this.walletId = walletId;
        this.balance = balance;
    }

    public Wallet() {
    }

    public UUID getWalletId() {
        return this.walletId;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

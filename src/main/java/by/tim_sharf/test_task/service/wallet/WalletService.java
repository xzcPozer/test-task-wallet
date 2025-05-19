package by.tim_sharf.test_task.service.wallet;

import by.tim_sharf.test_task.domain.Wallet;
import by.tim_sharf.test_task.dto.wallet.WalletRequest;
import by.tim_sharf.test_task.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final WalletRepository repository;

//    public WalletService(WalletRepository repository) {
//        this.repository = repository;
//    }

    public BigDecimal getBalanceById(UUID id) {
        return repository.findById(id)
                .map(Wallet::getBalance)
                .orElseThrow(() -> new EntityNotFoundException("wallet with id: " + id + " not found"));
    }

    public UUID depositMoney(WalletRequest request) {
        return repository.findById(request.walletId())
                .map(w -> {
                    w.setBalance(w.getBalance().add(request.amount()));
                    return repository.save(w);
                })
                .map(Wallet::getWalletId)
                .orElseThrow(() -> new EntityNotFoundException("wallet with id: " + request.walletId() + " not found"));
    }

    public UUID withdrawMoney(WalletRequest request) {
        Wallet wallet = repository.findById(request.walletId())
                .orElseThrow(() -> new EntityNotFoundException("wallet with id: " + request.walletId() + " not found"));

        BigDecimal newAmount = wallet.getBalance().subtract(request.amount());
        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("insufficient funds for wallet with id: " + request.walletId());
        }

        wallet.setBalance(newAmount);
        return repository.save(wallet).getWalletId();
    }
}

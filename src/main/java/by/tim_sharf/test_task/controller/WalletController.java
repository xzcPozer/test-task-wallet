package by.tim_sharf.test_task.controller;

import by.tim_sharf.test_task.dto.wallet.WalletRequest;
import by.tim_sharf.test_task.enums.OperationType;
import by.tim_sharf.test_task.service.wallet.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.OperationNotSupportedException;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}")
public class WalletController {

    private final WalletService service;

//    public WalletController(WalletService service) {
//        this.service = service;
//    }

    @PostMapping("wallet")
    public ResponseEntity<UUID> makeOperation(
            @RequestBody @Valid WalletRequest request
    ) throws OperationNotSupportedException {
        if(request.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalStateException("operations cannot be performed on a number less than or equal to zero");

        if (request.operationType().equals(OperationType.DEPOSIT))
            return ResponseEntity.ok(service.depositMoney(request));
        else if (request.operationType().equals(OperationType.WITHDRAW))
            return ResponseEntity.ok(service.withdrawMoney(request));
        else
            throw new OperationNotSupportedException("operation: " + request.operationType() + " not supported");
    }

    @GetMapping("wallets/{wallet_uuid}")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable("wallet_uuid") UUID id
    ) {
        return ResponseEntity.ok(service.getBalanceById(id));
    }
}

package by.tim_sharf.test_task.dto.wallet;


import by.tim_sharf.test_task.enums.OperationType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletRequest(

        @NotNull(message = "wallet id is mandatory")
        UUID walletId,

        @NotNull(message = "operation type is mandatory")
        OperationType operationType,

        @NotNull(message = "amount is mandatory")
        BigDecimal amount
) {
}

package by.tim_sharf.test_task.controller;

import by.tim_sharf.test_task.domain.Wallet;
import by.tim_sharf.test_task.dto.wallet.WalletRequest;
import by.tim_sharf.test_task.enums.OperationType;
import by.tim_sharf.test_task.repository.WalletRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class WalletControllerTest {

    private final static String apiPrefix = "/api/v1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository repository;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("bank_db")
            .withUsername("test")
            .withPassword("testpassword");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void depositMoney_Success() throws Exception {
        UUID walletId = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");

        WalletRequest request = new WalletRequest(walletId, OperationType.DEPOSIT, new BigDecimal("100.00"));

        mockMvc.perform(post(apiPrefix + "/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(walletId.toString()));

        Wallet updatedWallet = repository.findById(walletId)
                .orElseThrow();

        assert updatedWallet.getBalance().equals(new BigDecimal("10200.00"));
    }

    @Test
    void withdrawMoney_Success() throws Exception {
        UUID walletId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

        WalletRequest request = new WalletRequest(walletId, OperationType.WITHDRAW, new BigDecimal("693.23"));

        mockMvc.perform(post(apiPrefix + "/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(walletId.toString()));

        Wallet updatedWallet = repository.findById(walletId)
                .orElseThrow();

        assert updatedWallet.getBalance().equals(new BigDecimal("11607.07"));
    }

    @Test
    void invalidAmount_ReturnBadRequest() throws Exception {
        UUID walletId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        WalletRequest request = new WalletRequest(walletId, OperationType.DEPOSIT, new BigDecimal("-10.00"));

        mockMvc.perform(post(apiPrefix + "/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("operations cannot be performed on a number less than or equal to zero"));
    }

    @Test
    void depositMoney_recordNotFound_ReturnNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();
        WalletRequest request = new WalletRequest(walletId, OperationType.DEPOSIT, new BigDecimal("100000.00"));

        mockMvc.perform(post(apiPrefix + "/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("wallet with id: " + request.walletId() + " not found"));
    }

    @Test
    void withdrawMoney_recordNotFound_ReturnNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();
        WalletRequest request = new WalletRequest(walletId, OperationType.WITHDRAW, new BigDecimal("100000.00"));

        mockMvc.perform(post(apiPrefix + "/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("wallet with id: " + request.walletId() + " not found"));
    }

    @Test
    void withdrawMoney_InsufficientFunds_ReturnBadRequest() throws Exception {
        UUID walletId = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        WalletRequest request = new WalletRequest(walletId, OperationType.WITHDRAW, new BigDecimal("40000.01"));

        mockMvc.perform(post(apiPrefix + "/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("insufficient funds for wallet with id: " + request.walletId()));
    }

    @Test
    void getBalanceById_Success() throws Exception {
        UUID walletId = UUID.fromString("550e8400-e29b-41d4-a716-446655440008");

        mockMvc.perform(get(apiPrefix + "/wallets/{wallet_uuid}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("1000000.52"));
    }

    @Test
    void getBalanceById_recordNotFound_ReturnNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();

        mockMvc.perform(get(apiPrefix + "/wallets/{wallet_uuid}", walletId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("wallet with id: " + walletId + " not found"));
    }

}
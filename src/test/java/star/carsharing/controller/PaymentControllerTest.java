package star.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static star.carsharing.util.AuthTestUtil.authentication;
import static star.carsharing.util.AuthTestUtil.roleCustomer;
import static star.carsharing.util.PaymentTestUtil.mapSessionToPaymentResponseDto;
import static star.carsharing.util.PaymentTestUtil.mockSession;
import static star.carsharing.util.PaymentTestUtil.paymentDto;
import static star.carsharing.util.PaymentTestUtil.paymentRequestDto;
import static star.carsharing.util.PaymentTestUtil.sessionCreateParams;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import star.carsharing.dto.payment.PaymentDto;
import star.carsharing.dto.payment.PaymentRequestDto;
import star.carsharing.dto.payment.PaymentResponseDto;
import star.carsharing.model.Payment;
import star.carsharing.service.StripePaymentService;
import star.carsharing.telegram.NotificationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class PaymentControllerTest {
    protected MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private StripePaymentService stripePaymentService;
    @MockBean
    private NotificationService notificationService;

    @BeforeAll
    void beforeAll() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Verify method createSession with correct data. Payment type PAYMENT")
    @Sql(scripts = {"classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createSession_CorrectDataPaymentTypePayment_ReturnPaymentResponseDto()
            throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        PaymentRequestDto paymentRequestDto = paymentRequestDto(3L, Payment.Type.PAYMENT);
        String jsonRequest = objectMapper.writeValueAsString(paymentRequestDto);
        SessionCreateParams sessionCreateParams = sessionCreateParams(BigDecimal.valueOf(246.24));
        Session session = mockSession("cs_test_111", "https://checkout.stripe.com/c/pay/payment");

        when(stripePaymentService.createSessionParams(any()))
                .thenReturn(sessionCreateParams);
        when(stripePaymentService.makeSession(sessionCreateParams))
                .thenReturn(session);
        MvcResult result = mockMvc.perform(
                        post("/payments/create")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        PaymentResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseDto.class);

        PaymentResponseDto expected = mapSessionToPaymentResponseDto(session);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify method createSession with correct data. Payment type FINE")
    @Sql(scripts = {"classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createSession_CorrectDataPaymentTypeFine_ReturnPaymentResponseDto()
            throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        PaymentRequestDto paymentRequestDto = paymentRequestDto(3L, Payment.Type.FINE);
        String jsonRequest = objectMapper.writeValueAsString(paymentRequestDto);
        SessionCreateParams sessionCreateParams = sessionCreateParams(BigDecimal.valueOf(430.920));
        Session session = mockSession("cs_test_22222", "https://checkout.stripe.com/c/pay/fine");

        when(stripePaymentService.createSessionParams(any()))
                .thenReturn(sessionCreateParams);
        when(stripePaymentService.makeSession(sessionCreateParams))
                .thenReturn(session);
        MvcResult result = mockMvc.perform(
                        post("/payments/create")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        PaymentResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseDto.class);

        PaymentResponseDto expected = mapSessionToPaymentResponseDto(session);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method createSession with incorrect data.
            Invalid PaymentRequestDto
            """)
    @Sql(scripts = {"classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createSession_IncorrectDataInvalidDto_ReturnStatus() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        PaymentRequestDto paymentRequestDto = paymentRequestDto(-3L, Payment.Type.PAYMENT);
        String jsonRequest = objectMapper.writeValueAsString(paymentRequestDto);
        mockMvc.perform(
                        post("/payments/create")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Verify method createSession with incorrect data.
            Rental is not exist
            """)
    @Sql(scripts = {"classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createSession_IncorrectDataRentalNotExist_ReturnStatus() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        PaymentRequestDto paymentRequestDto = paymentRequestDto(387L, Payment.Type.PAYMENT);
        String jsonRequest = objectMapper.writeValueAsString(paymentRequestDto);
        mockMvc.perform(
                        post("/payments/create")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify method getById with correct data")
    @WithMockUser(username = "manager@email.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/payment/add-payments-to-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/payment/delete-payments-from-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getById_CorrectData_ReturnPaymentDto() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/payments/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PaymentDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentDto.class);

        PaymentDto expected = paymentDto();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method getById with correct data.
            Payment is not exist
            """)
    @WithMockUser(username = "manager@email.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/payment/add-payments-to-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/payment/delete-payments-from-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getById_IncorrectDataPaymentNotExist_ReturnStatus() throws Exception {
        mockMvc.perform(
                        get("/payments/769")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify method getPayments with correct data")
    @Sql(scripts = {"classpath:db/payment/add-payments-to-payments-table.sql",
            "classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/payment/delete-payments-from-payments-table.sql",
            "classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getPayments_CorrectData_ReturnPagesPaymentDto() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        MvcResult result = mockMvc.perform(
                        get("/payments")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<PaymentDto> actual = objectMapper.readValue(root.get("content").toString(),
                new TypeReference<>() {
                });

        PaymentDto expected = paymentDto();
        assertEquals(2, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("Verify method paymentSuccess with correct data")
    @WithMockUser(username = "manager@email.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/payment/add-payments-to-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/payment/delete-payments-from-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void paymentSuccess_CorrectData_ReturnStatus() throws Exception {
        when(stripePaymentService.isPaymentSessionPaid(any())).thenReturn(true);
        doNothing().when(notificationService).sentSuccessesPayment(any());
        mockMvc.perform(
                        get("/payments/success")
                                .param("session_id", "cs_test_111")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("""
            Verify method paymentSuccess with incorrect data.
            Session id is not exist
            """)
    @WithMockUser(username = "manager@email.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/payment/add-payments-to-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/payment/delete-payments-from-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void paymentSuccess_IncorrectData_ReturnStatus() throws Exception {
        when(stripePaymentService.isPaymentSessionPaid(any())).thenReturn(true);
        doNothing().when(notificationService).sentSuccessesPayment(any());
        mockMvc.perform(
                        get("/payments/success")
                                .param("session_id", "cs_test_incorrect")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify method paymentCancel with correct data")
    @WithMockUser(username = "manager@email.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/payment/delete-payments-from-payments-table.sql",
            "classpath:db/payment/add-payments-to-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/payment/delete-payments-from-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void paymentCancel_CorrectData_ReturnStatus() throws Exception {
        mockMvc.perform(
                        get("/payments/cancel")
                                .param("session_id", "cs_test_222")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("""
            Verify method paymentCancel with incorrect data.
            Payment was cancelled.
            """)
    @WithMockUser(username = "manager@email.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/payment/delete-payments-from-payments-table.sql",
            "classpath:db/payment/add-payments-to-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/payment/delete-payments-from-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void paymentCancel_IncorrectDataPaymentCancel_ReturnStatus() throws Exception {
        doNothing().when(notificationService).sentCancelPayment(any());
        mockMvc.perform(
                        get("/payments/cancel")
                                .param("session_id", "cs_test_111")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isPaymentRequired());
    }

    @Test
    @DisplayName("""
            Verify method paymentCancel with incorrect data.
            Payment is not exist
            """)
    @WithMockUser(username = "manager@email.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/payment/delete-payments-from-payments-table.sql",
            "classpath:db/payment/add-payments-to-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/payment/delete-payments-from-payments-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void paymentCancel_IncorrectDataPaymentNotExist_ReturnStatus() throws Exception {
        mockMvc.perform(
                        get("/payments/cancel")
                                .param("session_id", "cs_test_000")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

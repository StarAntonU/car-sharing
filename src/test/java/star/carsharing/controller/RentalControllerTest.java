package star.carsharing.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static star.carsharing.util.AuthTestUtil.authentication;
import static star.carsharing.util.AuthTestUtil.roleCustomer;
import static star.carsharing.util.AuthTestUtil.roleManager;
import static star.carsharing.util.RentalTestUnit.actualReturnTime;
import static star.carsharing.util.RentalTestUnit.createRentalRequestDto;
import static star.carsharing.util.RentalTestUnit.invalidCreateRentalRequestDto;
import static star.carsharing.util.RentalTestUnit.mapCreateRentalRequestDtoToRentalResponseDto;
import static star.carsharing.util.RentalTestUnit.rentalResponseDto;
import static star.carsharing.util.RentalTestUnit.rentalResponseWithActualReturnDateDto;
import static star.carsharing.util.RentalTestUnit.rentalTime;
import static star.carsharing.util.RentalTestUnit.userRentalIsActiveRequestDto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import star.carsharing.dto.rental.CreateRentalRequestDto;
import star.carsharing.dto.rental.RentalResponseDto;
import star.carsharing.dto.rental.RentalResponseWithActualReturnDateDto;
import star.carsharing.dto.rental.UserRentalIsActiveRequestDto;
import star.carsharing.telegram.NotificationService;
import star.carsharing.util.TimeProvider;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class RentalControllerTest {
    protected MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private TimeProvider timeProvider;

    @BeforeAll
    void beforeAll() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Verify method createRental with correct data")
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createRental_CorrectData_ReturnRentalResponseDto() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        CreateRentalRequestDto createRentalDto = createRentalRequestDto(1L);
        String jsonRequest = objectMapper.writeValueAsString(createRentalDto);

        when(timeProvider.now()).thenReturn(rentalTime);
        doNothing().when(notificationService).sentNotificationCreateRental(any());
        MvcResult result = mockMvc.perform(
                        post("/rentals")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        RentalResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), RentalResponseDto.class);

        RentalResponseDto expected = mapCreateRentalRequestDtoToRentalResponseDto(createRentalDto);
        //assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method createRental with incorrect data.
            Invalid CreateRentalRequestDto
            """)
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createRental_IncorrectDataInvalidDto_ReturnStatus() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        CreateRentalRequestDto createRentalDto = invalidCreateRentalRequestDto(1L);
        String jsonRequest = objectMapper.writeValueAsString(createRentalDto);

        doNothing().when(notificationService).sentNotificationCreateRental(any());
        mockMvc.perform(
                        post("/rentals")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Verify method createRental with incorrect data.
            Car is not exist
            """)
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createRental_IncorrectDataCarNotExist_ReturnStatus() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        CreateRentalRequestDto createRentalDto = createRentalRequestDto(41L);
        String jsonRequest = objectMapper.writeValueAsString(createRentalDto);

        doNothing().when(notificationService).sentNotificationCreateRental(any());
        mockMvc.perform(
                        post("/rentals")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify method getRentalById with correct data")
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/user/add-users-to-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getRentalById_CorrectData_ReturnRentalResponseDto() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        MvcResult result = mockMvc.perform(
                        get("/rentals/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        RentalResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), RentalResponseDto.class);

        RentalResponseDto expected = rentalResponseDto(1L, true);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method getRentalById with incorrect data.
            Rental is not exist
            """)
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/user/add-users-to-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getRentalById_IncorrectDataRentalNotExist_ReturnRentalStatus() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        mockMvc.perform(
                        get("/rentals/45")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("""
            Verify method getRentalById with incorrect data.
            User is not exist
            """)
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/user/add-users-to-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getRentalById_IncorrectDataUserNotExist_ReturnRentalStatus() throws Exception {
        Authentication authentication = authentication(23L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        mockMvc.perform(
                        get("/rentals/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Verify method closeRental with correct data")
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/user/add-users-to-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void closeRental_CorrectData_ReturnRentalResponseDto() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        when(timeProvider.now()).thenReturn(actualReturnTime);
        MvcResult result = mockMvc.perform(
                        post("/rentals/1/return")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        RentalResponseWithActualReturnDateDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RentalResponseWithActualReturnDateDto.class);

        RentalResponseWithActualReturnDateDto expected = rentalResponseWithActualReturnDateDto();
        assertTrue(reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("""
            Verify method closeRental with incorrect data.
            Rental is not exist
            """)
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/user/add-users-to-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void closeRental_IncorrectDataRentalNotExist_ReturnStatus() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        mockMvc.perform(
                        post("/rentals/584/return")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("""
            Verify method closeRental with incorrect data.
            User is not exist
            """)
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/user/add-users-to-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void closeRental_IncorrectDataUserNotExist_ReturnStatus() throws Exception {
        Authentication authentication = authentication(224L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        mockMvc.perform(
                        post("/rentals/1/return")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Verify method getUserRentalIsActive with is active true")
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/user/add-users-to-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getUserRentalIsActive_CorrectDataIsActualTrue_ReturnPageRentalResponseDto()
            throws Exception {
        Authentication authentication = authentication(1L, roleManager());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        UserRentalIsActiveRequestDto request = userRentalIsActiveRequestDto(2L, true);
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(
                        get("/rentals/active")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<RentalResponseDto> actual = objectMapper.readValue(root.get("content").toString(),
                new TypeReference<>() {
                });

        RentalResponseDto expected = rentalResponseDto(1L, true);
        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("Verify method getUserRentalIsActive with is active false")
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/user/add-users-to-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getUserRentalIsActive_CorrectDataIsActualFalse_ReturnPageRentalResponseDto()
            throws Exception {
        Authentication authentication = authentication(1L, roleManager());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        UserRentalIsActiveRequestDto request = userRentalIsActiveRequestDto(2L, false);
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(
                        get("/rentals/active")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<RentalResponseDto> actual = objectMapper.readValue(root.get("content").toString(),
                new TypeReference<>() {
                });

        RentalResponseDto expected = rentalResponseDto(2L, false);
        assertEquals(2, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("""
            Verify method getUserRentalIsActive with incorrect data.
            Invalid UserRentalIsActiveRequestDto
            """)
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql",
            "classpath:db/rental/add-rentals-to-rentals-table.sql",
            "classpath:db/user/add-users-to-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/rental/delete-rentals-from-rentals-table.sql",
            "classpath:db/car/delete-cars-from-cats-table.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getUserRentalIsActive_IncorrectDataInvalidDto_ReturnPageRentalResponseDto()
            throws Exception {
        Authentication authentication = authentication(1L, roleManager());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        UserRentalIsActiveRequestDto request = userRentalIsActiveRequestDto(-2L, false);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        get("/rentals/active")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

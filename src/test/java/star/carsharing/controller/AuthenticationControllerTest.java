package star.carsharing.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static star.carsharing.util.UserTestUtil.invalidUserLoginRequestDto;
import static star.carsharing.util.UserTestUtil.invalidUserRegisterDto;
import static star.carsharing.util.UserTestUtil.mapUserRegisterRequestDtoToUserDto;
import static star.carsharing.util.UserTestUtil.userLoginRequestDto;
import static star.carsharing.util.UserTestUtil.userLoginResponseDto;
import static star.carsharing.util.UserTestUtil.userRegisterDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import star.carsharing.config.TestAuthenticationServiceConfig;
import star.carsharing.dto.user.UserDto;
import star.carsharing.dto.user.UserLoginRequestDto;
import star.carsharing.dto.user.UserLoginResponseDto;
import star.carsharing.dto.user.UserRegisterRequestDto;
import star.carsharing.security.AuthenticationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestAuthenticationServiceConfig.class})
public class AuthenticationControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationService authenticationService;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Verify method register with correct data")
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void register_CorrectData_ReturnUserDto() throws Exception {
        UserRegisterRequestDto userRegisterDto = userRegisterDto();
        String jsonRequest = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult result = mockMvc.perform(
                        post("/auth/registration")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andReturn();
        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        UserDto expected = mapUserRegisterRequestDtoToUserDto(userRegisterDto);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Verify method register with incorrect data. Invalid request dto")
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void register_IncorrectData_ReturnStatus() throws Exception {
        UserRegisterRequestDto userRegisterDto = invalidUserRegisterDto();
        String jsonRequest = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(
                        post("/auth/registration")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verify method login with with correct data")
    public void login_CorrectData_ReturnUserLoginResponseDto() throws Exception {
        UserLoginRequestDto userLoginRequestDto = userLoginRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(userLoginRequestDto);
        UserLoginResponseDto expected = userLoginResponseDto();

        when(authenticationService.authenticate(userLoginRequestDto)).thenReturn(expected);
        MvcResult result = mockMvc.perform(
                post("/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserLoginResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserLoginResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify method login with with incorrect data. Invalid request dto")
    public void login_IncorrectData_ReturnStatus() throws Exception {
        UserLoginRequestDto userLoginRequestDto = invalidUserLoginRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(userLoginRequestDto);

        mockMvc.perform(
                        post("/auth/login")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

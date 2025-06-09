package star.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static star.carsharing.util.AuthTestUtil.authentication;
import static star.carsharing.util.AuthTestUtil.roleCustomer;
import static star.carsharing.util.UserTestUtil.invalidUpdateUserPassRequestDto;
import static star.carsharing.util.UserTestUtil.invalidUpdateUserRequestDto;
import static star.carsharing.util.UserTestUtil.listThreeUsersDto;
import static star.carsharing.util.UserTestUtil.mapUpdateUserRequestDtoToUserDto;
import static star.carsharing.util.UserTestUtil.mapUserToUserDto;
import static star.carsharing.util.UserTestUtil.updateRoleRequestDtoToUserDtoRoleManager;
import static star.carsharing.util.UserTestUtil.updateUserPassRequestDto;
import static star.carsharing.util.UserTestUtil.updateUserRequestDto;
import static star.carsharing.util.UserTestUtil.updateUserRoleRequestDto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import star.carsharing.dto.user.UpdateUserPassRequestDto;
import star.carsharing.dto.user.UpdateUserRequestDto;
import star.carsharing.dto.user.UpdateUserRoleRequestDto;
import star.carsharing.dto.user.UserDto;
import star.carsharing.model.Role;
import star.carsharing.model.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("Verify method getAllUsers with correct data")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/user/delete-users-from-users-table.sql",
            "classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllUsers_CorrectData_ReturnPageAllUserDto() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/users/all")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<UserDto> actual = objectMapper.readValue(root.get("content").toString(),
                new TypeReference<>() {
                });

        List<UserDto> expected = listThreeUsersDto();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify method updateUserRole with correct data")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateUserRole_CorrectData_ReturnUserDto() throws Exception {
        UpdateUserRoleRequestDto updateUserRoleDto = updateUserRoleRequestDto(
                Role.RoleName.MANAGER);
        String jsonRequest = objectMapper.writeValueAsString(updateUserRoleDto);

        MvcResult result = mockMvc.perform(
                        put("/users/2/role")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        UserDto expected = updateRoleRequestDtoToUserDtoRoleManager();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method updateUserRole with incorrect data.
            Role is not exist
            """)
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateUserRole_IncorrectDataInvalidRole_ReturnStatus() throws Exception {
        UpdateUserRoleRequestDto updateUserRoleDto = updateUserRoleRequestDto(null);
        String jsonRequest = objectMapper.writeValueAsString(updateUserRoleDto);

        mockMvc.perform(
                        put("/users/2/role")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Verify method updateUserRole with incorrect data.
            User by id is not exist
            """)
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateUserRole_IncorrectDataUserNotExist_ReturnStatus() throws Exception {
        UpdateUserRoleRequestDto updateUserRoleDto = updateUserRoleRequestDto(
                Role.RoleName.MANAGER);
        String jsonRequest = objectMapper.writeValueAsString(updateUserRoleDto);

        mockMvc.perform(
                        put("/users/2435/role")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify method getUserById with correct data")
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getUserById_CorrectData_ReturnUserDto() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        MvcResult result = mockMvc.perform(
                        get("/users/me")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        User user = (User) authentication.getPrincipal();
        UserDto expected = mapUserToUserDto(user, 2L);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method getUserById with incorrect data.
            User by id not exist
            """)
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getUserById_IncorrectData_ReturnStatus() throws Exception {
        Authentication authentication = authentication(43L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        mockMvc.perform(
                        get("/users/me")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify method updateUser with correct data")
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateUser_CorrectData_ReturnUserDto() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        UpdateUserRequestDto updateDto = updateUserRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(updateDto);

        MvcResult result = mockMvc.perform(
                put("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        UserDto expected = mapUpdateUserRequestDtoToUserDto(updateDto);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method updateUser with incorrect data.
            Invalid UpdateUserRequestDto
            """)
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateUser_IncorrectDataInvalidDto_ReturnUserDto() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        UpdateUserRequestDto updateDto = invalidUpdateUserRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(
                        put("/users/me")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Verify method updateUser with incorrect data.
            User by id not exist
            """)
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateUser_IncorrectDataUserNotExist_ReturnUserDto() throws Exception {
        Authentication authentication = authentication(432L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        UpdateUserRequestDto updateDto = updateUserRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(
                        put("/users/me")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify method updateUserPass with correct data")
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateUserPass_CorrectData_ReturnUserDto() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        UpdateUserPassRequestDto updateUserPassDto = updateUserPassRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(updateUserPassDto);

        mockMvc.perform(
                patch("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("""
            Verify method updateUserPass with incorrect data.
            Invalid UpdateUserPassRequestDto
            """)
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateUserPass_IncorrectDataInvalidDto_ReturnUserDto() throws Exception {
        Authentication authentication = authentication(2L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        UpdateUserPassRequestDto updateUserPassDto = invalidUpdateUserPassRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(updateUserPassDto);

        mockMvc.perform(
                        patch("/users/me")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Verify method updateUserPass with incorrect data.
            User by id not exist
            """)
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-from-users_roles.sql",
            "classpath:db/user/delete-users-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateUserPass_IncorrectDataUserNotExist_ReturnUserDto() throws Exception {
        Authentication authentication = authentication(245L, roleCustomer());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        UpdateUserPassRequestDto updateUserPassDto = updateUserPassRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(updateUserPassDto);

        mockMvc.perform(
                        patch("/users/me")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

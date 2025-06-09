package star.carsharing.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static star.carsharing.util.CarTestUtil.carDto;
import static star.carsharing.util.CarTestUtil.createCarDto;
import static star.carsharing.util.CarTestUtil.invalidCreateCarDto;
import static star.carsharing.util.CarTestUtil.listThreeCarsDto;
import static star.carsharing.util.CarTestUtil.listTwoCarsDto;
import static star.carsharing.util.CarTestUtil.mapCarToCarDto;
import static star.carsharing.util.CarTestUtil.mapCreateCarDtoToCar;
import static star.carsharing.util.CarTestUtil.mapCreateCarDtoToCarDto;
import static star.carsharing.util.CarTestUtil.mapUpdateCarInventoryDtoToCarDto;
import static star.carsharing.util.CarTestUtil.updateCarInventoryDto;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import star.carsharing.dto.car.CarDto;
import star.carsharing.dto.car.CreateCarDto;
import star.carsharing.dto.car.UpdateCarInventoryDto;
import star.carsharing.model.Car;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
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
    @DisplayName("Verify method addCar with correct data")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addCar_CorrectData_ReturnCarDto() throws Exception {
        CreateCarDto createCarDto = createCarDto();
        String jsonRequest = objectMapper.writeValueAsString(createCarDto);

        MvcResult result = mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);

        Car car = mapCreateCarDtoToCar(createCarDto, 1L);
        CarDto expected = mapCarToCarDto(car);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("""
            Verify method addCar with incorrect data.
             CreateCarDto model and brand are empty
            """)
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    public void addCar_IncorrectData_ReturnStatus() throws Exception {
        CreateCarDto createCarDto = invalidCreateCarDto();
        String jsonRequest = objectMapper.writeValueAsString(createCarDto);

        mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verify method getAllCars with correct data")
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllCars_CorrectData_ReturnAllPageCarDto() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/cars")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<CarDto> actual = objectMapper.readValue(root.get("content").toString(),
                new TypeReference<>() {
                });
        List<CarDto> expected = listThreeCarsDto();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify method getAllCars with correct data")
    @WithMockUser(username = "costumer", roles = {"COSTUMER"})
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findCarById_CorrectData_ReturnCarDto() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/cars/2")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);

        CarDto expected = carDto(2L);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method getAllCars with incorrect data.
            Car by id not exist
            """)
    @WithMockUser(username = "costumer", roles = {"COSTUMER"})
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findCarById_IncorrectData_ReturnCarDto() throws Exception {
        mockMvc.perform(
                        get("/cars/7876")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify method updateCarById with correct data")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCarById_CorrectData_ReturnCarDto() throws Exception {
        CreateCarDto createCarDto = createCarDto();
        Long id = 3L;
        String jsonRequest = objectMapper.writeValueAsString(createCarDto);

        MvcResult result = mockMvc.perform(
                        put("/cars/" + id)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);

        CarDto expected = mapCreateCarDtoToCarDto(createCarDto, id);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method updateCarById with incorrect data.
             CreateCarDto model and brand are empty
            """)
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCarById_IncorrectDataInvalidDto_ReturnStatus() throws Exception {
        CreateCarDto createCarDto = invalidCreateCarDto();
        String jsonRequest = objectMapper.writeValueAsString(createCarDto);

        mockMvc.perform(
                        put("/cars/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Verify method updateCarById with incorrect data.
             Invalid the car id
            """)
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCarById_IncorrectDataInvalidId_ReturnStatus() throws Exception {
        CreateCarDto createCarDto = createCarDto();
        String jsonRequest = objectMapper.writeValueAsString(createCarDto);

        mockMvc.perform(
                        put("/cars/654")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify method UpdateCarInventoryDto with correct data")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCarInventoryDto_CorrectData_ReturnCarDto() throws Exception {
        UpdateCarInventoryDto updateCarInventoryDto = updateCarInventoryDto(5);
        String jsonRequest = objectMapper.writeValueAsString(updateCarInventoryDto);

        MvcResult result = mockMvc.perform(
                        patch("/cars/2")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);

        CarDto expected = mapUpdateCarInventoryDtoToCarDto(updateCarInventoryDto);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method UpdateCarInventoryDto with incorrect data.
             Invalid inventory
            """)
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCarInventoryDto_IncorrectDataInvalidDto_ReturnStatus() throws Exception {
        UpdateCarInventoryDto updateCarInventoryDto = updateCarInventoryDto(0);
        String jsonRequest = objectMapper.writeValueAsString(updateCarInventoryDto);

        mockMvc.perform(
                        patch("/cars/2")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Verify method UpdateCarInventoryDto with incorrect data.
             Car by id not exist
            """)
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCarInventoryDto_IncorrectDataInvalidId_ReturnStatus() throws Exception {
        UpdateCarInventoryDto updateCarInventoryDto = updateCarInventoryDto(1);
        String jsonRequest = objectMapper.writeValueAsString(updateCarInventoryDto);

        mockMvc.perform(
                        patch("/cars/3543")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify method deleteCarById with correct data")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteCarById_CorrectData_ReturnCarDto() throws Exception {
        mockMvc.perform(
                        delete("/cars/2")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        MvcResult result = mockMvc.perform(
                        get("/cars")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<CarDto> actual = objectMapper.readValue(root.get("content").toString(),
                new TypeReference<>() {
                });
        List<CarDto> expected = listTwoCarsDto();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method deleteCarById with incorrect data.
             Car by id not exist
            """)
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:db/car/add-cars-to-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/car/delete-cars-from-cats-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteCarById_IncorrectData_ReturnStatus() throws Exception {
        mockMvc.perform(
                        delete("/cars/2343")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

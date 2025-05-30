package star.carsharing.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import star.carsharing.mapper.CarMapper;
import star.carsharing.repository.CarRepository;
import star.carsharing.service.impl.CarServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @InjectMocks
    private CarServiceImpl carService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @Test
    @DisplayName("Verify method add with correct data")
    public void add_CorrectData_ReturnValidCarDto() {

    }
}

package steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import spring.group.spring.Application;

import java.util.Arrays;
import java.util.List;

@CucumberContextConfiguration
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class BaseSteps {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper mapper;
    protected HttpHeaders httpHeaders;
    protected String requestBody;
    protected ResponseEntity<String> response;
    protected ModelMapper modelMapper;

    public BaseSteps() {
        this.httpHeaders = new HttpHeaders();
        this.modelMapper = new ModelMapper();
        this.httpHeaders.add("Content-Type", "application/json");
    }

}

package steps;

import io.cucumber.java.en.Given;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

public class BasicSteps extends BaseSteps {
    @Given("the endpoint for {string} is available for method {string}")
    public void theEndpointForIsAvailableForMethod(String endpoint, String method) {
        response = restTemplate
                .exchange("/" + endpoint,
                        HttpMethod.OPTIONS,
                        new HttpEntity<>(null, new HttpHeaders()),
                        String.class);
        System.out.println("Response Body: " + response.getBody());
        List<String> options = Arrays.stream((response.getHeaders()
                        .get("Allow")
                        .get(0)
                        .split(",")))
                .toList();
        Assertions.assertTrue(options.contains(method.toUpperCase()));
    }
}

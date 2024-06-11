import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/cucumber",
        plugin = {"pretty"},
        glue = {"spring.group.spring.steps"}
)
public class CucumberTestRunner {
}

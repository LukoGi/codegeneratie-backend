package steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.Ignore;
import org.junit.runner.RunWith;

@Ignore // ik hoop dat dit werkt
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/cucumber/features",
        glue = "steps",
        plugin = {"pretty", "html:target/cucumber-reports.html"}
)
public class CucumberTestRunner {
}
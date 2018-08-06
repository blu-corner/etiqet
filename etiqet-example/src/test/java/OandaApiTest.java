import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "html:target/cucumber"},
    features = "src/test/resources/features/oanda_api_test.feature",
    glue = { "com.neueda.etiqet.fixture" }
)
public class OandaApiTest { }

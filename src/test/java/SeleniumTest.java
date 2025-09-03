import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeleniumTest {

    private WebDriver driver;

    @BeforeEach
    @Step("Initialize WebDriver and open browser")
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        // Read mode from system property (default = head)
        String mode = System.getProperty("mode", "head").toLowerCase();

        if (mode.equals("headless")) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
        }

        // Common options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        logStep("Running in mode: " + mode);
    }

    @Test
    @Description("Verify Google home page title contains 'Google'")
    public void googleSearchTest() {
        openUrl("https://www.google.com");
        String title = driver.getTitle();
        logStep("Page title: " + title);
        Assertions.assertTrue(title.contains("Google"), "Google title check failed!");
    }

    @Test
    @Description("Verify Example.com page title is exactly 'Example Domain'")
    public void exampleDotComTest() {
        openUrl("https://example.com");
        String title = driver.getTitle();
        logStep("Page title: " + title);
        Assertions.assertEquals("Example Domain", title, "Title did not match!");
    }

    @AfterEach
    @Step("Close the browser")
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ---------- Allure helper steps ----------
    @Step("Open URL: {url}")
    private void openUrl(String url) {
        driver.get(url);
    }

    @Step("Log step: {message}")
    private void logStep(String message) {
        Allure.step(message);
        System.out.println("✅ " + message);
    }
}

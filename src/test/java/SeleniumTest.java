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
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        String mode = System.getProperty("mode", "head").toLowerCase();

        if (mode.equals("headless")) {
            options.addArguments("--headless=new", "--disable-gpu");
        }

        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        Allure.step("Starting WebDriver in mode: " + mode);
    }

    @Test
    @Description("Verify Google homepage title contains 'Google'")
    public void googleSearchTest() {
        openPage("https://www.google.com");
        String title = driver.getTitle();
        Allure.step("Page title: " + title);
        Assertions.assertTrue(title.contains("Google"), "Google title check failed!");
    }

    @Test
    @Description("Verify Example.com title matches 'Example Domain'")
    public void exampleDotComTest() {
        openPage("https://example.com");
        String title = driver.getTitle();
        Allure.step("Page title: " + title);
        Assertions.assertEquals("Example Domain", title, "Title did not match!");
    }

    @Step("Open page {url}")
    private void openPage(String url) {
        driver.get(url);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            Allure.step("Closed WebDriver");
        }
    }
}

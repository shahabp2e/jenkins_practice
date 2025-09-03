import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeleniumTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = new org.openqa.selenium.chrome.ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    @Epic("Google Testing")
    @Feature("Search Feature")
    @Story("Open Google Home Page")
    @Description("Verify that Google loads and has the correct title")
    public void googleSearchTest() {
        driver.get("https://www.google.com");
        Allure.step("Opened Google homepage");
        Assertions.assertTrue(driver.getTitle().contains("Google"), "Google title check failed!");
    }

    @Test
    @Epic("Example Testing")
    @Feature("Navigation Feature")
    @Story("Open Example.com")
    @Description("Verify that Example Domain loads and has the correct title")
    public void exampleDotComTest() {
        driver.get("https://example.com");
        Allure.step("Opened Example.com");
        Assertions.assertEquals("Example Domain", driver.getTitle(), "Title did not match!");
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] takeScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        // take screenshot always (optional: only on failure)
        takeScreenshot();
        if (driver != null) {
            driver.quit();
        }
    }
}

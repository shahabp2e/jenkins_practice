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

        // ❌ No --user-data-dir (prevents profile conflicts)
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        System.out.println("✅ Running in mode: " + mode);
    }

    @Test
    public void googleSearchTest() {
        driver.get("https://www.google.com");
        System.out.println("Title is: " + driver.getTitle());
        Assertions.assertTrue(driver.getTitle().contains("Google"), "Google title check failed!");
    }

    @Test
    public void exampleDotComTest() {
        driver.get("https://example.com");
        System.out.println("Title is: " + driver.getTitle());
        Assertions.assertEquals("Example Domain", driver.getTitle(), "Title did not match!");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Files;
import java.nio.file.Path;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SeleniumTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();

        // Create unique Chrome profile for Jenkins run
        Path tempProfile = Files.createTempDirectory("chrome-profile");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // headless for CI
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--user-data-dir=" + tempProfile.toAbsolutePath());

        driver = new ChromeDriver(options);
    }

    @Test
    @Order(1)
    public void googleTitleTest() {
        driver.get("https://www.google.com");
        Assertions.assertTrue(driver.getTitle().contains("Google"));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

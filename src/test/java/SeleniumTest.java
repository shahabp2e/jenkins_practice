import io.qameta.allure.Attachment;
import io.qameta.allure.junit5.AllureJUnit5;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AllureJUnit5.class)
public class SeleniumTest {

    WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
    }

    @Test
    public void googleSearchTest() {
        driver.get("https://www.google.com");
        driver.findElement(By.name("q")).sendKeys("Shahab");
        driver.findElement(By.name("q")).submit();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.titleContains("Shahab"));

        assertTrue(driver.getTitle().contains("Shahab"), "Page title should contain 'Shahab'");
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        if (driver != null) {
            try {
                takeScreenshot(testInfo.getDisplayName()); // always capture
            } catch (Exception e) {
                e.printStackTrace();
            }
            driver.quit();
        }
    }

    @Attachment(value = "Screenshot - {0}", type = "image/png")
    public byte[] takeScreenshot(String name) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}

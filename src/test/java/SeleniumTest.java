import io.qameta.allure.Allure;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({AllureJunit5.class, SeleniumTest.ScreenshotOnFailure.class})
public class SeleniumTest {

    private WebDriver driver;
    private Path tempUserDataDir;

    @BeforeEach
    public void setUp() throws IOException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");

        tempUserDataDir = Files.createTempDirectory("chrome-user-data");
        options.addArguments("--user-data-dir=" + tempUserDataDir.toAbsolutePath());

        driver = new ChromeDriver(options);
    }

    // ✅ Passed test
    @Test
    public void googleSearchTest() {
        driver.get("https://www.google.com");
        assertTrue(driver.getTitle().contains("Google"), "Title should contain 'Google'");
    }

    // ❌ Optional demo failing test (for Allure report only)
    @Test
    public void failingTestExample() {
        driver.get("https://www.google.com");
        assertTrue(driver.getTitle().contains("Bing"), "Title should contain 'Bing'");
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (driver != null) {
            driver.quit();
        }

        if (tempUserDataDir != null && Files.exists(tempUserDataDir)) {
            deleteDirectoryRecursively(tempUserDataDir);
        }
    }

    private void deleteDirectoryRecursively(Path path) throws IOException {
        Files.walk(path)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    // --- JUnit 5 TestWatcher to capture screenshots only on failure ---
    public static class ScreenshotOnFailure implements TestWatcher {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            SeleniumTest testInstance = (SeleniumTest) context.getRequiredTestInstance();
            WebDriver driver = testInstance.driver;
            if (driver != null) {
                Allure.addAttachment("Screenshot on Failure",
                        new ByteArrayInputStream(((TakesScreenshot) driver)
                                .getScreenshotAs(OutputType.BYTES)));
            }
        }

        @Override
        public void testSuccessful(ExtensionContext context) {
            // Do nothing for passed tests
        }
    }
}

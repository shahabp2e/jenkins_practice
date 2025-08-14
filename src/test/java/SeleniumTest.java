import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ExtendWith(AllureJunit5.class)
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

        // Create unique temporary user data directory to avoid conflicts
        tempUserDataDir = Files.createTempDirectory("chrome-user-data");
        options.addArguments("--user-data-dir=" + tempUserDataDir.toAbsolutePath().toString());

        driver = new ChromeDriver(options);
    }

    @Test
    public void googleSearchTest() {
        driver.get("https://www.google.com");
        System.out.println("Title is: " + driver.getTitle());
        // Add your assertions here if needed
    }

    // New test case added
    @Test
    public void exampleDotComTest() {
        driver.get("https://example.com");
        System.out.println("Title is: " + driver.getTitle());
        assert driver.getTitle().equals("Example Domain") 
            : "Title did not match!";
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (driver != null) {
            driver.quit();
        }
        // Clean up the temp user data directory after test
        if (tempUserDataDir != null && Files.exists(tempUserDataDir)) {
            deleteDirectoryRecursively(tempUserDataDir);
        }
    }

    // Helper method to recursively delete temp directory
    private void deleteDirectoryRecursively(Path path) throws IOException {
        Files.walk(path)
                .sorted((a, b) -> b.compareTo(a))  // delete children first
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}

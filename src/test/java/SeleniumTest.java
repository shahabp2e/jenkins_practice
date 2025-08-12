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
public void setUp() {
    ChromeOptions options = new ChromeOptions();
    // options.addArguments("--headless=new");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--disable-gpu");
    options.addArguments("--remote-allow-origins=*");

    // Temporarily comment this out and try if it works without user-data-dir
    // tempUserDataDir = Files.createTempDirectory("chrome-user-data");
    // options.addArguments("--user-data-dir=" + tempUserDataDir.toAbsolutePath().toString());

    driver = new ChromeDriver(options);
}

    @Test
    public void googleSearchTest() {
        driver.get("https://www.google.com");
        System.out.println("Title is: " + driver.getTitle());
        // Add your assertions here if needed
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (driver != null) {
            driver.quit();
        }
        // Temporary user data directory ko delete karen
        if (tempUserDataDir != null && Files.exists(tempUserDataDir)) {
            deleteDirectoryRecursively(tempUserDataDir);
        }
    }

    private void deleteDirectoryRecursively(Path path) throws IOException {
        Files.walk(path)
                .sorted((a, b) -> b.compareTo(a))  // children pehle delete karein
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}

import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AllureJunit5.class)
public class SeleniumTest {

    @Test
    public void googleSearchTest() {
        WebDriverManager.chromedriver().setup();

        // Chrome options for Jenkins/CI environment
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Headless mode
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);

        driver.get("https://www.google.com");
        driver.findElement(By.name("q")).sendKeys("Shahab");
        driver.findElement(By.name("q")).submit();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.titleContains("Shahab"));

        assertTrue(driver.getTitle().contains("Shahab"));

        driver.quit();
    }
}

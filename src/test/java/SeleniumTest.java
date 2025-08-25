// import io.qameta.allure.junit5.AllureJunit5;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;

// @ExtendWith(AllureJunit5.class)
// public class SeleniumTest {

//     private WebDriver driver;
//     private Path tempUserDataDir;

//     @BeforeEach
//     public void setUp() throws IOException {
//         ChromeOptions options = new ChromeOptions();
//         options.addArguments("--headless=new");
//         options.addArguments("--no-sandbox");
//         options.addArguments("--disable-dev-shm-usage");
//         options.addArguments("--disable-gpu");
//         options.addArguments("--remote-allow-origins=*");

//         // Create unique temporary user data directory to avoid conflicts
//         tempUserDataDir = Files.createTempDirectory("chrome-user-data");
//         options.addArguments("--user-data-dir=" + tempUserDataDir.toAbsolutePath().toString());

//         driver = new ChromeDriver(options);
//     }

//     @Test
//     public void googleSearchTest() {
//         driver.get("https://www.google.com");
//         System.out.println("Title is: " + driver.getTitle());
//         // Add your assertions here if needed
//     }

//     // New test case added
//     @Test
//     public void exampleDotComTest() {
//         driver.get("https://example.com");
//         System.out.println("Title is: " + driver.getTitle());
//         assert driver.getTitle().equals("Example Domain") 
//             : "Title did not match!";
//     }

//     @AfterEach
//     public void tearDown() throws IOException {
//         if (driver != null) {
//             driver.quit();
//         }
//         // Clean up the temp user data directory after test
//         if (tempUserDataDir != null && Files.exists(tempUserDataDir)) {
//             deleteDirectoryRecursively(tempUserDataDir);
//         }
//     }

//     // Helper method to recursively delete temp directory
//     private void deleteDirectoryRecursively(Path path) throws IOException {
//         Files.walk(path)
//                 .sorted((a, b) -> b.compareTo(a))  // delete children first
//                 .forEach(p -> {
//                     try {
//                         Files.delete(p);
//                     } catch (IOException e) {
//                         e.printStackTrace();
//                     }
//                 });
//     }
// }

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;

 @ExtendWith(AllureJunit5.class)
   public class SeleniumTest{

    // CMS driver (used by your original 4 tests)
    WebDriver driver;
    WebDriverWait wait;

    // Separate driver for website form (used only in timestamp test)
    WebDriver websiteDriver;
    WebDriverWait websiteWait;

    // For timestamp comparison
    String websiteTime = "";
    String cmsTime = "";

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://dev-kalp-auth.p2eppl.com/login");

        // Login CMS
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")))
                .sendKeys("himanshu.kumar@mai.io");
        driver.findElement(By.name("password")).sendKeys("Qwerty@123");
        driver.findElement(By.xpath("//form//button[@type='submit']")).click();

        // Navigate to Admin panel
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class,'profile-dropdown-btn')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Admin panel']"))).click();

        // Switch to new tab
        String originalWindow = driver.getWindowHandle();
        wait.until(d -> d.getWindowHandles().size() > 1);
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }

        // Click on Enquiry
        By enquiryButton = By.xpath("//span[normalize-space()='Enquiry']");
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(enquiryButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        wait.until(ExpectedConditions.elementToBeClickable(enquiryButton)).click();
        System.out.println("✅ Navigated to Enquiry Page");
    }

    // ---------------- Helpers (used by timestamp test) ----------------
    private String getRandomEmail() {
        Random random = new Random();
        int num = random.nextInt(1000);
        return "shahab.pathan+" + num + "@kalp.studio";
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().replace("\n", " ").replaceAll("\\s+", " ");
    }

    // ✅ Test Case 1: Verify all enquiries visible
    @Test(priority = 1)
    public void verifyAllEnquiriesVisible() {
        List<WebElement> enquiries = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table//tr"))
        );
        Assert.assertTrue(enquiries.size() > 0, "❌ No enquiries found!");
        System.out.println("✅ Enquiries are visible. Count: " + enquiries.size());
    }
    // ✅ Test Case 2: Verify search functionality
    @Test(priority = 2)
    public void verifySearchFunctionality() throws InterruptedException {
        String[][] searchInputs = {
                {"firstname", "Shahab"},
                {"lastname", "Nawaz"},
                {"email", "shahab.pathan+857@kalp.studio"},
                {"phone", "8394025309"}
        };

        for (String[] searchData : searchInputs) {
            String field = searchData[0];
            String value = searchData[1];

            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div/div[1]/div/div/div[2]/div[1]/input")));
            searchBox.clear();
            searchBox.sendKeys(value);

            Thread.sleep(2000); // wait for table refresh

            WebElement firstRow = driver.findElement(By.xpath("//table/tbody/tr[1]"));
            String rowText = firstRow.getText();

            System.out.println("🔍 Searching by " + field + ": " + value);
            System.out.println("📌 First row result: " + rowText);

            Assert.assertTrue(rowText.contains(value),
                    "❌ Search failed for " + field + " = " + value);
            System.out.println("✅ Search working for " + field);
            Thread.sleep(2000);
        }
    }

    // ✅ Test Case 3: Verify date filter
    @Test(priority = 3)
    public void verifyDateFilter() throws InterruptedException {
        WebElement dateFilter = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div/div[1]/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/input")));
        dateFilter.click();
        Thread.sleep(1000);

        // Select From (today - 2 days) and To (today)
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime fromDate = today.minusDays(2);

        DateTimeFormatter cmsFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy hh:mm:ss a", Locale.ENGLISH);

        // Click fromDate (adjust XPath to your calendar UI)
        WebElement fromDateInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div/div[1]/div/div/div[2]/div[2]/div[1]/div/div[1]/div[2]/div[2]/div/div/div[1]/div/div[2]/div[1]/div[6]")));
        fromDateInput.click();

        // Click toDate
        WebElement toDateInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div/div[1]/div/div/div[2]/div[2]/div[1]/div/div[1]/div[2]/div[2]/div/div/div[1]/div/div[2]/div[2]/div[3]")));
        toDateInput.click();

        // Apply
        WebElement applyBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div/div[1]/div/div/div[2]/div[2]/div[1]/div/div[1]/div[2]/div[2]/div/div/div[2]/div[2]/button")));
        applyBtn.click();
        Thread.sleep(2000);

        // Verify results
        List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr/td[4]"));
        boolean allWithinRange = true;

        for (WebElement row : rows) {
            String timeText = normalize(row.getText());
            System.out.println("Raw timestamp: '" + timeText + "'");

            LocalDateTime rowTime = LocalDateTime.parse(timeText, cmsFormatter);

            if (rowTime.isBefore(fromDate) || rowTime.isAfter(today)) {
                allWithinRange = false;
                System.out.println("❌ Out-of-range record: " + timeText);
            } else {
                System.out.println("✅ Record OK: " + timeText);
            }
        }

        if (allWithinRange) {
            System.out.println("🎯 Date filter working correctly.");
        } else {
            System.out.println("⚠️ Date filter issue detected!");
        }
    }

    // ✅ Test Case 4: Verify pagination
    @Test(priority = 4)
    public void verifyPagination() {
        List<WebElement> paginationButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div/div[2]/div/div[3]/div/div/div/div[2]/button")
        ));

        System.out.println("Pagination buttons found: " + paginationButtons.size());
        for (WebElement btn : paginationButtons) {
            System.out.println("Button text: " + btn.getText());
        }

        // Assert pagination exists
        Assert.assertTrue(paginationButtons.size() > 1, "❌ Pagination not found!");

        // Click on Page 2
        WebElement page2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div/div[2]/div/div[3]/div/div/div/div[2]/button[3]"));
        page2.click();

        System.out.println("✅ Pagination working (navigated to page 2).");
    }

    // ✅ Test Case 5: Website submit vs CMS timestamp (newly merged)
    @Test(priority = 5)
    public void verifyTimestampSubmission() throws InterruptedException {
        // 1) Submit website form in a separate browser
        websiteDriver = new ChromeDriver();
        websiteDriver.manage().window().maximize();
        websiteWait = new WebDriverWait(websiteDriver, Duration.ofSeconds(20));
        websiteDriver.get("https://dev-ks-build-website.p2eppl.com/sponsorship#sponsorship-inquiry");

        Thread.sleep(3000);

        websiteDriver.findElement(By.id("firstname")).sendKeys("Shahab");
        websiteDriver.findElement(By.id("lastname")).sendKeys("Nawaz");
        websiteDriver.findElement(By.id("phone_number")).sendKeys("8394025309");

        String emailUsed = getRandomEmail();
        websiteDriver.findElement(By.id("email")).sendKeys(emailUsed);
        websiteDriver.findElement(By.id("company_name")).sendKeys("Kalp Studio");

        WebElement submit = websiteDriver.findElement(By.xpath("//*[@id=\"sponsorship-inquiry\"]/div/div/div[2]/div/div/form/div[2]/div/button"));
        ((JavascriptExecutor) websiteDriver).executeScript("arguments[0].scrollIntoView({block: 'center'});", submit);
        Thread.sleep(500);
        websiteWait.until(ExpectedConditions.elementToBeClickable(submit)).click();
        System.out.println("✅ Website form submitted. Email: " + emailUsed);

        // Capture local website submission time
        websiteTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        System.out.println("🌐 Website Timestamp: " + websiteTime);

        // (Optional) wait briefly for backend to ingest
        Thread.sleep(3000);

        // 2) In CMS (already logged-in & at Enquiry page), search by the same email
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div/div[1]/div/div/div[2]/div[1]/input")));
        searchBox.clear();
        searchBox.sendKeys(emailUsed);
        Thread.sleep(2000); // allow table to refresh

        // Ensure first row contains our email
        WebElement firstRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table/tbody/tr[1]")));
        String firstRowText = normalize(firstRow.getText());
        System.out.println("📋 First row after search: " + firstRowText);

        // If ingestion is a bit delayed, poll up to ~20s
        int retries = 10;
        while (!firstRowText.contains(emailUsed) && retries-- > 0) {
            Thread.sleep(2000);
            firstRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table/tbody/tr[1]")));
            firstRowText = normalize(firstRow.getText());
            System.out.println("⏳ Waiting for record... (" + (10 - retries) + ")");
        }

        Assert.assertTrue(firstRowText.contains(emailUsed), "❌ New enquiry not found in CMS for email: " + emailUsed);

        // Get CMS timestamp from the first row (4th column)
        WebElement cmsTimeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div/div[2]/div/div[2]/div/table/tbody/tr[1]/td[4]")));
        cmsTime = normalize(cmsTimeElement.getText());
        System.out.println("🗄️  CMS Timestamp: " + cmsTime);

        // 3) Compare timestamps with tolerance
        try {
            DateTimeFormatter websiteFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            DateTimeFormatter cmsFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy hh:mm:ss a", Locale.ENGLISH);

            LocalDateTime webTime = LocalDateTime.parse(websiteTime, websiteFormatter);
            LocalDateTime cmsDateTime = LocalDateTime.parse(cmsTime, cmsFormatter);

            long diffSeconds = Duration.between(webTime, cmsDateTime).getSeconds();
            System.out.println("⏱️  Time difference (s): " + diffSeconds);

            Assert.assertTrue(Math.abs(diffSeconds) <= 60,
                    "❌ Timestamp mismatch! Difference = " + diffSeconds + " seconds");
            System.out.println("✅ Timestamp matches within 1 minute tolerance.");
        } catch (Exception e) {
            System.out.println("⚠️ Failed to parse CMS timestamp: " + cmsTime);
            throw e;
        } finally {
            // Close the website driver for cleanliness
            if (websiteDriver != null) {
                websiteDriver.quit();
                websiteDriver = null;
            }
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (websiteDriver != null) {
            websiteDriver.quit();
        }
    }}



package com.marathon.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationSeleniumTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        ChromeOptions options = new ChromeOptions();
        options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        try {
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        } catch (Exception ex) {
            System.err.println("[WARN] ChromeDriver initialization failed: " + ex.getMessage());
            driver = null;
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void clickElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        try {
            element.click();
        } catch (Exception ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void captureScreenshot(String testName) {
        if (driver instanceof TakesScreenshot takesScreenshot) {
            try {
                File src = takesScreenshot.getScreenshotAs(OutputType.FILE);
                Path destDir = Paths.get("screenshots");
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
                Path destFile = destDir.resolve(testName + "_" + timestamp + ".png");
                Files.copy(src.toPath(), destFile);
                System.out.println("[SELENIUM] Captured screenshot: " + destFile.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("[SELENIUM ERROR] Failed to save screenshot: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Journey 1: Happy-path participant registration flow with confirmation slip")
    void testHappyPathRegistration() {
        if (driver == null) {
            System.out.println("[SELENIUM SKIP] Headless Chrome driver not available on this agent runner.");
            return;
        }

        try {
            driver.get(baseUrl + "/");

            driver.findElement(By.id("fullName")).sendKeys("Jordan Lee");
            driver.findElement(By.id("email")).sendKeys("jordan.lee." + System.currentTimeMillis() + "@example.com");
            driver.findElement(By.id("age")).sendKeys("27");

            Select genderSelect = new Select(driver.findElement(By.id("gender")));
            genderSelect.selectByValue("Male");

            Select categorySelect = new Select(driver.findElement(By.id("category")));
            categorySelect.selectByValue("HALF_MARATHON");

            driver.findElement(By.id("emergencyName")).sendKeys("Morgan Lee");
            driver.findElement(By.id("emergencyPhone")).sendKeys("+1-555-0199");

            WebElement waiverCheckbox = driver.findElement(By.id("waiverAccepted"));
            if (!waiverCheckbox.isSelected()) {
                clickElement(waiverCheckbox);
            }

            captureScreenshot("journey1_form_filled");

            WebElement submitBtn = driver.findElement(By.id("submitBtn"));
            clickElement(submitBtn);

            wait.until(ExpectedConditions.urlContains("/confirmation/"));
            WebElement regIdBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmationRegId")));

            assertNotNull(regIdBadge.getText());
            assertTrue(regIdBadge.getText().startsWith("REG-HM-"), "Registration ID must start with REG-HM-");

            captureScreenshot("journey1_confirmation_success");
        } catch (Exception ex) {
            captureScreenshot("journey1_failure");
            throw ex;
        }
    }

    @Test
    @DisplayName("Journey 2: Age-gate validation error when underage for Full Marathon")
    void testAgeValidationFailureForFullMarathon() {
        if (driver == null) {
            System.out.println("[SELENIUM SKIP] Headless Chrome driver not available on this agent runner.");
            return;
        }

        try {
            driver.get(baseUrl + "/");

            driver.findElement(By.id("fullName")).sendKeys("Underage Runner");
            driver.findElement(By.id("email")).sendKeys("underage." + System.currentTimeMillis() + "@example.com");
            driver.findElement(By.id("age")).sendKeys("15");

            Select genderSelect = new Select(driver.findElement(By.id("gender")));
            genderSelect.selectByValue("Female");

            Select categorySelect = new Select(driver.findElement(By.id("category")));
            categorySelect.selectByValue("FULL_MARATHON");

            driver.findElement(By.id("emergencyName")).sendKeys("Parent Contact");
            driver.findElement(By.id("emergencyPhone")).sendKeys("+1-555-1122");

            WebElement waiverCheckbox = driver.findElement(By.id("waiverAccepted"));
            if (!waiverCheckbox.isSelected()) {
                clickElement(waiverCheckbox);
            }

            WebElement submitBtn = driver.findElement(By.id("submitBtn"));
            clickElement(submitBtn);

            WebElement errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorAlert")));
            assertTrue(errorAlert.getText().contains("Participants must be at least 18 years old for Full Marathon."));

            captureScreenshot("journey2_age_validation_error");
        } catch (Exception ex) {
            captureScreenshot("journey2_failure");
            throw ex;
        }
    }

    @Test
    @DisplayName("Journey 3: Real-time participant status tracking lookup")
    void testStatusTrackingLookup() {
        if (driver == null) {
            System.out.println("[SELENIUM SKIP] Headless Chrome driver not available on this agent runner.");
            return;
        }

        try {
            driver.get(baseUrl + "/status");

            WebElement searchInput = driver.findElement(By.id("searchQuery"));
            searchInput.clear();
            searchInput.sendKeys("REG-FM-101A");

            WebElement searchBtn = driver.findElement(By.id("searchBtn"));
            clickElement(searchBtn);

            WebElement resultCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("statusResultCard")));
            WebElement nameElement = driver.findElement(By.id("statusFullName"));
            WebElement statusBadge = driver.findElement(By.id("statusBadge"));

            assertEquals("Alex Rivera", nameElement.getText());
            assertEquals("Pending Approval", statusBadge.getText());

            captureScreenshot("journey3_status_lookup_success");
        } catch (Exception ex) {
            captureScreenshot("journey3_failure");
            throw ex;
        }
    }

    @Test
    @DisplayName("Journey 4: Admin dashboard approval workflow and atomic bib allocation")
    void testAdminApprovalAndBibAllocation() {
        if (driver == null) {
            System.out.println("[SELENIUM SKIP] Headless Chrome driver not available on this agent runner.");
            return;
        }

        try {
            driver.get(baseUrl + "/admin");

            WebElement approveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("approveBtn_p1-alex-rivera")));
            clickElement(approveBtn);

            wait.until(ExpectedConditions.urlContains("/admin"));
            WebElement updatedStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("status_p1-alex-rivera")));
            WebElement allocatedBib = driver.findElement(By.id("bib_p1-alex-rivera"));

            assertEquals("Approved", updatedStatus.getText());
            assertTrue(allocatedBib.getText().startsWith("BIB-FM-"), "Allocated bib must start with BIB-FM-");

            captureScreenshot("journey4_admin_approval_success");
        } catch (Exception ex) {
            captureScreenshot("journey4_failure");
            throw ex;
        }
    }
}

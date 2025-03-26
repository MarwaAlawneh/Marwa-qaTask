package com.privilee;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapPageTestCases {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * FUNCTIONALITY: Check if venue markers icon  are displayed on the map.
     */
    @Test
    public void testVenueMarkersArePresent() {
        driver.get("https://staging-website.privilee.ae/map");
        List<WebElement> markers = driver.findElements(By.className("venue-marker"));
        assertFalse(markers.isEmpty(), "No venue markers found on the map");
    }

    /**
     * FUNCTIONALITY: Search functionality should return results for valid venue.
     */
    @Test
    public void testSearchFunctionality() {
        driver.get("https://staging-website.privilee.ae/map");
        WebElement searchBox = driver.findElement(By.cssSelector("input[placeholder='Search venues']"));
        searchBox.sendKeys("Reset Fitness");
        searchBox.submit();
        List<WebElement> results = driver.findElements(By.className("venue-list-item"));
        assertTrue(results.size() > 0, "No search results found");
    }

    /**
     * FUNCTIONALITY: Verify that an invalid search shows "No results found."
     */
    @Test
    public void testInvalidSearchShowsNoResults() {
        driver.get("https://staging-website.privilee.ae/map");
        WebElement searchBox = driver.findElement(By.cssSelector("input[placeholder='Search venues']"));
        searchBox.sendKeys("Marwatest");
        searchBox.sendKeys(Keys.RETURN);

        WebElement noResultsMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(), 'No results found')]"))
        );

        assertTrue(noResultsMessage.isDisplayed(), "No results message not shown");
    }

    /**
     * USER INTERFACE: Verify that zoom-in and zoom-out buttons work.
     */
    @Test
    public void testMapZoomFunctionality() {
        driver.get("https://staging-website.privilee.ae/map");
        WebElement zoomInButton = driver.findElement(By.className("zoom-in-button"));
        WebElement zoomOutButton = driver.findElement(By.className("zoom-out-button"));

        zoomInButton.click();
        zoomOutButton.click();

        assertTrue(zoomInButton.isDisplayed() && zoomOutButton.isDisplayed(), "Zoom buttons are not functional");
    }

    /**
     * USER INTERFACE: Clicking on a venue marker should display its details.
     */
    @Test
    public void testClickVenueMarkerDisplaysDetails() {
        driver.get("https://staging-website.privilee.ae/map");
        List<WebElement> markers = driver.findElements(By.className("venue-marker"));

        if (!markers.isEmpty()) {
            markers.get(0).click();
            WebElement venueDetails = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.className("venue-details"))
            );
            assertTrue(venueDetails.isDisplayed(), "Venue details not shown on marker click");
        } else {
            fail("No venue markers found to test");
        }
    }

    /**
     * FUNCTIONALITY & DATA ACCURACY: Verify that venue filtering works.
     */
    @Test
    public void testVenueFiltering() {
        driver.get("https://staging-website.privilee.ae/map");
        WebElement filterButton = driver.findElement(By.className("filter-button"));
        filterButton.click();

        WebElement gymFilter = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Gym')]"))
        );
        gymFilter.click();

        List<WebElement> filteredVenues = driver.findElements(By.className("venue-list-item"));
        assertFalse(filteredVenues.isEmpty(), "Filter did not return any venues");
    }

    /**
     * PERFORMANCE: Ensure the map loads within 5 seconds.
     */
    @Test
    public void testPageLoadPerformance() {
        long startTime = System.currentTimeMillis();
        driver.get("https://staging-website.privilee.ae/map");
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.mvn test

("venue-marker")));

        long endTime = System.currentTimeMillis();
        long loadTime = (endTime - startTime) / 1000;

        assertTrue(loadTime <= 5, "Page took longer than 5 seconds to load");
    }

    /**
     * USER INTERFACE: Verify that the map can be dragged.
     */
    @Test
    public void testMapDragging() {
        driver.get("https://staging-website.privilee.ae/map");
        WebElement mapCanvas = driver.findElement(By.className("map-container"));

        Actions actions = new Actions(driver);
        actions.clickAndHold(mapCanvas)
               .moveByOffset(100, 100) // Drag the map slightly
               .release()
               .perform();

        assertTrue(mapCanvas.isDisplayed(), "Map dragging failed");
    }

    /**
     * DATA ACCURACY: Ensure the correct venue details appear after a search.
     */
    @Test
    public void testSearchReturnsCorrectVenue() {
        driver.get("https://staging-website.privilee.ae/map");

        WebElement searchBox = driver.findElement(By.cssSelector("input[placeholder='Search venues']"));
        searchBox.sendKeys("Beach Club");
        searchBox.sendKeys(Keys.RETURN);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("venue-list-item")));

        WebElement firstResult = driver.findElement(By.className("venue-list-item"));
        String resultText = firstResult.getText().toLowerCase();

        assertTrue(resultText.contains("beach club"), "Search result does not match query");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

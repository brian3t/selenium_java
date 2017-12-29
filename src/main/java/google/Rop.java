package google;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

//import org.openqa.selenium.firefox.FirefoxDriver;

public class Rop {
    //    public static final Boolean IS_DEBUG = true;
    public static final Boolean IS_DEBUG = false;
    //    public static final String DB_HOST = "172.28.1.116";
    public static final String DB_HOST = "127.0.0.1";
    public static final Integer WAIT_TIME = 500;
    public static final Integer WAIT_LONG_TIME = 10000;
    public static final String REPORT_NAME = "PO Sales b3t";

    public static void main(String[] args) throws InterruptedException {
        String report_selector = String.format("//div[contains(@class,'x-grid-cell-inner') and text()='%s']", REPORT_NAME);

        // Create an object driver for accessing driver method’s
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\Users\\tn423731\\AppData\\Local\\Google\\Chrome\\User Data");
        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);

// navigate() will open URL
        driver.navigate().to("https://app.retailops.com/");

        System.out.println("Launching Browser");

        WebDriverWait wait = new WebDriverWait(driver, 80);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='submit']")));
//        driver.findElement(By.id("elUsername")).sendKeys("bnguyen@shoemetro.com");
//        driver.findElement(By.id("elPassword")).sendKeys("ShoeMetro2");
        driver.findElement(By.cssSelector("input[type='submit']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.favorite-tool-icon[data-test='Reporting']")));
        driver.findElement(By.cssSelector("div.favorite-tool-icon[data-test='Reporting']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.x-grid-view")));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        Boolean worker_error = false;

        try {
            for (int j = 0; j < 36; j++) {//from 19000 down to 1000
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(report_selector)));
                js.executeScript("$('div.x-grid-cell-inner:contains(\"PO Sales b3t\")').click()");
                if (j == 0) {
                    js.executeScript("$('div.x-grid-cell-inner:contains(\"PO Sales b3t\")').click()");
                }

                String report_filter = "div.gt-report-data span.iot-operation";
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(report_filter)));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(report_filter)));
                WebElement report_filter_element = driver.findElement(By.cssSelector(report_filter));

                if (IS_DEBUG && j > 1) {
                    j = 9999;
                }
                Actions actions = new Actions(driver);
                report_filter_element = driver.findElement(By.cssSelector(report_filter));
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(report_filter)));
                actions.moveToElement(report_filter_element);
                actions.click();
                for (int i = 0; i < 15; i++) {
                    actions.sendKeys(Keys.ARROW_RIGHT);
                }
                for (int i = 0; i < 15; i++) {
                    actions.sendKeys(Keys.BACK_SPACE);
                }
                String range_start = Integer.toString(18800 - (200 * j));
                String range_end = Integer.toString(19000 - (200 * j));
                actions.sendKeys("PO.NUMBER ");
//            actions.pause(WAIT_LONG_TIME);
                actions.sendKeys(String.format("> \"%s\" ", range_start));
//            actions.pause(WAIT_LONG_TIME);
                actions.sendKeys(" AND PO.NUMBER ");
//            actions.pause(WAIT_LONG_TIME);
                actions.sendKeys(String.format("< \"%s\" ", range_end));
//            actions.pause(WAIT_LONG_TIME);
                actions.sendKeys(Keys.ENTER);
                actions.build().perform();//done building query


           /* if (IS_DEBUG && j == 1) {
                Thread.sleep(WAIT_LONG_TIME);
            }*/

                //now download
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[descendant::text()='Download CSV']")));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[descendant::text()='Download CSV']")));
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[descendant::text()='Download CSV']")));
                driver.findElement(By.xpath("//button[descendant::text()='Download CSV']")).click();
                Rop.wait_till_download_finish("C:\\Users\\tn423731\\Downloads");
                System.out.println(Integer.toString(j + 1) + " download completed");

                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.active-tab")));
                Actions action = new Actions(driver);
                WebElement we = driver.findElement(By.cssSelector("div.active-tab"));
                action.moveToElement(we).build().perform();
                js.executeScript("$('div.active-tab>span').click()");

                //check for worker protocol error
                worker_error = (driver.findElements(By.xpath("//div[text()='Error: Worker protocol error: Worker exceeded timeout']")).size() > 0);
                if (worker_error){
                    //todob write to file and exit
                    Integer a = 1;
                }

//            Thread.sleep(WAIT_LONG_TIME);
            }
        } catch (TimeoutException e) {
            System.out.println(e.getMessage());
            File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(scrFile, new File("c:\\Users\\tn423731\\Downloads\\selenium_screenshot.png"));
            } catch (IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }
//        driver.quit();

    }

    public static void wait_till_download_finish(String download_folder) throws NullPointerException {
        try {
            Thread.sleep(WAIT_LONG_TIME);//wait for download to start
        } catch (InterruptedException e){

        }
        Path dir = FileSystems.getDefault().getPath(download_folder);
        Boolean still_downloading = false;
        do {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                still_downloading = false;
                for (Path file : stream) {
                    if (file.getFileName().toString().endsWith("crdownload")) {
                        still_downloading = true;
                        System.out.println("I see you. Still downloading?");
                        System.out.println(file.getFileName());
                        break;
                    }
                /*long last_mod = file.toFile().lastModified();
                long minutes_since = (last_mod - System.currentTimeMillis()) / 1000 / 60;
                if (minutes_since < -5) {
                    System.out.println("More than 5 minutes ago");
                }*/
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {

                }
            } catch (IOException | DirectoryIteratorException x) {
                System.err.println(x);
            }
        }
        while (still_downloading);

    }
}

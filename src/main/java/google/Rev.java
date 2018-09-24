package google;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

//import org.openqa.selenium.firefox.FirefoxDriver;

public class Rev {
    //    public static final Boolean IS_DEBUG = true;
    public static final Boolean IS_DEBUG = false;
    public static final Integer WAIT_SHORT_TIME = 5000;
    public static final Integer WAIT_LONG_TIME = 10000;
    //    public static final String DB_HOST = "172.28.1.116";
    public static final String REPORT_NAME = "PO Sales b3t";
    public static final String SITES[] = {"atosdrg","atosrush","himg","monroesurg","pch","ppmh","pssurg","sbhny","smsomonroe","smsopssurg","tyronehospital-physician","ubmc","wrmc","staging-wrmc"};

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "/usr/lib/chromium-browser/chromedriver");
        String report_selector = String.format("//div[contains(@class,'x-grid-cell-inner') and text()='%s']", REPORT_NAME);

        // Create an object driver for accessing driver method’s
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("user-data-dir=/home/tri/.config/google-chrome");
        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);

// navigate() will open URL

        for (String site:SITES) {
            driver.navigate().to("https://" + site + ".revenuemasters.com/");

            System.out.println("Launching Browser");
            WebDriverWait wait = new WebDriverWait(driver, 80);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='submit']")));
            driver.findElement(By.id("username")).sendKeys("qaauto@revenuemasters.com");
//            driver.findElement(By.id("password")).sendKeys("Paco2016!");
            driver.findElement(By.cssSelector("input[type='submit']")).click();


            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input.cm_submit")));//<input class="left cm_submit Btn_blue" type="submit" value="OK">
            driver.findElement(By.cssSelector("input.cm_submit")).click();

            driver.navigate().to("https://" + site + ".revenuemasters.com/index/quickcharts");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.claim-summary__title")));
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(scrFile, new File("/home/tri/bench/selenium/" + site + "selenium_screenshot.png"));
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                driver.quit();
            }
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href='/login/logout']")));//<input class="left cm_submit Btn_blue" type="submit" value="OK">
            driver.findElement(By.cssSelector("a[href='/login/logout']")).click();
        }
//        Thread.sleep(25000);
        driver.quit();

        Boolean worker_error = false;

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

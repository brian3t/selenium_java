package google;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.openqa.selenium.firefox.FirefoxDriver;

public class Rop {
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

//        Thread.sleep(WAIT_LONG_TIME);
//        driver.close();

/*
        options.addArguments("start-maximized");
        options.addArguments("user-data-dir=/path/to/your/custom/profile");
        String chromeProfile = "C:\\Users\\tn423731\\AppData\\Local\\Google\\Chrome\\User Data\\Default";
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        ArrayList<String> switches = new ArrayList<String>();
        switches.add("--user-data-dir=" + chromeProfile);
        capabilities.setCapability("chrome.switches", switches);
        WebDriver driver = new ChromeDriver();
*/

// navigate() will open URL
        driver.navigate().to("https://app.retailops.com/");

        System.out.println("Launching Browser");

// using ID of an element to identify element, <u>sendkeys</u> is used to sendtext in field
//        driver.findElement(By.<em>id("email")).sendKeys("username");
//        driver.findElement(By.<em>id("pass")).sendKeys("password");

        WebDriverWait wait = new WebDriverWait(driver, 20);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='submit']")));
//        driver.findElement(By.id("elUsername")).sendKeys("bnguyen@shoemetro.com");
//        driver.findElement(By.id("elPassword")).sendKeys("rTrapok)1");
        driver.findElement(By.cssSelector("input[type='submit']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.favorite-tool-icon[data-test='Reporting']")));
        driver.findElement(By.cssSelector("div.favorite-tool-icon[data-test='Reporting']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.x-grid-view")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(report_selector)));
        driver.findElement(By.xpath(report_selector)).click();
        Thread.sleep(WAIT_TIME);
        driver.findElement(By.xpath(report_selector)).click();

        String report_filter = "div.gt-report-data span.iot-operation";
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(report_filter)));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(report_filter)));
        WebElement report_filter_element = driver.findElement(By.cssSelector(report_filter));

        Actions actions = new Actions(driver);
        actions.moveToElement(report_filter_element);
        actions.click();
        for (int i = 0; i < 15; i++) {
            actions.sendKeys(Keys.ARROW_RIGHT);
        }
        for (int i = 0; i < 15; i++) {
            actions.sendKeys(Keys.BACK_SPACE);
        }
        actions.sendKeys("PO.NUMBER > \"16501\" AND PO.NUMBER < \"16991\" ");
        actions.sendKeys(Keys.ENTER);
        actions.build().perform();//done building query

        //now download
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Download CSV']")));
        driver.findElement(By.xpath("//span[text()='Download CSV']")).click();

        Thread.sleep(WAIT_LONG_TIME);
        Thread.sleep(WAIT_LONG_TIME);
//        driver.close();

        //old code
        /*wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[type='password']")));
        Thread.sleep(WAIT_TIME);
        driver.findElement(By.cssSelector("[type='password']")).sendKeys("Shoemetro4");
        driver.findElement(By.id("passwordNext")).click();
        Thread.sleep(WAIT_TIME);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table[role='list']")));
        Thread.sleep(WAIT_TIME);
        driver.findElement(By.xpath("//*[@id='f-ic']/table/tbody/tr[1]/td[1]")).click();
        Thread.sleep(WAIT_TIME);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[role='main']")));
        Thread.sleep(WAIT_TIME);*/


        /*WebElement span_mail_in_mail_list = driver.findElement(By.xpath("//div[@aria-label='Mail list']//span[starts-with(text(),'COG On Hand Appraiser Report')]"));
        span_mail_in_mail_list.click();

        //wait until [id="Item.MessageUniqueBody"] is avail;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id='Item.MessageUniqueBody']")));
        WebElement mail_body = driver.findElement(By.cssSelector("[id='Item.MessageUniqueBody']"));
        String text_please_find = mail_body.getText();

//        String text_please_find = "Please find the COG On Hand Appraiser Report at https://storage.googleapis.com/retailops-public/52522e04e09d7e377b2089215718ba9c/cog_onhand_appraiser-20171211.zip.  Lot/Inventory figures as of: 2017-12-11 23:59:59.";
        System.out.println("Mail body: " + text_please_find);//Please find the COG On Hand Appraiser Report at https://storage.googleapis.com/retailops-public/52522e04e09d7e377b2089215718ba9c/cog_onhand_appraiser-20171211.zip.
        //Lot/Inventory figures as of: 2017-12-11 23:59:59.
        driver.findElement(By.cssSelector("button[title='Archive (E)']")).click();

        Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}");   // the pattern to search for
        Matcher m = p.matcher(text_please_find);

        String date_time = null;
        // if we find a match, get the group
        if (m.find())
        {
            // we're only looking for one group, so get it
            date_time = m.group(0);

            // print the group out for verification
            System.out.format("Found datetime: '%s'\n", date_time);
        }

        p = Pattern.compile("https://storage.googleapis.com/retailops-public/[a-zA-Z0-9]+/cog_onhand_appraiser-[0-9]+\\.zip");   // the pattern to search for
        m = p.matcher(text_please_find);

        String link = null;
        // if we find a match, get the group
        if (m.find())
        {
            // we're only looking for one group, so get it
            link = m.group(0);

            // print the group out for verification
            System.out.format("Found link: '%s'\n", link);
        }

        // This will close window
        driver.close();

        try {
            if (!link.contains("https://storage.googleapis.com/retailops-public")){
                System.exit(0);
            }
        } catch (NullPointerException e){
            System.out.println("Exception" + e.getMessage());
        }

        //now writing to mysql db
        System.out.println(date_time + link);
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            System.out.println("Error cant find jdbc driver");
            System.out.println(ex.getMessage());
            System.exit(0);
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://"  + DB_HOST + "/wh?" +
                    "user=wh&password=jyX@ua9qy");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(0);
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement("REPLACE INTO wh.cogs_file(report_datetime, link) " +
                    "VALUES (?,?) ");
            stmt.setString(1, date_time);
            stmt.setString(2, link);

            rs = stmt.executeQuery();
            System.out.println(rs);
            rs.close();
            // Now do something with the ResultSet ....
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }
        }*/
    }
}

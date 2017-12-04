package google;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
//import org.openqa.selenium.firefox.FirefoxDriver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Login {
//    public static final String DB_HOST = "172.28.1.116";
    public static final String DB_HOST = "127.0.0.1";
    public static final Integer WAIT_TIME = 500;
    public static void main(String[] args) throws InterruptedException {
// Create an object driver for accessing driver method’s
        WebDriver driver = new ChromeDriver();

// navigate() will open URL
        driver.navigate().to("https://groups.google.com/a/shoemetro.com/forum/#!forum/cogs");

        System.out.println("Launching Browser");

// Maximize is used to maximize the window.
        driver.manage().window().maximize();

// using ID of an element to identify element, <u>sendkeys</u> is used to sendtext in field
//        driver.findElement(By.<em>id("email")).sendKeys("username");
//        driver.findElement(By.<em>id("pass")).sendKeys("password");

// click method will click on Login button
        driver.findElement(By.cssSelector("[type='email']")).sendKeys("bnguyen@shoemetro.com");
        driver.findElement(By.id("identifierNext")).click();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        Thread.sleep(WAIT_TIME);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[type='password']")));
        Thread.sleep(WAIT_TIME);
        driver.findElement(By.cssSelector("[type='password']")).sendKeys("Shoemetro4");
        driver.findElement(By.id("passwordNext")).click();
        Thread.sleep(WAIT_TIME);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table[role='list']")));
        Thread.sleep(WAIT_TIME);
        driver.findElement(By.xpath("//*[@id='f-ic']/table/tbody/tr[1]/td[1]")).click();
        Thread.sleep(WAIT_TIME);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[role='main']")));
        Thread.sleep(WAIT_TIME);
        WebElement anchor = driver.findElement(By.cssSelector("div[role='main'] div[dir='ltr'] a"));
        String link  =anchor.getAttribute("href");
        System.out.println(link);//https://storage.googleapis.com/retailops-public/f702f2b07f5a932bf9bed35b9c532003/cog_onhand_appraiser-20170803.zip
        String title = driver.findElement(By.id("t-t")).getText();//2017-08-03 23:59:59
        Pattern p = Pattern.compile("\\d{4}\\-\\d{2}-\\d{2}\\s\\d{2}\\:\\d{2}\\:\\d{2}");   // the pattern to search for
        Matcher m = p.matcher(title);

        String date_time = null;
        // if we find a match, get the group
        if (m.find())
        {
            // we're only looking for one group, so get it
            date_time = m.group(0);

            // print the group out for verification
            System.out.format("Found: '%s'\n", date_time);
        }

// This will close window
        driver.close();

        if (!link.contains("https://storage.googleapis.com/retailops-public")){
            System.exit(0);
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
        }
    }
}
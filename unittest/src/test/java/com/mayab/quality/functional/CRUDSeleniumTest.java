package com.mayab.quality.functional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import org.junit.runners.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class CRUDSeleniumTest {
    private WebDriver driver;
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();
    JavascriptExecutor js;

    @Before
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        baseUrl = "https://mern-crud-mpfr.onrender.com/";
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
        js = (JavascriptExecutor) driver;
    }

    @Test
    public void test1_CreateNewRecord() throws Exception {
        driver.get(baseUrl);
        pause(3000);
        driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/button")).click();
        pause(1000);
        driver.findElement(By.name("name")).sendKeys("Marco Winistörfer");
        driver.findElement(By.name("email")).sendKeys("marco.wini@example.com");
        driver.findElement(By.name("age")).sendKeys("25");
        
        driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[3]/div[2]/div")).click();;
        
        driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[3]/div[2]/div/div[2]/div[1]")).click();
        driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/button")).click();

        pause(3000);
        
        String actualResult = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[4]/div/div")).getText();

        assertTrue(actualResult.contains("Nice one"));
    }
    
    @Test
    public void test2_CreateNewRecord_duplicateEmail() throws Exception {
        driver.get(baseUrl);
        pause(3000);
        driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/button")).click();
        pause(1000);
        driver.findElement(By.name("name")).sendKeys("Marco Algarvo");
        driver.findElement(By.name("email")).sendKeys("marco.wini@example.com");
        driver.findElement(By.name("age")).sendKeys("28");
       
        driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[3]/div[2]/div")).click();;
     
        driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[3]/div[2]/div/div[2]/div[1]")).click();
        driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/button")).click();

        pause(3000);
        
        String actualResult = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[5]/div/div")).getText();

        assertTrue(actualResult.contains("Woah"));
    }
    
    @Test
    public void test3_ModifyAge() throws Exception {
        driver.get(baseUrl);
        pause(3000);

        WebElement userRow = driver.findElement(By.xpath("//td[text()='marco.wini@example.com']/parent::tr"));

        WebElement editButton = userRow.findElement(By.xpath("./td[5]/button[1]")); 
        editButton.click();
        pause(1000);

        WebElement ageField = driver.findElement(By.name("age"));
        ageField.clear();
        ageField.sendKeys("28"); 
        
        WebElement saveButton = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/button"));
        saveButton.click();

        pause(3000);

        String actualResult = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[4]/div/div")).getText();
        assertTrue(actualResult.contains("Nice one"));
    }
        
    @Test
    public void test4_FindSpecificUser() throws Exception {
        driver.get(baseUrl);
        pause(3000);

        String pageText = driver.findElement(By.tagName("body")).getText();

        assertTrue(pageText.contains("marco.wini@example.com"));

        System.out.println("The text 'marco.wini@example.com' was found on the page.");
    }
    
    @Test
    public void test5_SearchAllRecords() throws Exception {
        driver.get(baseUrl);
        pause(3000);

        List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));


        System.out.println("Verifying all records in the table:");
        for (WebElement row : rows) {
            String email = row.findElement(By.xpath("./td[2]")).getText(); 
            
            System.out.println("Email: " + email);
            
            String pageText = driver.findElement(By.tagName("body")).getText();
            
            assertTrue(pageText.contains(email));

            }

        System.out.println("All records in the table were successfully verified.");
    }

    @Test
	public void test6_DeleteRecord() throws Exception {
	    driver.get(baseUrl);
	    pause(3000);
	
	    driver.findElement(By.xpath("//td[text()='marco.wini@example.com']/following-sibling::td[3]/button[2]")).click();
	    pause(1000);
	
	    driver.findElement(By.xpath("//button[text()='Yes']")).click();
	    pause(2000);
	
	    String pageText = driver.findElement(By.tagName("body")).getText();
	
	    assertFalse(pageText.contains("marco.wini@example.com"));
	
	}
    
    private void pause(long mils) {
        try {
            Thread.sleep(mils);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
}

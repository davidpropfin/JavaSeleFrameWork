package Framework;

import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.net.MalformedURLException;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebDriver;

public class WebDriverManager
{
    private WebDriver driver;
    private AppiumDriver appiumDriver;
    AppiumDriver nativeDriver;
    private WebDriverWait wait;
    private int implicitWait;
    private int explicitWait;
    private int timeoutInterval;
    
    public WebDriverManager() {
        this.driver = null;
        this.appiumDriver = null;
        this.nativeDriver = null;
        this.wait = null;
    }
    
    public WebDriver getDriverInstance() {
        if (this.driver != null) {
            return this.driver;
        }
        return (WebDriver)this.appiumDriver;
    }
    
    public void setDriverInstance(final WebDriver dv) {
        this.driver = dv;
    }
    
    public AppiumDriver getAppiumDriverInstance() {
        return this.appiumDriver;
    }
    
    public void startBrowser(final String browser, final int threadDelay, final DesiredCapabilities capabilities) {
        switch (browser) {

            case "chrome": {
                this.startChrome(threadDelay, capabilities);
                break;
            }
            case "firefox": {
                this.startFirefox(threadDelay, capabilities);
                break;
            }
            case "safari": {
                this.startSafari(threadDelay);
                break;
            }
            default: {
                System.out.println("**Error: undefined browser " + browser);
                System.exit(0);
                break;
            }
        }
    }
    
    public void startGrid(final String gridHubURL, final DesiredCapabilities browserType) {
        try {
            this.driver = (WebDriver)new RemoteWebDriver(new URL(gridHubURL), (Capabilities)browserType);
            this.driver = new Augmenter().augment(this.driver);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }    
    public void startAppium(final String deviceURL, final int threadDelay, final DesiredCapabilities capabilities) {
        try {
            this.appiumDriver = (AppiumDriver)new WebDriverCustomIOS(new URL(deviceURL), (Capabilities)capabilities, threadDelay);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    public void startFirefox(final int threadDelay, final DesiredCapabilities capabilities) {
        this.driver = (WebDriver)new WebDriverCustomFirefox(threadDelay, capabilities);
    }
    
    public void startSafari(final int threadDelay) {
        this.driver = (WebDriver)new WebDriverCustomSafari(threadDelay);
    }
    
    public void startChrome(final int threadDelay, final DesiredCapabilities capabilities) {
        ChromeDriverManager.getInstance().setup();
        this.driver = (WebDriver)new WebDriverCustomChrome(threadDelay, capabilities);
    }
    
    public WebDriverWait getWait() {
        return this.wait;
    }
    
    public void setExplicitWait(final int timeout) {
        this.timeoutInterval = timeout;
        if (this.driver != null) {
            this.wait = new WebDriverWait(this.driver, (long)this.timeoutInterval);
        }
        else {
            this.wait = new WebDriverWait((WebDriver)this.appiumDriver, (long)this.timeoutInterval);
        }
    }
    
    public int getImplicitWait() {
        return this.implicitWait;
    }
    
    public int getExplicitWait() {
        return this.timeoutInterval;
    }
    
    public void setImplicitWait(final int wait) {
        this.implicitWait = wait;
        if (this.driver != null) {
            this.driver.manage().timeouts().implicitlyWait((long)this.implicitWait, TimeUnit.SECONDS);
        }
        else {
            this.appiumDriver.manage().timeouts().implicitlyWait((long)this.implicitWait, TimeUnit.SECONDS);
        }
    }
    
    public void closeDriver() {
        if (this.driver != null) {
            this.driver.close();
        }
        else {
            this.appiumDriver.close();
        }
    }
    
    public void quitDriver() {
        if (this.driver != null) {
            this.driver.quit();
            this.driver = null;
        }
        else {
            this.appiumDriver.quit();
            this.appiumDriver = null;
        }
    }
    
    public void screenshot(final String testName, final String filePath) {
        final File srcFile = (File)((TakesScreenshot)this.driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(srcFile, new File(filePath + testName + ".png"));
            System.out.println("***Placed screen shot in " + filePath + testName + " ***");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

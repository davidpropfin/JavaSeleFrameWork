package Framework;

import org.openqa.selenium.WebDriverException;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.client.CookieStore;
import com.google.common.base.Function;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import java.util.Map;
import org.openqa.selenium.firefox.FirefoxProfile;
import java.util.HashMap;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.io.BufferedReader;
import java.io.File;
import java.util.Random;
import java.util.Date;
import org.openqa.selenium.interactions.Actions;

public class Page<T>
{
    protected WebDriverManager WebDriverManager;
    private static String locator;
    private static String displayText;
    private Actions userAction;
    private final String actionClick = "Click ";
    private final String actionSelect = "Select ";
    private final String typeButton = "button";
    private final String typeImage = "img";
    private final String typeForm = "form";
    private final String typeLabel = "label";
    private final String typeLink = "link";
    private final String typeMenu = "menu";
    private final String typeOption = "option";
    private final String typeDropdown = "dropdown";
    private final String typeTextString = "text";
    private final String typeTextField = "textField";
    private final String typeRow = "row";
    private final String typeTab = "tab";
    private final String typeDialog = "dialog";
    private final String displayTab = " Tab\n";
    private final String displayDialog = " Button\n";
    private final String displayButton = " Button\n";
    private final String displayLink = " Link\n";
    private final String displayMenu = " Dropdown\n";
    private final String displayEnter = "Enter ";
    private boolean isRunning;
    private Date seed;
    private Random random;
    protected XMLParser<XMLMapProfiler> appMap;
    protected XMLParser<XMLMapEnv> envMap;
    protected XMLParser<XMLMapSession> sessionMap;
    protected XMLParser<XMLMapLogins> loginsMap;
    
    public Page() {
        this.seed = new Date();
        (this.random = new Random()).setSeed(this.seed.getTime());
    }
    
    public void setAppMap(final File xmlMap) {
        (this.appMap = (XMLParser<XMLMapProfiler>)new XMLParser((Class)XMLMapProfiler.class, xmlMap)).createMapPageData();
    }
    
    public void setAppMap(final BufferedReader xmlMap) {
        (this.appMap = (XMLParser<XMLMapProfiler>)new XMLParser((Class)XMLMapProfiler.class, xmlMap)).createMapPageData();
    }
    
    public void setEnvMap(final File xmlMap) {
        (this.envMap = (XMLParser<XMLMapEnv>)new XMLParser((Class)XMLMapEnv.class, xmlMap)).createMapEnvironment();
    }
    
    public void setEnvMap(final BufferedReader xmlMap) {
        (this.envMap = (XMLParser<XMLMapEnv>)new XMLParser((Class)XMLMapEnv.class, xmlMap)).createMapEnvironment();
    }
    
    public void setSessionMap(final File xmlMap) {
        (this.sessionMap = (XMLParser<XMLMapSession>)new XMLParser((Class)XMLMapSession.class, xmlMap)).createMapSession();
    }
    
    public void setSessionMap(final BufferedReader xmlMap) {
        (this.sessionMap = (XMLParser<XMLMapSession>)new XMLParser((Class)XMLMapSession.class, xmlMap)).createMapSession();
    }
    
    public void setLoginsMap(final File xmlMap) {
        (this.loginsMap = (XMLParser<XMLMapLogins>)new XMLParser((Class)XMLMapLogins.class, xmlMap)).createMapLogins();
    }
    
    public void setLoginsMap(final BufferedReader xmlMap) {
        (this.loginsMap = (XMLParser<XMLMapLogins>)new XMLParser((Class)XMLMapLogins.class, xmlMap)).createMapLogins();
    }
    
    public String getMavenProperty(final String propertyName) {
        return System.getProperty(propertyName);
    }
    
    public WebDriverManager getWebDriver() {
        return this.WebDriverManager;
    }
    
    public int setExecutionDelay() {
        final String delay = System.getProperty("delayMs");
        if (delay == null || delay.equals("ExecutionDelay")) {
            return Integer.parseInt(this.sessionMap.getNameByParentKey("settings", "delayMs"));
        }
        return Integer.parseInt(delay);
    }
    
    public Page<T> setEnv(final String domain, final String xmlProfile) {
        this.setEnv(domain, xmlProfile, false);
        return this;
    }
    
    public Page<T> setEnv(final String domain, final String xmlProfile, final boolean override) {
        final String runEnv = this.getMavenProperty("testEnv");
        System.out.print("Environment:\n\t");
        if (runEnv == null || runEnv.equals("SeleniumTestEnv") || override) {
            this.envMap.setDomain(domain);
            System.out.println("Domain:" + this.envMap.getDomain());
        }
        else {
            System.out.println(runEnv.toUpperCase());
            this.envMap.setDomain(runEnv);
        }
        return this;
    }
    
    public Page<T> setDriver(final String appDriver) {
        DesiredCapabilities capabilities = null;
        final String lowerCase = appDriver.toLowerCase();
        switch (lowerCase) {
            case "ie":
            case "ie32": {
                capabilities = DesiredCapabilities.internetExplorer();
                capabilities.setCapability("ignoreProtectedModeSettings", true);
                break;
            }
            case "chrome": {
                final ChromeOptions chrome_options = new ChromeOptions();
                chrome_options.addArguments(new String[] { "--disable-notifications" });
                chrome_options.addArguments(new String[] { "--disable-infobars" });
                chrome_options.addArguments(new String[] { "--disable-extensions" });
                chrome_options.addArguments(new String[] { "--disable-web-security" });
                chrome_options.addArguments(new String[] { "--no-proxy-server" });
                chrome_options.addArguments(new String[] { "--enable-automation" });
                final Map<String, Object> prefs = new HashMap<String, Object>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                chrome_options.setExperimentalOption("prefs", (Object)prefs);
                capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability("chromeOptions", (Object)chrome_options);
                capabilities.setCapability("ensureCleanSession", true);
                capabilities.setCapability("javascriptEnabled", true);
                break;
            }
            case "firefox": {
                if (this.sessionMap.getNameByParentKey("settings", "browserPath") != null && !this.sessionMap.getNameByParentKey("settings", "browserPath").isEmpty()) {
                    System.setProperty("webdriver.firefox.bin", this.sessionMap.getNameByParentKey("settings", "browserPath"));
                }
                final FirefoxProfile profile = new FirefoxProfile();
                profile.setPreference("browser.download.folderList", 2);
                profile.setPreference("browser.download.manager.showWhenStarting", false);
                profile.setPreference("browser.download.dir", "~/Downloads");
                profile.setPreference("browser.helperApps.alwaysAsk.force", false);
                profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/plain,text/csv,application/pdf,application/x-pdf,application/x-csv,application/binary,binary/octet-stream,application/octet-stream");
                profile.setPreference("plugin.disable_full_page_plugin_for_types", "application/pdf");
                profile.setPreference("pdfjs.disabled", true);
                profile.setPreference("plugin.scan.Acrobat", "99.0");
                profile.setPreference("plugin.scan.plid.all", false);
                capabilities = DesiredCapabilities.firefox();
                capabilities.setCapability("firefox_profile", (Object)profile);
                break;
            }
            case "safari": {
                capabilities = DesiredCapabilities.safari();
                break;
            }
            case "ios": {
                capabilities = new DesiredCapabilities();
                capabilities.setCapability("deviceName", "iPad 2");
                capabilities.setCapability("browserName", "safari");
                capabilities.setCapability("platformVersion", "9.3");
                capabilities.setCapability("platformName", "iOS");
                capabilities.setCapability("safariAllowPopups", "true");
                capabilities.setCapability("unexpectedAlertBehaviour", "accept");
                capabilities.setCapability("nativeWebTap", "true");
                capabilities.setCapability("app", "/Users/cathy.braun/Documents/workspace/appiumTest/AppiumTest/src/CDLiOSTest-O-Matic.app");
                break;
            }
            default: {
                System.out.println("**Error: Browser type undefined " + appDriver.toLowerCase());
                System.exit(0);
                break;
            }
        }
        System.out.println("Browser: " + appDriver.toUpperCase());
        if (this.WebDriverManager.getDriverInstance() == null) {
            if (this.getSessionMap("SeleniumGrid", "grid").equals("enable")) {
                this.WebDriverManager.startGrid(this.sessionMap.getNameByParentKey("SeleniumGrid", "gridHub"), capabilities);
            }
            else if (this.getSessionMap("settings", "browser").toLowerCase().equals("ios")) {
                this.WebDriverManager.startAppium(this.getSessionMap("MobileDevice", this.getSessionMap("MobileDevice", "device")), this.setExecutionDelay(), capabilities);
            }
            else {
                this.WebDriverManager.startBrowser(appDriver.toLowerCase(), this.setExecutionDelay(), capabilities);
            }
            this.setDefaultWait();
        }
        this.userAction = new Actions(this.WebDriverManager.getDriverInstance());
        return this;
    }
    
    public void setDefaultWait() {
        this.setImplicitWaitDefault();
        this.setExplicitWaitDefault();
    }
    
    public void setImplicitWaitDefault() {
        this.setImplicitWait(Integer.parseInt(this.getSessionMap("settings", "implicitWait")));
    }
    
    public void setExplicitWaitDefault() {
        this.WebDriverManager.setExplicitWait(Integer.parseInt(this.getSessionMap("settings", "explicitWait")));
    }
    
    public void setExplicitWait(final int timeout) {
        this.WebDriverManager.setExplicitWait(timeout);
    }
    
    public void setImplicitWait(final int timeout) {
        this.WebDriverManager.setImplicitWait(timeout);
    }
    
    private Object executeJS(final String script, final String id) {
        final JavascriptExecutor js = (JavascriptExecutor)this.getWebDriver().getDriverInstance();
        final WebElement element = this.getElement(id, false);
        return js.executeScript(script, new Object[] { element });
    }
    
    private Object executeJS(final String script) {
        final JavascriptExecutor js = (JavascriptExecutor)this.getWebDriver().getDriverInstance();
        final Object scope = js.executeScript(script, new Object[0]);
        return scope;
    }
    
    public void JSExec() {
        final JavascriptExecutor js = (JavascriptExecutor)this.WebDriverManager.getDriverInstance();
        final RemoteWebElement whatisthis = (RemoteWebElement)js.executeScript("return document.getElementById('showDropdownMenu');", new Object[0]);
        System.out.println("Whatisthisid: " + whatisthis.getId());
        final WebElement element = this.getElement("navbar.{Menu}");
        js.executeScript("var element = arguments[0];if(document.createEvent){var mouseEventObj = document.createEvent('MouseEvents');mouseEventObj.initEvent('mouseover',true,false);element.dispatchEvent(mouseEventObj);}else if(document.createEventObject){elem.fireEvent('onmouseover');}", new Object[] { element });
    }
    
    public Page<T> setResolution(final String width, final String height) {
        if (width.toLowerCase().equals("max") || height.toLowerCase().equals("max")) {
            this.WebDriverManager.getDriverInstance().manage().window().maximize();
        }
        else if (!this.getSessionMap("settings", "browser").toLowerCase().equals("ios")) {
            this.WebDriverManager.getDriverInstance().manage().window().setSize(new Dimension(Integer.parseInt(width), Integer.parseInt(height)));
        }
        System.out.println("\tScreen resolution: " + this.WebDriverManager.getDriverInstance().manage().window().getSize());
        return this;
    }
    
    public Page<T> startApp(final String appName) {
        final String url = this.envMap.getId(appName);
        System.out.println("\tStarting Application: " + url + "\n");
        this.WebDriverManager.getDriverInstance().get(url);
        return this;
    }
    
    public String getEnvDomain() {
        return this.envMap.getDomain();
    }
    
    public String getEnvURL(final String env, final String app) {
        return this.envMap.getId(env, app);
    }
    
    public String getEnvMap(final String key, final String value) {
        try {
            this.envMap.getId(key, value).isEmpty();
        }
        catch (Exception e) {
            System.out.println("***Fatal Framework Error: Env attribute " + key + ":" + value + " not found***");
            System.out.println("***Test Exit***");
            this.closeApp();
        }
        return this.envMap.getId(key, value);
    }
    
    public String getLoginsMap(final String key, final String value) {
        try {
            this.loginsMap.getNameByParentKey(key, value).isEmpty();
        }
        catch (Exception e) {
            System.out.println("***Fatal Framework Error: Login attribute " + key + ":" + value + " not found***");
            System.out.println("***Test Exit***");
            this.closeApp();
        }
        return this.loginsMap.getNameByParentKey(key, value);
    }
    
    public String getSessionMap(final String key, final String attribute) {
        try {
            this.sessionMap.getNameByParentKey(key, attribute).isEmpty();
        }
        catch (Exception e) {
            System.out.println("***Fatal Framework Error: Session " + key + ":" + attribute + " not found***");
            System.out.println("***Test Exit***");
            this.closeApp();
        }
        return this.sessionMap.getNameByParentKey(key, attribute);
    }
    
    public String getSessionSettingAttribute(final String attribute) {
        return this.getSessionMap("settings", attribute);
    }
    
    public String getSessionUser(final String login) {
        return this.getLoginsMap(this.getSessionSettingAttribute(login), "username");
    }
    
    public String getSessionUserPassword(final String login) {
        return this.getLoginsMap(this.getSessionSettingAttribute(login), "password");
    }
    
    public String getSessionExpConfigPath() {
        return this.getSessionMap(this.getSessionSettingAttribute("OS"), "expConfig");
    }
    
    public String getSessionTestDataPath() {
        return this.getSessionMap(this.getSessionSettingAttribute("OS"), "testdata");
    }
    
    public String getHighlight() {
        return this.getSessionSettingAttribute("highlight");
    }
    
    public Page<T> startEnv() {
        if (this.WebDriverManager == null) {
            this.WebDriverManager = new WebDriverManager();
        }
        if (this.WebDriverManager.getDriverInstance() == null) {
            final String param = System.getProperty("browser");
            if (param != null && !param.toLowerCase().equals("browser")) {
                this.setDriver(param);
            }
            else {
                this.setDriver(this.getSessionMap("settings", "browser"));
            }
        }
        return this;
    }
    
    public Page<T> startTest(final String testname) {
        this.startTest(testname, this.getSessionSettingAttribute("environmentId"), this.getSessionSettingAttribute("profile"), this.getSessionSettingAttribute("screenWidth"), this.getSessionSettingAttribute("screenHeight"), this.getSessionSettingAttribute("application"));
        return this;
    }
    
    public Page<T> startTest(final String testname, final String env, final String profile, final String width, final String height, final String app) {
        System.out.println("TestCase:\n\t" + testname);
        this.setEnv(env, profile);
        this.setResolution(width, height);
        this.startApp(app);
        return this;
    }
    
    public Page<T> screenCapture(final String fileName) {
        final String filePath = "./screenshots/";
        this.WebDriverManager.screenshot(fileName, filePath);
        return this;
    }
    
    public boolean isApplicationRunning() {
        boolean status = false;
        if (this.WebDriverManager.getDriverInstance().findElements(By.xpath(this.appMap.getLocator("login.{Username}", Page.locator))).size() > 0) {
            status = true;
        }
        this.isRunning = status;
        this.WebDriverManager.setImplicitWait(Integer.parseInt(this.sessionMap.getNameByParentKey("settings", "implicitWait")));
        return this.isRunning;
    }
    
    public void closeApp() {
        if (this.WebDriverManager.getDriverInstance() != null) {
            System.out.println("Closing application");
            this.WebDriverManager.getDriverInstance().quit();
            this.WebDriverManager.setDriverInstance((WebDriver)null);
            System.gc();
            System.runFinalization();
        }
    }
    
    public void waitUntil(final String xpath) {
        this.WebDriverManager.getWait().until((Function)ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }
    
    public CookieStore getSessionCookie() {
        final Set<Cookie> seleniumCookies = (Set<Cookie>)this.WebDriverManager.getDriverInstance().manage().getCookies();
        final BasicCookieStore cookieStore = new BasicCookieStore();
        System.out.println(seleniumCookies);
        for (final Cookie seleniumCookie : seleniumCookies) {
            System.out.println(seleniumCookie.getName() + " " + seleniumCookie.getValue());
            System.out.println(seleniumCookie.getDomain());
            final BasicClientCookie basicClientCookie = new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
            basicClientCookie.setDomain("." + seleniumCookie.getDomain());
            basicClientCookie.setPath(seleniumCookie.getPath());
            basicClientCookie.setAttribute("domain", "true");
            cookieStore.addCookie((org.apache.http.cookie.Cookie)basicClientCookie);
        }
        return (CookieStore)cookieStore;
    }
    
    public String getElementDisplayText(final String key) {
        return this.appMap.getLocator(key, Page.displayText);
    }
    
    public void displayText(final String key, final boolean outputEnable) {
        String displayText = this.getElementDisplayText(key);
        if (outputEnable) {
            if (displayText == null || displayText == "") {
                displayText = "";
            }
            else if (!this.envMap.getDomain().equals("CIT")) {
                System.out.print(this.timeStamp() + " ");
            }
            System.out.print(displayText.trim() + this.getElementType(key));
        }
    }
    
    private void highlightElement(final WebElement element) {
        if (this.getHighlight().equals("enable")) {
            final JavascriptExecutor js = (JavascriptExecutor)this.WebDriverManager.getDriverInstance();
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", new Object[] { element, "color:black; border: 1px solid green; background-color:#00FF00;" });
        }
    }
    
    public By getSeleniumBy(final String key) {
        return By.xpath(this.appMap.getLocator(key, Page.locator));
    }
    
    public WebElement getElement(final String key) {
        return this.getElement(key, true);
    }
    
    public WebElement getElement(final String key, final boolean outputEnable) {
        this.displayText(key, outputEnable);
        final WebElement element = this.WebDriverManager.getDriverInstance().findElement(this.getSeleniumBy(key));
        this.highlightElement(element);
        return element;
    }
    
    public WebElement getElementByXpath(final String xpath) {
        final WebElement element = this.WebDriverManager.getDriverInstance().findElement(By.xpath(xpath));
        this.highlightElement(element);
        return element;
    }
    
    public List<WebElement> getElementsByXpath(final String xpath) {
        return (List<WebElement>)this.WebDriverManager.getDriverInstance().findElements(By.xpath(xpath));
    }
    
    public boolean getElementNotExists(final String xpath) {
        try {
            return (boolean)this.WebDriverManager.getWait().until((Function)ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean getElementExists(final String xpath) {
        boolean exists = false;
        try {
            this.getWebDriver().getWait().until((Function)ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            exists = true;
        }
        catch (Exception e) {
            System.out.println("In Catch");
        }
        return exists;
    }
    
    private String getElementType(final String key) {
        final String type = this.appMap.getLocator(key, "type");
        String actionText = null;
        String typeText = " ";
        final String s = type;
        switch (s) {
            case "button":
            case "img": {
                actionText = "Click ";
                typeText = " Button\n";
                break;
            }
            case "form": {
                actionText = "";
                break;
            }
            case "label": {
                actionText = "";
                break;
            }
            case "link": {
                actionText = "Click ";
                typeText = " Link\n";
                break;
            }
            case "menu": {
                actionText = "Select ";
                typeText = " Dropdown\n";
                break;
            }
            case "option":
            case "dropdown": {
                actionText = "Select ";
                break;
            }
            case "textField":
            case "text": {
                actionText = "Enter ";
                break;
            }
            case "row": {
                break;
            }
            case "tab": {
                actionText = "Click ";
                typeText = " Tab\n";
                break;
            }
            case "dialog": {
                actionText = "Click ";
                typeText = " Button\n";
                break;
            }
            default: {
                actionText = "";
                typeText = " ";
                break;
            }
        }
        if (!actionText.isEmpty()) {
            System.out.print(actionText);
        }
        return typeText;
    }
    
    public String getXpath(final String key) {
        return this.appMap.getLocator(key, Page.locator);
    }
    
    public String RandomSet(final int minLength, final int maxLength, final String charSet) {
        final int randLength = this.random.nextInt(maxLength - minLength + 1) + minLength;
        final StringBuilder sb = new StringBuilder(maxLength);
        for (int i = 0; i < randLength; ++i) {
            sb.append(charSet.charAt(this.random.nextInt(charSet.length())));
        }
        return sb.toString();
    }
    
    public String RandomString(final int maxlength) {
        return RandomStringUtils.randomAlphanumeric(maxlength);
    }
    
    public int RandomNumeric(final int size) {
        int randnum = Integer.parseInt(RandomStringUtils.randomNumeric(size));
        if (randnum == 0) {
            randnum = 1;
        }
        return randnum;
    }
    
    public int RandomNumericMaxLength(final int maxLength) {
        final int randLength = this.random.nextInt(maxLength + 1);
        return randLength;
    }
    
    public String RandomNameUpTo(final int maxlength) {
        return RandomStringUtils.randomAlphanumeric(this.random.nextInt(maxlength) + 1);
    }
    
    public List<String> CreateListRandom(final int numToCreate, final int minStringLength, final int maxStringLength, final String charset) {
        final List<String> stringList = new ArrayList<String>();
        for (int i = 0; i <= numToCreate; ++i) {
            stringList.add(this.RandomSet(minStringLength, maxStringLength, charset));
        }
        return stringList;
    }
    
    public String RandomEmail(final int maxlength) {
        return RandomStringUtils.randomAlphanumeric(this.random.nextInt(maxlength) + 1) + "@" + RandomStringUtils.randomAlphanumeric(this.random.nextInt(maxlength) + 1) + "." + RandomStringUtils.randomAlphanumeric(3);
    }
    
    public String timeStamp() {
        final Date date = new Date();
        final Timestamp now = new Timestamp(date.getTime());
        final String time = new SimpleDateFormat("HH:mm:ss.SSS").format(now);
        return time;
    }
    
    public void polling(final int interval) {
        try {
            TimeUnit.SECONDS.sleep(interval);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void assertClickByXpath(final String idXpath, final String idAssertXpath) {
        boolean exit = false;
        int retry = 1;
        final int max_retry = 2;
        while (!exit && retry <= max_retry) {
            try {
                this.getElementByXpath(idXpath).click();
                if (!this.getElementExists(idAssertXpath)) {
                    continue;
                }
                exit = true;
            }
            catch (WebDriverException e) {
                System.out.println("**Assert failed: retry #" + retry + " of " + max_retry);
                this.polling(2);
                ++retry;
            }
        }
    }
    
    public void assertClick(final String id, final String idAssert) {
        this.displayText(id, true);
        this.assertClickByXpath(this.getXpath(id), this.getXpath(idAssert));
    }
    
    public void assertWait(final String xpath) {
        this.assertWait(xpath, true);
    }
    
    public void assertWait(final String xpath, final boolean exists) {
        this.assertWait(xpath, exists, 300);
    }
    
    public void assertWait(final String xpath, final boolean exists, final int timeoutOverride) {
        final long start = System.currentTimeMillis();
        final long end = start + timeoutOverride * 1000;
        boolean state = false;
        this.setImplicitWait(30);
        while (!state && System.currentTimeMillis() < end) {
            if (exists) {
                state = this.getElementExists(xpath);
            }
            else {
                state = !this.getElementExists(xpath);
            }
            if (!state) {
                this.polling(1);
            }
        }
        this.setImplicitWaitDefault();
    }
    
    static {
        Page.locator = "locator";
        Page.displayText = "displayText";
    }
}

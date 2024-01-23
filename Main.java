import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.awt.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Scanner scan = new Scanner(System.in);
    static ArrayList<WebDriver> webDrivers = new ArrayList<>();
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.drive","../chromedriver.exe");


        // TODO
        //--------------------------------------------------------------------------------
        String STUDENT_ID = "400100100";
        String EDU_PASS = "EduPassword";
        boolean type = true; //True means choosing the unit at the beginning of the rand and false means waiting for the capacity of the course
        //--------------------------------------------------------------------------------
        if(type){

            ArrayList<Integer> registerCoursesIndex = new ArrayList<>();
            ArrayList<Integer> deleteCoursesIndex = new ArrayList<>();
            ArrayList<Integer> changeCoursesIndex = new ArrayList<>();
            ArrayList<Integer> changeToIndex = new ArrayList<>();

            // TODO
            //--------------------------------------------------------------------------------
            LocalTime startTime = LocalTime.of(9,0,0);
            // Please enter the courses you want me to take or change the group or delete:
            registerCoursesIndex.add(2);
            registerCoursesIndex.add(3);
            deleteCoursesIndex.add(4);
            deleteCoursesIndex.add(7);
            changeCoursesIndex.add(6); // Don't forget to fill in the index of the group to be changed to
            changeToIndex.add(2);
            //--------------------------------------------------------------------------------

            int n = registerCoursesIndex.size();
            int m = deleteCoursesIndex.size();
            int k = changeCoursesIndex.size();

            ArrayList<WebElement> registerCoursesButton = new ArrayList<>();
            ArrayList<WebElement> deleteCoursesButton = new ArrayList<>();
            ArrayList<WebElement> changeCoursesButton = new ArrayList<>();


            int temp;
            for (int i = 0; i < n; i++) {
                webDrivers.add(logIn(STUDENT_ID,EDU_PASS));
                temp = registerCoursesIndex.get(i);
                openPlus(i,temp);
                registerCoursesButton.add(registerCourse(i));
            }
            for (int i = n; i < n+m ; i++) {
                webDrivers.add(logIn(STUDENT_ID,EDU_PASS));
                temp = deleteCoursesIndex.get(i-n);
                openPlus(i,temp);
                deleteCoursesButton.add(deleteCourse(i));
            }
            for (int i = n+m; i < n+m+k ; i++) {
                webDrivers.add(logIn(STUDENT_ID,EDU_PASS));
                temp = changeCoursesIndex.get(i-n-m);
                openPlus(i,temp);
                changeCoursesButton.add(changeGroup(i,changeToIndex.get(i-n-m)));
            }

            WebDriver clockDriver = new ChromeDriver();
            clockDriver.manage().window().maximize();
            clockDriver.get("http://edu.sharif.edu");

            WebElement username = waitToFindByName(clockDriver,"username",100,25);
            WebElement password = clockDriver.findElement(By.name("password"));
            username.sendKeys(STUDENT_ID);
            password.sendKeys(EDU_PASS);

            WebElement logButton = clockDriver.findElement(By.xpath("//*[@id='loginform']/div/form/div[3]/button"));
            logButton.click();

            clockDriver.navigate().refresh();
            WebElement timeElement = waitToFind(clockDriver,"//*[@id='currentClock']",100,25);

            String lastCheck = timeElement.getAttribute("innerHTML");

            while (lastCheck.equals(timeElement.getAttribute("innerHTML"))){
                timeElement = clockDriver.findElement(By.xpath("//*[@id='currentClock']"));
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");
            LocalTime nowTime = LocalTime.parse(timeElement.getAttribute("innerHTML"), formatter);

            clockDriver.close();

            long diff = nowTime.until(startTime, ChronoUnit.SECONDS);
            System.out.println(diff);
            try {
                Thread.sleep(diff*1000);
            } catch (InterruptedException e) {
                System.out.println("لطفا خودت انتخاب واحد بکن! من نتونستم درست کار کنم :(");
            }

            for (int i = 0; i < n; i++) {
                registerCoursesButton.get(i).click();
            }
            for (int i = n; i < n+m ; i++) {
                deleteCoursesButton.get(i-n).click();
            }
            for (int i = n+m; i < n+m+k ; i++) {
                changeCoursesButton.get(i-n-m).click();
                // An example to wait until the deletion popup closes (on behalf of placing the deletion request in the queue of myedu scripts):
                // WebDriverWait wait = new WebDriverWait(webDrivers.get(i),Duration.ofSeconds(5));
                // wait.until(ExpectedConditions.invisibilityOf(changeCoursesButton.get(i-n-m)));
            }
            for (WebDriver webDriver : webDrivers) webDriver.close();
        } else {
            WebDriver webDriver = logIn(STUDENT_ID,EDU_PASS);
            ArrayList<Integer> courses = new ArrayList<>();
            // TODO
            //--------------------------------------------------------------------------------
            // Please enter the courses you want me to wait for:
            courses.add(2);
            courses.add(3);
            courses.add(4);
            courses.add(5);
            //--------------------------------------------------------------------------------
            int n = courses.size();
            while (true){
                for (int i = 0; i < n; i++) {
                    WebElement colorBara = waitToFind(webDriver,"//*[@id='root']/div/div[2]/table/tbody/tr["+
                            n+"]/td[8]",100,25);
                    String text = colorBara.getAttribute("innerHTML");
                    if(text.contains("blue")||text.contains("yellow")){
                        WebElement plusButton = webDriver.findElement(By.xpath(
                                "//*[@id='root']/div/div[2]/table/tbody/tr[" + n
                                        + "]/td[1]/button[1]"));
                        plusButton.click();

                        WebElement registerButton = waitToFind(webDriver,"//button[@class='ui icon positive right labeled button']"
                                ,100,25);
                        registerButton.click();
                        System.out.println("I applied for the course with index "+n+". Please check it yourself and then run me again");
                        while (true){
                            Toolkit.getDefaultToolkit().beep();
                        }
                    }
                }
            }
        }
    }

    public static WebDriver logIn(String user, String pass){
        Scanner scanner = Main.scan;
        WebDriver webDriver = new ChromeDriver();
        webDriver.manage().window().maximize();
        webDriver.get("http://my.edu.sharif.edu");

        WebElement usernameM = waitToFindByName(webDriver,"username",100,25);
        WebElement passwordM = webDriver.findElement(By.name("password"));
        usernameM.sendKeys(user);
        passwordM.sendKeys(pass);

        WebElement securityCode = webDriver.findElement(By.name("securityCode"));

        String code = scanner.nextLine();
        securityCode.sendKeys(code);

        WebElement logButton = webDriver.findElement(By.xpath("//button[@class='ui large fluid primary button']"));
        logButton.click();

        WebElement markButton = waitToFind(webDriver,"//a[@href='/courses/marked']",100,25);
        markButton.click();
        webDriver.navigate().refresh();
        return webDriver;
    }



    public static void openPlus(int index,int CN){
        WebElement plusButton = waitToFind(Main.webDrivers.get(index),"//*[@id='root']/div/div[2]/table/tbody/tr["
                + CN + "]/td[1]/button[1]",100,25);
        plusButton.click();
    }

    public static WebElement registerCourse(int index){
        return waitToFind(Main.webDrivers.get(index),"//button[@class='ui icon positive right labeled button']",100,25);
    }

    public static WebElement deleteCourse(int index){
        return waitToFind(Main.webDrivers.get(index),"//button[@class='ui icon negative right labeled button']",100,25);
    }

    public static WebElement changeGroup(int index, int changeC){
        WebDriver webDriver = Main.webDrivers.get(index);
        WebElement changeRol = waitToFind(webDriver,"//div[@role='listbox']",100,25);
        changeRol.click();

        WebElement selectCourse = waitToFind(webDriver,"/html/body/div[2]/div/div[2" +
                "]/table/tbody/tr[4]/td[2]/div/div[2]/div["+ changeC +"]",10,25);
        selectCourse.click();

        return webDriver.findElement(By.xpath("//button[@class='ui icon primary right labeled button']"));
    }
    public static WebElement waitToFind(WebDriver webDriver, String elementXpath, int timeOut, int pollTime){ // find by xpath
        Wait<WebDriver> wait = new FluentWait<>(webDriver).withTimeout(Duration.ofSeconds(timeOut))
                .pollingEvery(Duration.ofMillis(pollTime)).ignoring(org.openqa.selenium.NoSuchElementException.class);

        return wait.until(webDriver1 -> webDriver1.findElement(By.xpath(elementXpath)));
    }
    public static WebElement waitToFindByName(WebDriver webDriver, String elementName, int timeOut, int pollTime){ // find by xpath
        Wait<WebDriver> wait = new FluentWait<>(webDriver).withTimeout(Duration.ofSeconds(timeOut))
                .pollingEvery(Duration.ofMillis(pollTime)).ignoring(org.openqa.selenium.NoSuchElementException.class);

        return wait.until(webDriver1 -> webDriver1.findElement(By.name(elementName)));
    }
}
package in.reqres;

import static io.restassured.RestAssured.given;
import static specification.Specification.*;

import data.*;
import helpers.Parameters;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import helpers.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class APITest {

  @Test
  public void takeAllUsersFromPageThenUsersAvatarsNamesDifferent() {
    UsersResponse usersResponse = given()
        .spec(requestSpec())
        .when()
        .get("/api/users?page=2")
        .then()
        .log().body()
        .spec(responseSpec())
        .extract().body().as(UsersResponse.class);

    List<User> users = usersResponse.getData();
    List<String> avatarNames = getFileNames(
        users.stream().map(User::getAvatar).collect(
            Collectors.toList()));
    int uniqueAvatarNamesCount = (int) avatarNames.stream().distinct().count();
    Assert.assertEquals(uniqueAvatarNamesCount, avatarNames.size(),
        "Список пользователей содержит одинаковые имена файлов: " + avatarNames);
  }

  @Test(dataProvider = "loginCorrectData", dataProviderClass = Parameters.class)
  public void loginWithCorrectDataThenCorrectToken(String email, String password, String token) {
    Account account = new Account(email, password);
    Response response = given()
        .spec(requestSpec())
        .body(account)
        .when()
        .post("/api/login")
        .then()
        .log().all()
        .spec(responseSpec())
        .extract().response();
    JsonPath jsonPath = response.jsonPath();
    String realToken = jsonPath.getString("token");

    Assert.assertEquals(realToken, token,
        "При входе в аккаунт с корректными данными, ожидаемый token: " + token + ". А в ответе: "
            + realToken);
  }

  @Test(dataProvider = "loginWithoutPassword", dataProviderClass = Parameters.class)
  public void loginWithoutPasswordThenError(String email, String error) {
    Account account = new Account(email);
    Response response = given()
        .spec(requestSpec())
        .body(account)
        .when()
        .post("/api/login")
        .then()
        .log().all()
        .statusCode(400)
        .extract().response();
    JsonPath jsonPath = response.jsonPath();
    String curError = jsonPath.getString("error");
    Assert.assertEquals(curError, error,
        "При входе в личный кабинет без пароля получаем ошибку: " + curError + ". Ожидаем: "
            + error);
  }

  @Test
  public void getListColorInfoThenDataYearsSorted() {
    ColorInfosResponse colorInfosResponse = given()
        .spec(requestSpec())
        .when()
        .get("/api/unknown")
        .then()
        .log().all()
        .spec(responseSpec())
        .extract().body().as(ColorInfosResponse.class);

    List<Integer> yearsList = colorInfosResponse.getData().stream()
        .map(ColorInfo::getYear).collect(Collectors.toList());
    List<Integer> sortedYearsList = getSortedList(yearsList);
    Assert.assertEquals(yearsList, sortedYearsList,
        "Данные получены не в отсортированном порядке по годам");
  }

  @Test(dataProvider = "expectedTagsCount", dataProviderClass = Parameters.class)
  public void getXMLDataThenEqualCountTags(int expectedTagsCount) {
    Response response = given()
        .when()
        .get("https://gateway.autodns.com/")
        .then()
        .log().body()
        .extract().response();

    Document document = getDocumentFromString(response.asString());
    XPath xPath = getXPath();
    int actualTagsCount = getTagsCount(document, xPath);

    Assert.assertEquals(actualTagsCount, expectedTagsCount,
        "Количество тегов в файле: " + actualTagsCount + " отличаается от ожидаемого: "
            + expectedTagsCount);
  }

  /**
   * Метод находит названия файлов из url
   *
   * @param allData - полный url
   * @return список названий файлов
   */
  private static List<String> getFileNames(List<String> allData) {
    List<String> fileNames = new ArrayList<>();
    for (String str : allData) {
      String[] splitStr = str.split("/");
      String curFileName = splitStr[splitStr.length - 1];
      fileNames.add(curFileName);
    }
    return fileNames;
  }

  /**
   * Метод подсчитывает все теги документа при помощи XPath
   */
  private static int getTagsCount(Document document, XPath xPath) {
    try {
      return ((Number) xPath.evaluate("count(//*)", document,
          XPathConstants.NUMBER)).intValue();
    } catch (XPathExpressionException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Метод получающий Document из строки
   */
  private static Document getDocumentFromString(String xmlData) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
    try {
      return builder.parse(new InputSource(new StringReader(xmlData)));
    } catch (SAXException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Метод генерирующий XPath
   */
  private static XPath getXPath() {
    XPathFactory xPathFactory = XPathFactory.newInstance();
    return xPathFactory.newXPath();
  }

  /**
   * Метод копирует List, и сортирует его
   *
   * @param original - оригинальный список
   * @return - отсортированная копия списка
   */
  private static List<Integer> getSortedList(List<Integer> original) {
    List<Integer> sortedList = new ArrayList<>(original);
    Collections.sort(sortedList);
    return sortedList;
  }
}

package in.reqres;

import static io.restassured.RestAssured.given;
import static specification.Specification.*;

import data.Account;
import data.Resource;
import data.User;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class APITest {

  @Test
  public void takeAllUsersFromPageThenUsersAvatarsNamesDifferent() {
    Resource resource = given()
        .spec(requestSpec())
        .when()
        .get("/api/users?page=2")
        .then()
        .log().body()
        .spec(responseSpec())
        .extract().body().as(Resource.class);

    List<String> avatarNames = getFileNames(
        resource.getData().stream().map(User::getAvatar).collect(
            Collectors.toList()));
    int uniqueAvatarNamesCount = (int) avatarNames.stream().distinct().count();
    Assert.assertEquals(uniqueAvatarNamesCount, avatarNames.size(),
        "Список пользователей содержит одинаковые имена файлов: " + avatarNames);
  }

  @Test(dataProvider = "loginCorrectData")
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

  @Test(dataProvider = "loginWithoutPassword")
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

  @DataProvider
  public Object[][] loginCorrectData() {
    return new Object[][]{{"eve.holt@reqres.in", "cityslicka", "QpwL5tke4Pnpja7X4"}};
  }

  @DataProvider
  public Object[][] loginWithoutPassword() {
    return new Object[][]{{"peter@klaven", "Missing password"}};
  }
}

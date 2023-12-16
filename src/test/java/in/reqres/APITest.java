package in.reqres;

import static io.restassured.RestAssured.given;

import data.Resource;
import data.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.Test;

public class APITest {

  @Test
  public void takeAllUsersFromPageThenUsersAvatarsNamesDifferent() {
    Resource resource = given()
        .when()
        .get("https://reqres.in/api/users?page=2")
        .then()
        .log().body()
        .extract().body().as(Resource.class);

    List<String> avatarNames = getFileNames(
        resource.getData().stream().map(User::getAvatar).collect(
            Collectors.toList()));
    int uniqueAvatarNamesCount = (int) avatarNames.stream().distinct().count();
    Assert.assertEquals(uniqueAvatarNamesCount, avatarNames.size(),
        "Список пользователей содержит одинаковые имена файлов: " + avatarNames);
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
}

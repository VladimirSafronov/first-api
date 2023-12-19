package helpers;

import org.testng.annotations.DataProvider;

/**
 * Класс, используемый для параметризации тестов (DataProvider)
 */
public class Parameters {

  @DataProvider
  public static Object[][] loginCorrectData() {
    return new Object[][]{{"eve.holt@reqres.in", "cityslicka", "QpwL5tke4Pnpja7X4"}};
  }

  @DataProvider
  public static Object[][] loginWithoutPassword() {
    return new Object[][]{{"peter@klaven", "Missing password"}};
  }

  @DataProvider
  public static Object[][] expectedTagsCount() {
    return new Object[][]{{14}};
  }
}

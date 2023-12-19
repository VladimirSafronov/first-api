package helpers;

import java.util.Collection;

/**
 * Данный класс переопределяет классический Assert чтобы assert отображался в allure всегда (не
 * только, когда тест упадет)
 */
public class Assert {

  public static void assertEquals(int actual, int expected, String message) {
    org.testng.Assert.assertEquals(actual, expected, message);
  }

  public static void assertEquals(String actual, String expected, String message) {
    org.testng.Assert.assertEquals(actual, expected, message);
  }

  public static void assertEquals(Collection<?> actual, Collection<?> expected, String message) {
    org.testng.Assert.assertEquals(actual, expected, message);
  }
}

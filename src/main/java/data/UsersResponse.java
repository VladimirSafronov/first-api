package data;

import java.util.List;

/**
 * Дата-класс содержащий список пользователей
 */
public class UsersResponse extends ResourcesResponse {

  private List<User> data;

  public List<User> getData() {
    return data;
  }

  public void setData(List<User> data) {
    this.data = data;
  }
}

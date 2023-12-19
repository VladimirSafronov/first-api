package data;

import java.util.List;

/**
 * Дата-класс содержащий список информации о цветах
 */
public class ColorInfosResponse extends ResourcesResponse {

  private List<ColorInfo> data;

  public List<ColorInfo> getData() {
    return data;
  }

  public void setData(List<ColorInfo> data) {
    this.data = data;
  }
}

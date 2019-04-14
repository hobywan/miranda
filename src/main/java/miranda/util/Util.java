package miranda.util;

import java.io.*;

public class Util {

  public static String readFile(File file){
    StringBuilder  buffer = new StringBuilder();
    BufferedReader reader = null;

    try {
      reader = new BufferedReader(new FileReader(file));

      String text;
      while ((text = reader.readLine()) != null) {
        buffer.append(text);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return buffer.toString();
  }
}

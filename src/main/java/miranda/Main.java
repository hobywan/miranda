package miranda;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Locale;

public class Main extends Application {

  @Override
  public void start(Stage stage) throws Exception {

    Locale.setDefault(new Locale("en", "US", "MAC"));

    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("scene.fxml"));
    Parent root = loader.load();

    String style = getClass().getResource("theme.css").toExternalForm();
    Scene scene = new Scene(root);
    scene.getStylesheets().add(style);

    stage.setTitle("Miranda");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) { launch(args); }
}

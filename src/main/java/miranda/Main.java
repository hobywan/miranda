/*
 * Copyright 2014, Hoby Rakotoarivelo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

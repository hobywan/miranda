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

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import miranda.util.InvalidSequenceException;
import miranda.util.Util;

public class Controller implements Initializable {

  private MiRNA model;

  @FXML private MenuItem menuOpen;
  @FXML private MenuItem menuClose;
  @FXML private MenuItem menuExit;
  @FXML private TextArea initialSequence;
  @FXML private TextArea alignedPattern;
  @FXML private TextArea energyMatrix;
  @FXML private Button loadButton;
  @FXML private Button runButton;
  @FXML private Button resetButton;
  @FXML private Label lengthLabel;
  @FXML private Label energyLabel;
  @FXML private Label nbPairsLabel;
  @FXML private RadioButton simpleCriter;
  @FXML private RadioButton wobbleCriter;
  @FXML private RadioButton advancedCriter;
  @FXML private Accordion resultPanel;
  @FXML private TitledPane patternPane;

  @Override
  public void initialize(URL url, ResourceBundle rb) {

    try {
      model = new MiRNA();
      loadButton.setOnAction(this::openFile);
      runButton.setOnAction(this::execute);
      resetButton.setOnAction(this::reset);
      menuOpen.setOnAction(this::openFile);
      menuClose.setOnAction(this::reset);
      menuExit.setOnAction(this::exit);

      // criterion radio button group
      ToggleGroup group = new ToggleGroup();
      simpleCriter.setToggleGroup(group);
      wobbleCriter.setToggleGroup(group);
      advancedCriter.setToggleGroup(group);
      wobbleCriter.setSelected(true);
      advancedCriter.setDisable(true);

      // expand result panel
      resultPanel.setExpandedPane(patternPane);
      reset(null);
    } catch (Exception e) {
      showStackTrace(e);
      exit(null);
    }
  }

  @FXML protected void openFile(ActionEvent event) {

    FileChooser chooser = new FileChooser();
    FileChooser.ExtensionFilter filter =
      new FileChooser.ExtensionFilter("Text files", "*.txt");
    chooser.getExtensionFilters().add(filter);
    Window window = menuOpen.getParentPopup().getScene().getWindow();
    File file = chooser.showOpenDialog(window);
    if (file != null) {
      String content = Util.readFile(file);
      initialSequence.setText(content);
      int length = initialSequence.getLength();
      lengthLabel.setText(String.valueOf(length));
      try {
        this.model.setSequence(content);
      } catch (InvalidSequenceException e) {
        reset(event);
        alertInvalidSequence();
      }
    }
  }

  @FXML private void reset(@SuppressWarnings("unused") ActionEvent event) {
    initialSequence.setText("Loaded base sequence will be displayed here ...");
    alignedPattern.setText("Conformation pattern will be printed here ...");
    lengthLabel.setText("0");
    energyLabel.setText("0");
    nbPairsLabel.setText("0");
    energyMatrix.setText("");
  }

  @FXML private void exit(@SuppressWarnings("unused") ActionEvent event) {
    Platform.exit();
    System.exit(0);
  }

  @FXML private void execute(ActionEvent event) {
    if (this.model.isValid()) {
      Matching.Rule rule;

      if(simpleCriter.isSelected()) {
        rule = Matching.Rule.UNIFORM;
      } else if (advancedCriter.isSelected()) {
        rule = Matching.Rule.LOOKAHEAD;
      } else {
        rule = Matching.Rule.REAL;
      }

      // compute secondary structure
      this.model.setCriterion(rule);
      this.model.computeSecondaryStructure();
      // retrieve result
      String matrix  = this.model.printEnergyMatrix();
      String pattern = this.model.getPattern();
      double energy  = this.model.getEnergy();
      int length     = this.model.getLength();
      int nbPairs    = this.model.getNumberPairs();

      // show them
      alignedPattern.setText(pattern);
      lengthLabel.setText(String.valueOf(length));
      energyLabel.setText(new DecimalFormat("#0.0").format(energy));
      nbPairsLabel.setText(String.valueOf(nbPairs));
      energyMatrix.setText(matrix);
    } else {
      alertInvalidSequence();
      reset(event);
    }
  }

  private void alertInvalidSequence() {

    String details = "Invalid submitted nucleotide sequence\n";
    details += "The sequence should contain only A|U|C|G characters";

    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setGraphic(null);
    alert.setHeaderText("");
    alert.setContentText(details);

    // apply root scene style
    DialogPane parent = alert.getDialogPane();
    String style = getClass().getResource("theme.css").toExternalForm();
    parent.getStylesheets().add(style);
    parent.getStyleClass().add("dialog-pane");

    // show error message
    alert.showAndWait();
    System.err.println(details);
  }

  private void showStackTrace(Exception e) {

    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setGraphic(null);
    alert.setHeaderText("");
    alert.setContentText("An unexpected error occured");

    StringWriter sw = new StringWriter();
    e.printStackTrace(new PrintWriter(sw));
    String errorText = sw.toString();

    Label label = new Label("The exception stacktrace was:");

    TextArea textArea = new TextArea(errorText);
    textArea.setEditable(false);
    textArea.setWrapText(true);
    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    textArea.getStyleClass().add("log-text");
    GridPane.setVgrow(textArea, Priority.ALWAYS);
    GridPane.setHgrow(textArea, Priority.ALWAYS);

    GridPane content = new GridPane();
    content.setPrefWidth(600);
    content.setMaxWidth(Double.MAX_VALUE);
    content.add(label, 0, 0);
    content.add(textArea, 0, 1);

    // apply root scene style
    DialogPane parent = alert.getDialogPane();
    //parent.setContent(content);
    parent.setExpandableContent(content);
    String style = getClass().getResource("theme.css").toExternalForm();
    parent.getStylesheets().add(style);
    parent.getStyleClass().add("dialog-pane");

    // show dialog
    alert.showAndWait();
  }
}
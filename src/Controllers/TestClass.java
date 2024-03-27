package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TestClass implements Initializable {

    @FXML
    private AnchorPane anchorpanetest;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ChoiceBox taskButton = new ChoiceBox();
        anchorpanetest.getChildren().add(taskButton);
        AnchorPane.setLeftAnchor(taskButton,0.0);
        AnchorPane.setBottomAnchor(taskButton,0.0);
        AnchorPane.setTopAnchor(taskButton,0.0);
        AnchorPane.setRightAnchor(taskButton,0.0);
        //taskButton.setOpacity(1);
        taskButton.getItems().add("dummyjob1");
        taskButton.getItems().add("dummyjob2");
        taskButton.getItems().add("dummyjob3");
        //taskButton.setOnAction(event1 -> TaskButton(event1));
    }
}

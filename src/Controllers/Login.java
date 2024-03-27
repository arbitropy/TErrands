package Controllers;

import Database.DB;
import TErrands.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class Login {

    Stage primaryStage;
    Scene LoginScene;
    Scene dashboardScene;
    PreparedStatement statement;
    @FXML
    TextField loginEmail;
    @FXML
    PasswordField loginPassword;
    @FXML
    AnchorPane mainAnchorpane;
    @FXML
    private Hyperlink registerLink;
    @FXML
    Button LoginButton;
    @FXML
    Button backButton;

    public void BackButton(ActionEvent event) throws IOException {
        primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Parent intro = FXMLLoader.load(getClass().getResource("/Scenes/Intro.fxml"));
        Scene introScene = new Scene(intro);
        primaryStage.setScene(introScene);
    }

    public void SeeMore(ActionEvent event) throws IOException {
        primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Parent loginPage = FXMLLoader.load(getClass().getResource("/Scenes/Login.fxml"));
        LoginScene = new Scene(loginPage);
        primaryStage.setScene(LoginScene);
    }

    public void LoginEnter(ActionEvent event) throws IOException, SQLException, CsvValidationException {
        String userName = loginEmail.getText();
        String password = loginPassword.getText();

        if (userName.equals("") || password.equals("")){
            loginEmail.setStyle("-fx-background-color:"+ Main.colors.get("Wrong")+"; -fx-background-radius: 20");
            loginPassword.setStyle("-fx-background-color:"+ Main.colors.get("Wrong")+"; -fx-background-radius: 20");
            loginEmail.clear();
            loginPassword.clear();
            loginEmail.setPromptText("Login Info can't be empty");
            loginPassword.setPromptText("Login Info can't be empty");
            return;
        }



        // database is not connected, temporary csv being used for demonstration
        // statement = Main.mainConnection.prepareStatement("Select * From Profiles Where Email=? and password=?");
        // statement.setString(1,userName);
        // statement.setString(2,password);
        // ResultSet rs = statement.executeQuery();
        String[] userInfo = Main.MatchCreds(userName, password);
        if(userInfo != null){
        // if (rs.next()){
            Main.currentUser = loginEmail.getText();
            Parent dashBoard = FXMLLoader.load(getClass().getResource("/Scenes/Dashboard.fxml"));
            primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            dashboardScene = new Scene(dashBoard);
            primaryStage.setScene(dashboardScene);
        }
        else {
            mainAnchorpane = (AnchorPane) (((Button)(event.getSource())).getParent());
            loginEmail.setStyle("-fx-background-color:"+ Main.colors.get("Wrong")+"; -fx-background-radius: 20");
            loginPassword.setStyle("-fx-background-color:"+ Main.colors.get("Wrong")+"; -fx-background-radius: 20");
            loginEmail.clear();
            loginPassword.clear();
            loginEmail.setPromptText("Invalid Login information");
            loginPassword.setPromptText("Invalid Login information");
        }

    }

    public void Register(ActionEvent event){
        registerLink.setDisable(true);
        loginEmail.setPromptText("Email/Username");
        loginPassword.setPromptText("New Password");
        loginEmail.setStyle("-fx-background-color:"+ Main.colors.get("Textbox")+"; -fx-background-radius: 20");
        loginPassword.setStyle("-fx-background-color:"+ Main.colors.get("Textbox")+"; -fx-background-radius: 20");
        loginEmail.clear();
        loginPassword.clear();

        LoginButton.setText("Register");
        LoginButton.setOnAction(event1 -> {
            try {
                RegisterButton(event1);
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            } catch (CsvValidationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
        });
    }


    public void RegisterButton(ActionEvent event) throws SQLException, IOException, CsvValidationException {
        String userName = loginEmail.getText();
        String password = loginPassword.getText();

        if (userName.equals("") || password.equals("")){
            loginEmail.setStyle("-fx-background-color:"+ Main.colors.get("Wrong")+"; -fx-background-radius: 20");
            loginPassword.setStyle("-fx-background-color:"+ Main.colors.get("Wrong")+"; -fx-background-radius: 20");
            loginEmail.clear();
            loginPassword.clear();
            loginEmail.setPromptText("Registration Info can't be empty");
            loginPassword.setPromptText("Registration Info can't be empty");
            return;
        }
        else {
            // statement = Main.mainConnection.prepareStatement("Select * From Profiles Where Email=?");
            // statement.setString(1,userName);
            // ResultSet rs = statement.executeQuery();
            String[] userInfo = Main.MatchCreds(userName);
            if(userInfo != null){
            // if(rs.next()){
                loginEmail.setStyle("-fx-background-color:"+ Main.colors.get("Wrong")+"; -fx-background-radius: 20");
                loginEmail.clear();
                loginEmail.setPromptText("Email/Username already in use");
            }
            else {
                //statement = Main.mainConnection.prepareStatement("INSERT INTO Profiles (Email,Password) VALUES('"+userName+"','"+password+"');");
                Main.RegisterCreds(userName, password);
                LoginButton.setText("Registration Successful");
                Parent dashBoard = FXMLLoader.load(getClass().getResource("/Scenes/Dashboard.fxml"));
                primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                dashboardScene = new Scene(dashBoard);
                Main.currentUser = loginEmail.getText();
                primaryStage.setScene(dashboardScene);
            }
        }
    }

}
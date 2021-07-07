package TErrands;

import Controllers.Card;
import Controllers.Project;
import Database.DB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Connection;
import java.util.*;

public class Main extends Application {
    public static Map<String,Project> allProjects = new LinkedHashMap<String,Project>();
    public static Map<String,String> colors = new LinkedHashMap<String, String>();
    public static Stage primaryStage;
     public static Project currentProject;
     public static Connection mainConnection;
     public DB db;
     public static String projectStorage = "C:\\Users\\Administrator\\Documents\\BasicTutorial\\src\\ProjectFiles\\";

     public static String currentUser;


    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/Scenes/Intro.fxml"));
        stage.setScene(new Scene(root));
        stage.show();

        db = new DB();
        mainConnection = db.EstablishConnection();
        PopulateColors();
        primaryStage = stage;
        primaryStage.setResizable(false);
    }

    void PopulateColors(){
        colors.put("Purple","#c174f2");
        colors.put("Pink","#f18585");
        colors.put("Red","#f25757");
        colors.put("Blue","#736ced");
        colors.put("Violet","#e365c1");
        colors.put("Brown","#d77a61");
        colors.put("Yellow","#fbd261");
        colors.put("Green","#60d394");
        colors.put("LightGreen","#aaf683");
        colors.put("Grey","#ced5b8");
        colors.put("Orange","#fc8e60");
        colors.put("Textbox", "#dbe6fd");
        colors.put("Background", "#293b5f");
        colors.put("Text","#b2ab8c");
        colors.put("DarkBackground","#243454");
        colors.put("Wrong","#d18082");
    }



    public static void main(String[] args) {
        launch(args);
    }
}

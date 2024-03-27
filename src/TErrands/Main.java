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

import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;


// database codes are commented out, as mysql setup was local and not included in the project
// uses a csv file for credentials

public class Main extends Application {
    public static Map<String,Project> allProjects = new LinkedHashMap<String,Project>();
    public static Map<String,String> colors = new LinkedHashMap<String, String>();
    public static Stage primaryStage;
     public static Project currentProject;
    //  public static Connection mainConnection;
    //  public DB db;
     public static String projectStorage = "D:\\Work\\TErrands\\src\\ProjectFiles\\";
     public static String currentUser;
     public static String[] currentUserInfo;


    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/Scenes/Intro.fxml"));
        stage.setScene(new Scene(root));
        stage.show();

        // db = new DB();
        // mainConnection = db.EstablishConnection();
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

    static String credFilePath = "src\\Controllers\\cred.csv";

    // check if the credentials match, temporarily for csv
    public static String[] MatchCreds(String username, String password) throws FileNotFoundException, IOException, CsvValidationException{
        try (CSVReader csvReader = new CSVReader(new FileReader(credFilePath));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                if (values[1].equals(username) && values[2].equals(password)){
                    currentUserInfo = values; // can't query for each info as no db, save instead, only when password check
                    return values;
                }
            }
            return null;
        } 
    }
    // overloaded for registration mail search
    public static String[] MatchCreds(String username) throws FileNotFoundException, IOException, CsvValidationException{
        try (CSVReader csvReader = new CSVReader(new FileReader(credFilePath));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                if (values[1].equals(username)){
                    return values;
                }
            }
            return null;
        } 
    }

    public static int IdCount() throws FileNotFoundException, IOException, CsvValidationException{
        try (CSVReader csvReader = new CSVReader(new FileReader(credFilePath));) {
            int count = 0;
            while ((csvReader.readNext()) != null) {
                count++;
            }
            return count;
        } 
    }

    public static boolean RegisterCreds(String username, String password) throws CsvValidationException{
        try {
            FileWriter fileWriter = new FileWriter(credFilePath, true);
            fileWriter.append(IdCount() + 1 + "");
            fileWriter.append(",");
            fileWriter.append(username);
            fileWriter.append(",");
            fileWriter.append(password);
            fileWriter.append(",");
            fileWriter.append('0'); // 0 projects when new account
            fileWriter.append("\n");
            fileWriter.close();
            MatchCreds(username, password); // to save the info in currentUserInfo
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean AddProject(String username, String projectname) throws IOException, CsvValidationException {
        String tempFilePath = "src\\Controllers\\cred_temp.csv";
        boolean success = false;

        try (CSVReader csvReader = new CSVReader(new FileReader(credFilePath));
             CSVWriter csvWriter = new CSVWriter(new FileWriter(tempFilePath))) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                // System.out.println(values[0]);
                if (values[1].equals(username)) {
                    String[] newValues = new String[values.length + 1];
                    System.arraycopy(values, 0, newValues, 0, values.length);
                    newValues[values.length] = projectname;
                    newValues[3] = Integer.parseInt(values[3]) + 1 + ""; // increment project count
                    csvWriter.writeNext(newValues);
                    success = true;
                } else {
                    csvWriter.writeNext(values);
                }
            }
        }
        // Replace the original file with the modified file
        File originalFile = new File(credFilePath);
        File tempFile = new File(tempFilePath);
        if (originalFile.delete() && tempFile.renameTo(originalFile)) {
            return success;
        } else {
            return false;
        }
    }
}

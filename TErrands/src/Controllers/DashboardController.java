package Controllers;

import com.mysql.cj.protocol.x.SyncFlushDeflaterOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import TErrands.Main;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class DashboardController implements Initializable {
    @FXML
    public FlowPane projectHbox;
    @FXML
    Button addProjectButton;
    String colors[] = new String[Main.colors.size()];
    TextField projectNameInput;

    PreparedStatement statement;
    ResultSet resultSet;

    Map<String,Project> projects = new LinkedHashMap<>();

    Random random = new Random();

    @FXML
    Button backButton;
    public void BackButton(ActionEvent event) throws IOException {
        Stage primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Parent login = FXMLLoader.load(getClass().getResource("/Scenes/Login.fxml"));
        Scene loginScene = new Scene(login);
        primaryStage.setScene(loginScene);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colors = Main.colors.values().toArray(new String[0]);
        try {
            statement = Main.mainConnection.prepareStatement("select * from profiles where email=?");
            statement.setString(1, Main.currentUser);
            resultSet = statement.executeQuery();
            resultSet.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        int projectAmount = 0;
        try {
            projectAmount = resultSet.getInt(4);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (projectAmount > 0) {
            for (int i = 1; i <= projectAmount; i++) {
                try {
                    PopulateProjectFP(DeSerializeProject(resultSet.getString(5 + i)));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    public Project DeSerializeProject(String name) {
        Project e = null;
        try {
            FileInputStream fileIn = new FileInputStream(Main.projectStorage + name + ".ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            e = (Project) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();

        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
        }
        return e;
    }

    public void NewProjectButton(javafx.event.ActionEvent event) {
        projectNameInput = new TextField();
        projectNameInput.setPromptText("Enter Project Name");
        projectNameInput.setOnAction(e -> {
            try {
                NewProjectNameEnter();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        projectNameInput.setStyle("-fx-background-color: " + Main.colors.get("Textbox") +
                "; -fx-background-radius: 20;");
        projectNameInput.setPrefWidth(addProjectButton.getPrefWidth());
        projectNameInput.setPrefHeight(addProjectButton.getPrefHeight());
        projectHbox.getChildren().set(0, projectNameInput);
    }

    public void NewProjectNameEnter() throws SQLException {
        Project project = new Project(projectNameInput.getText(),Main.currentUser);
        statement = Main.mainConnection.prepareStatement("Select projectamount From Profiles Where Email=?");
        statement.setString(1, Main.currentUser);
        resultSet = statement.executeQuery();
        resultSet.next();
        int projectAmount = resultSet.getInt(1);
        statement = Main.mainConnection.prepareStatement("Select * from profiles");
        int collumnCount = statement.executeQuery().getMetaData().getColumnCount();
        String fileName = GenerateProjectName();
        project.fileName = fileName;
        SerializeProject(project);
        if (collumnCount < 5 + projectAmount + 1) {
            statement = Main.mainConnection.prepareStatement("alter table profiles add column p? varchar (100)");
            statement.setInt(1, projectAmount + 1);
            statement.execute();
        }
        statement = Main.mainConnection.prepareStatement("update profiles set p?=? where email=?");
        statement.setInt(1, projectAmount + 1);
        statement.setString(2, fileName);
        statement.setString(3, Main.currentUser);
        statement.executeUpdate();
        statement = Main.mainConnection.prepareStatement("update profiles set projectamount=?  where email=?");
        statement.setInt(1, projectAmount + 1);
        statement.setString(2, Main.currentUser);
        statement.executeUpdate();

        PopulateProjectFP(project);
        projectHbox.getChildren().set(0, addProjectButton);
    }

    void SerializeProject(Project project) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(Main.projectStorage + project.fileName + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(project);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    String GenerateProjectName() throws SQLException {
        statement = Main.mainConnection.prepareStatement("select projectid from projects order by projectid desc limit 1");
        resultSet = statement.executeQuery();
        resultSet.next();
        String newName = projectNameInput.getText() + (resultSet.getInt(1) + 1);
        statement = Main.mainConnection.prepareStatement("insert into projects (projectname,filename) values(?,?)");
        statement.setString(1, projectNameInput.getText());
        statement.setString(2, newName);
        statement.executeUpdate();
        return newName;
    }

    void PopulateProjectFP(Project project) {
        projects.put(project.projectName,project);
        Button ProjectI = new Button(project.projectName);
        ProjectI.setWrapText(true);
        ProjectI.setAlignment(Pos.CENTER);
        ProjectI.setPrefHeight(addProjectButton.getPrefHeight());
        ProjectI.setPrefWidth(addProjectButton.getPrefWidth());
        projectHbox.getChildren().add(ProjectI);
        ProjectI.setFont(addProjectButton.getFont());
        ProjectI.setStyle("-fx-background-color: " + colors[random.nextInt(10)] +
                "; -fx-background-radius: 15; -fx-text-alignment: center; -fx-alignment: center;");
        ProjectI.setOnAction(e -> {
            try {
                EnterProjectButton(e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    private void EnterProjectButton(javafx.event.ActionEvent event) throws IOException {
        Main.currentProject = projects.get(((Button)(event.getSource())).getText());
        Parent dashBoard = FXMLLoader.load(getClass().getResource("/Scenes/MainBoard.fxml"));
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene mainBoardScene = new Scene(dashBoard);
        primaryStage.setScene(mainBoardScene);
    }

}

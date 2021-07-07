package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import TErrands.Main;
import javafx.stage.Stage;
import java.sql.*;
import java.io.*;
import java.net.URL;
import java.util.*;

public class MainBoardController implements Initializable {

    PreparedStatement statement;
    ResultSet resultSet;

    @FXML
    public AnchorPane mainTaskViewAnchorpane;


    String colors[] = new String[Main.colors.size()];
    Random random = new Random();
    AnchorPane tempAnchorpane;
    @FXML
    private HBox mainHbox;

    Map<VBox, Card> allCards = new LinkedHashMap<VBox, Card>();


    @FXML
    private Button createNewCardButton;

    @FXML
    private Label projectName;

    public List<Card> cards = Main.currentProject.cards;

    @FXML
    private Label taskViewTaskName;
    @FXML
    private ComboBox<String> taskViewAssignTaskBox;
    @FXML
    private Button taskViewDeleteTaskButton;
    @FXML
    private ComboBox<String> taskViewMoveTaskBox;
    @FXML
    private Label taskViewAssignedTo;
    @FXML
    private Button taskViewCrossButton;
    private int currentTaskViewCardIndex;
    private String currentTaskViewTaskText;
    public void TaskButton(ActionEvent event) {
        taskViewAssignTaskBox.getItems().clear();
        taskViewMoveTaskBox.getItems().clear();

        mainTaskViewAnchorpane.setVisible(true);
        mainTaskViewAnchorpane.setDisable(false);
        Button parentButton = ((Button)(event.getSource()));
        currentTaskViewTaskText = parentButton.getText();
        VBox parentVbox = (VBox) (parentButton.getParent()).getParent();
        currentTaskViewCardIndex =  IndexFinder(parentVbox);
        taskViewTaskName.setText(Main.currentProject.projectName + " ->" + Main.currentProject.cards.get(currentTaskViewCardIndex).cardName + " ->" + currentTaskViewTaskText);
        taskViewAssignedTo.setText("Currently assigned to: "+Main.currentUser);
        if(!Main.currentUser.equals(Main.currentProject.admin)){
            taskViewAssignTaskBox.setStyle("-fx-background-color:"+ Main.colors.get("Wrong")+"; -fx-background-radius: 20");
            taskViewDeleteTaskButton.setStyle("-fx-background-color:"+ Main.colors.get("Wrong")+"; -fx-background-radius: 20");
            Tooltip adminToolTip = new Tooltip("This feature is only available for project Administrator");
            taskViewAssignTaskBox.setTooltip(adminToolTip);
            taskViewDeleteTaskButton.setTooltip(adminToolTip);
            taskViewDeleteTaskButton.setOnAction(null);
        }else {
            for (int i = 0; i < Main.currentProject.collaborators.size(); i++) {
                taskViewAssignTaskBox.getItems().add(Main.currentProject.collaborators.get(i));
            }
        }
        for (int i = 0; i < Main.currentProject.cards.size(); i++) {
            taskViewMoveTaskBox.getItems().add(Main.currentProject.cards.get(i).cardName);
        }
        taskViewDeleteTaskButton.setOnAction(null);

    }
    @FXML
    void TaskViewAssignAction(ActionEvent event) throws IOException {
        String assignto = (String) (((ComboBox)(event.getSource())).getValue());
        int assignIndex = Main.currentProject.cards.get(currentTaskViewCardIndex).taskList.indexOf(currentTaskViewTaskText);
        Main.currentProject.cards.get(currentTaskViewCardIndex).assignedTo.set(assignIndex,assignto);
        RefreshProject();
    }
    @FXML
    void TaskViewDeleteAction(ActionEvent event) throws IOException {
        Main.currentProject.cards.get(currentTaskViewCardIndex).taskList.remove(currentTaskViewTaskText);
        RefreshProject();
    }
    @FXML
    void TaskViewMoveAction(ActionEvent event) throws IOException {
        String cardName = (String) (((ComboBox)(event.getSource())).getValue());
        int toIndex = 0;
        for(int i = 0; i < Main.currentProject.cards.size(); i++){
            if(cardName.equals(Main.currentProject.cards.get(i).cardName)){
                toIndex = i;
                break;
            }
        }
        Main.currentProject.cards.get(toIndex).taskList.add(currentTaskViewTaskText);
        Main.currentProject.cards.get(currentTaskViewCardIndex).taskList.remove(currentTaskViewTaskText);
        RefreshProject();
    }
    @FXML
    void taskViewCrossButtonAction(ActionEvent event) {
        mainTaskViewAnchorpane.setDisable(true);
        mainTaskViewAnchorpane.setVisible(false);
    }


    @FXML
    Button backButton;
    public void BackButton(ActionEvent event) throws IOException {
        Stage primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Parent dashboard = FXMLLoader.load(getClass().getResource("/Scenes/Dashboard.fxml"));
        Scene dashboardScene = new Scene(dashboard);
        primaryStage.setScene(dashboardScene);
    }

    @FXML
    private Button collaborateButton;

    @FXML
    private AnchorPane collaborateAnchorPane;

    @FXML
    private Label collaboratingPeopleList;

    @FXML
    private Button collaborateAddButton;

    @FXML
    void collaborateAddButtonAction(ActionEvent event) {
        if(Main.currentUser.equals(Main.currentProject.admin))
        {
            TextField textField = new TextField();
            textField.setPromptText("Enter Email");
            textField.setStyle(collaborateAddButton.getStyle());
            textField.setOnAction(event1 -> {
                try {
                    collaborateAddTextFieldAction(event1);
                } catch (SQLException | IOException throwables) {
                    throwables.printStackTrace();
                }
            });
            ((AnchorPane) (collaborateAddButton.getParent())).getChildren().set(1, textField);
            textField.requestFocus();
            AnchorPane.setBottomAnchor(textField, 15.0);
            AnchorPane.setRightAnchor(textField, 56.0);
            AnchorPane.setLeftAnchor(textField, 56.0);
        }
    }

    void collaborateAddTextFieldAction(ActionEvent event) throws SQLException, IOException {
        TextField source = ((TextField)(event.getSource()));
        String email = source.getText();
        statement = Main.mainConnection.prepareStatement("select * from profiles where email=?");
        statement.setString(1, email);
        resultSet = statement.executeQuery();
        if(!resultSet.next()){
            source.clear();
            source.setPromptText("Invalid user");
            source.setStyle("-fx-background-color:"+ Main.colors.get("Wrong")+"; -fx-background-radius: 20");
            return;
        }
        Main.currentProject.collaborators.add(email);
        statement = Main.mainConnection.prepareStatement("Select projectamount From Profiles Where Email=?");
        statement.setString(1, email);
        resultSet = statement.executeQuery();
        resultSet.next();
        int projectAmount = resultSet.getInt(1);
        statement = Main.mainConnection.prepareStatement("Select * from profiles");
        int collumnCount = statement.executeQuery().getMetaData().getColumnCount();
        String fileName = Main.currentProject.fileName;
        if (collumnCount < 5 + projectAmount + 1) {
            statement = Main.mainConnection.prepareStatement("alter table profiles add column p? varchar (100)");
            statement.setInt(1, projectAmount + 1);
            statement.execute();
        }
        statement = Main.mainConnection.prepareStatement("update profiles set p?=? where email=?");
        statement.setInt(1, projectAmount + 1);
        statement.setString(2, fileName);
        statement.setString(3, email);
        statement.executeUpdate();
        statement = Main.mainConnection.prepareStatement("update profiles set projectamount=?  where email=?");
        statement.setInt(1, projectAmount + 1);
        statement.setString(2, email);
        statement.executeUpdate();

        RefreshProject();

    }

    @FXML
    void collaborateButtonAction(ActionEvent event) {
        if (collaborateAnchorPane.isDisabled()) {
            if(!Main.currentUser.equals(Main.currentProject.admin)){
                collaborateAddButton.setStyle("-fx-background-color:"+ Main.colors.get("Wrong")+"; -fx-background-radius: 20");
                Tooltip adminToolTip = new Tooltip("This feature is only available for project Administrator");
                collaborateAddButton.setTooltip(adminToolTip);
            }
            collaborateAnchorPane.setDisable(false);
            collaborateAnchorPane.setVisible(true);
            String collaboratorlist = Main.currentProject.collaborators.size() + " people are currently working on this project.";
            for (int i = 0; i < Main.currentProject.collaborators.size(); i++) {
                collaboratorlist = collaboratorlist + "\n" + Main.currentProject.collaborators.get(i);
            }
            collaboratorlist = collaboratorlist + "\nCurrent administrator is " + Main.currentProject.admin ;
            collaboratingPeopleList.setText(collaboratorlist);
        }
        else{
            collaborateAnchorPane.setDisable(true);
            collaborateAnchorPane.setVisible(false);
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colors = Main.colors.values().toArray(new String[0]);
        mainTaskViewAnchorpane.setDisable(true);
        mainTaskViewAnchorpane.setVisible(false);
        collaborateAnchorPane.setDisable(true);
        collaborateAnchorPane.setVisible(false);

        projectName.setText(Main.currentProject.projectName);
        PopulateScreen();

    }

    void PopulateScreen(){
        for (int i = 0; i < cards.size(); i++) {
            PopulateCards(cards.get(i));
            for (int j = 0; j < cards.get(i).taskList.size(); j++) {
                VBox parentVbox = ((VBox) (mainHbox.getChildren().get(i)));
                tempAnchorpane = (AnchorPane) (parentVbox.getChildren().get(parentVbox.getChildren().size() - 1));
                AnchorPane anchorPane = PopulateTasks(cards.get(i).taskList.get(j), "#000000");
                anchorPane.setStyle(parentVbox.getChildren().get(0).getStyle() + "-fx-background-radius: 0 0 0 0;");
                parentVbox.getChildren().set(parentVbox.getChildren().size() - 1, anchorPane);
                parentVbox.getChildren().add(tempAnchorpane);
            }
        }
    }

    public void RefreshProject() throws IOException {
            try {
                FileOutputStream fileOut =
                        new FileOutputStream(Main.projectStorage + Main.currentProject.fileName + ".ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(Main.currentProject);
                out.close();
                fileOut.close();
            } catch (IOException i) {
                i.printStackTrace();
            }

            Project e = null;
            try {
                FileInputStream fileIn = new FileInputStream(Main.projectStorage + Main.currentProject.fileName + ".ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                e = (Project) in.readObject();
                in.close();
                fileIn.close();
            } catch (IOException i) {
                i.printStackTrace();

            } catch (ClassNotFoundException c) {
                c.printStackTrace();
            }
            Main.currentProject = e;
        Parent dashBoard = FXMLLoader.load(getClass().getResource("/Scenes/MainBoard.fxml"));
        Stage primaryStage = (Stage) mainHbox.getScene().getWindow();
        Scene mainBoardScene = new Scene(dashBoard);
        primaryStage.setScene(mainBoardScene);
    }

    @FXML
    void createNewCardButtonAction(ActionEvent event) {
        TextField newCardName = new TextField();
        newCardName.setPromptText("Enter card name");
        newCardName.setPrefWidth(200);
        newCardName.setOnAction(e -> {
            try {
                NewCardAction(e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        mainHbox.getChildren().set(IndexFinder((Node) event.getSource()), newCardName);
        newCardName.requestFocus();
        newCardName.setStyle("-fx-background-color: " + Main.colors.get("Textbox") +
                "; -fx-background-radius: 20;");
    }

    void NewCardAction(ActionEvent event) throws IOException {
        int colorindex = random.nextInt(10);
        Card card = new Card(((TextField) event.getSource()).getText(), colors[colorindex]);
        Main.currentProject.cards.add(card);
        PopulateCards(card);
        RefreshProject();
    }



    public Button CloneTaskButton() {
        Button createNewTaskButtonClone = new Button("Create New Task");
        createNewTaskButtonClone.setStyle(createNewCardButton.getStyle());
        createNewTaskButtonClone.setPrefWidth(createNewCardButton.getPrefWidth() - 10);
        AnchorPane.setLeftAnchor(createNewTaskButtonClone, 5.0);
        AnchorPane.setTopAnchor(createNewTaskButtonClone, 10.0);
        AnchorPane.setBottomAnchor(createNewTaskButtonClone, 5.0);
        createNewTaskButtonClone.setPrefHeight(createNewCardButton.getPrefHeight() + 10);
        createNewTaskButtonClone.setFont(createNewCardButton.getFont());
        createNewTaskButtonClone.setOnAction(e -> CreateTaskButtonAction(e));
        return createNewTaskButtonClone;
    }

    public void PopulateCards(Card card) {
        VBox vBox = new VBox();
        mainHbox.getChildren().set(mainHbox.getChildren().size() - 1, vBox);
        vBox.prefWidth(200);
        vBox.prefHeight(65);

        Label label = new Label(card.cardName);
        label.setPrefSize(180, 30);
        AnchorPane.setLeftAnchor(label, 10.0);
        label.setAlignment(Pos.TOP_LEFT);
        label.setFont(Font.font("Arial Black", 12));
        label.setTextFill(Paint.valueOf("#000000"));
        label.setWrapText(true);

        AnchorPane anchorPane = new AnchorPane();
        vBox.getChildren().add(anchorPane);
        anchorPane.setPrefSize(200, 30);
        anchorPane.getChildren().add(label);
        anchorPane.setStyle("-fx-background-color: " + card.cardColor +
                "; -fx-background-radius: 12 12 0 0;");

        allCards.put(vBox, card);
        mainHbox.setPrefWidth(mainHbox.getPrefWidth() + 200);
        mainHbox.getChildren().add(createNewCardButton);

        AnchorPane anchorPane2 = new AnchorPane();
        anchorPane2.setPrefSize(200, 32);
        anchorPane2.getChildren().add(CloneTaskButton());
        anchorPane2.setStyle("-fx-background-color: " + card.cardColor +
                "; -fx-background-radius: 0 0 12 12;");
        vBox.getChildren().add(anchorPane2);
    }

    public void CreateTaskButtonAction(ActionEvent event) {
        tempAnchorpane = ((AnchorPane) (((Button) (event.getSource())).getParent()));
        TextField newTaskName = new TextField();
        newTaskName.setPromptText("Enter Task name");
        newTaskName.setPrefWidth(190);
        AnchorPane.setLeftAnchor(newTaskName, 5.0);
        AnchorPane.setBottomAnchor(newTaskName, 5.0);
        AnchorPane.setTopAnchor(newTaskName, 10.0);
        tempAnchorpane.getChildren().set(0, newTaskName);
        newTaskName.requestFocus();
        newTaskName.setOnAction(e -> {
            try {
                NewTaskAction(e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        newTaskName.setStyle("-fx-background-color: " + Main.colors.get("Textbox") +
                "; -fx-background-radius: 20;");
    }


    int IndexFinder(Node vBox){
        return mainHbox.getChildren().indexOf(vBox);
    }


    public void NewTaskAction(ActionEvent event) throws IOException {
        TextField parentTextField = ((TextField) (event.getSource()));
        AnchorPane parentAnchorpane = (AnchorPane) (parentTextField.getParent());
        VBox parentVbox = (VBox) (parentAnchorpane.getParent());
        int index = IndexFinder(parentVbox);
        Main.currentProject.cards.get(index).taskList.add(parentTextField.getText());
        Main.currentProject.cards.get(index).assignedTo.add(Main.currentUser);
        AnchorPane anchorPane = PopulateTasks(parentTextField.getText(), parentAnchorpane.getStyle());
        parentVbox.getChildren().set(parentVbox.getChildren().size() - 1, anchorPane);
        tempAnchorpane.getChildren().set(0, CloneTaskButton());
        parentVbox.getChildren().add(tempAnchorpane);
        RefreshProject();
    }

    public AnchorPane PopulateTasks(String name, String color) {
        Label label = new Label(name);
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Paint.valueOf("#000000"));
        label.setWrapText(true);
        label.setFont(Font.font("Arial", 12));
        AnchorPane.setLeftAnchor(label, 5.0);
        AnchorPane.setRightAnchor(label, 5.0);
        AnchorPane.setTopAnchor(label, 5.0);
        label.setPadding(new Insets(5, 5, 5, 5));
        label.setPrefWidth(180);
        label.setStyle("-fx-background-color: rgba(26, 26, 26, 0.3); -fx-background-radius: 5");

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(200, label.getPrefHeight() + 30);
        anchorPane.getChildren().add(label);
        anchorPane.setStyle(color + "-fx-background-radius: 0 0 0 0;");

        Button taskButton = new Button();
        taskButton.setText(name);
        anchorPane.getChildren().add(taskButton);
        AnchorPane.setLeftAnchor(taskButton, 0.0);
        AnchorPane.setBottomAnchor(taskButton, 0.0);
        AnchorPane.setTopAnchor(taskButton, 0.0);
        AnchorPane.setRightAnchor(taskButton, 0.0);
        taskButton.setOpacity(0);
        taskButton.setOnAction(event1 -> TaskButton(event1));

        return anchorPane;
    }
}
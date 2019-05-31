/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import blowfishapp.tcp.*;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javax.swing.JProgressBar;

/**
 *
 * @author Magdalena
 */
public class BlowFishApp extends Application {

    String address = "127.0.0.3";
    int port = 9999;
    public ProgressBar progressBar = new ProgressBar();
  
    @Override
    public void start(Stage primaryStage) throws UnknownHostException {
        Text encryptionTypeText = new Text("Tryb szyfrowania");
        Text outputFileNameText = new Text("Plik wyjściowy");
        Text error = new Text("");
        Text login = new Text("Podaj login: ");
        Text password = new Text("Podaj hasło: ");
        TextField loginTextField = new TextField("Login");
        PasswordField passwordTextField = new PasswordField();
        TextField outputFileNameTextField = new TextField("Nazwa");
        Text inputFileNameText = new Text("Plik wejściowy");
        TextField inputFileNameTextField = new TextField("Nazwa");
        Text filesListText = new Text("Pliki do wyboru:");
        progressBar.setPrefWidth(250.0d);
        progressBar.setProgress(0);

        ObservableList<String> names = FXCollections.observableArrayList(
                "ECB", "CBC", "CFB", "OFB", "BRAK");
        ChoiceBox<String> encryptionChoiceBox = new ChoiceBox<>(names);

        Text pswdText = new Text("Hasło");
        PasswordField pswdField = new PasswordField();

        InetAddress serverAddress = InetAddress.getByName(address);

        Button chooseButton = new Button();
        chooseButton.setText("Lista plików");
        chooseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Task<String> task = new FileRequest(serverAddress, port);
                    executor.submit(task);
                    filesListText.setText("Pliki do wyboru:\n" + task.get());

                } catch (InterruptedException ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button sendButton = new Button();
        sendButton.setText("Prośba o plik");
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    InetAddress serverAddress = InetAddress.getByName(address);

                    Task<Void> task = new Client(serverAddress, port,
                            encryptionChoiceBox.getValue().getBytes(), inputFileNameTextField.getText().getBytes(),
                            outputFileNameTextField.getText(), pswdField.getText(), progressBar);
                    executor.submit(task);
                    //progressBar.setProgress(1);
                } catch (Exception ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        
        Button loginButton = new Button();
        Button logoutButton = new Button();
        logoutButton.setText("Wyloguj");
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    encryptionTypeText.setVisible(false);
                    encryptionChoiceBox.setVisible(false);
                    chooseButton.setVisible(false);
                    inputFileNameText.setVisible(false);
                    inputFileNameTextField.setVisible(false);
                    outputFileNameText.setVisible(false);
                    outputFileNameTextField.setVisible(false);
                    pswdText.setVisible(false);
                    pswdField.setVisible(false);
                    sendButton.setVisible(false);
                    progressBar.setVisible(false);
                    filesListText.setVisible(false);
                    logoutButton.setVisible(false);
                    login.setVisible(true);
                    password.setVisible(true);
                    loginTextField.setVisible(true);
                    passwordTextField.setVisible(true);
                    loginButton.setVisible(true);
                    error.setVisible(true);
            }
        });


        loginButton.setText("Zaloguj");
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String pass = passwordTextField.getText();
                String user = loginTextField.getText();
                String result = "failed";
                int number = 0;

                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Task<String> taskLogin = new Login(serverAddress, port, user, pass);
                    executor.submit(taskLogin);
                try {
                    result = taskLogin.get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (result.contentEquals("success")) {               
                    passwordTextField.setText(null);
                    loginTextField.setText(null);
                    error.setText(null);
                    encryptionTypeText.setVisible(true);
                    encryptionChoiceBox.setVisible(true);
                    chooseButton.setVisible(true);
                    inputFileNameText.setVisible(true);
                    inputFileNameTextField.setVisible(true);
                    outputFileNameText.setVisible(true);
                    outputFileNameTextField.setVisible(true);
                    pswdText.setVisible(true);
                    pswdField.setVisible(true);
                    sendButton.setVisible(true);
                    progressBar.setVisible(true);
                    filesListText.setVisible(true);
                    logoutButton.setVisible(true);
                    login.setVisible(false);
                    password.setVisible(false);
                    loginTextField.setVisible(false);
                    passwordTextField.setVisible(false);
                    loginButton.setVisible(false);
                    error.setVisible(false);
                }
                if(result.contentEquals("failed"))
                {
                    error.setText("Złe hasło lub login. Spróbuj jeszcze raz.");
                }
            }
        });

        GridPane gridPane = new GridPane();

        encryptionTypeText.setVisible(false);
        encryptionChoiceBox.setVisible(false);
        chooseButton.setVisible(false);
        inputFileNameText.setVisible(false);
        inputFileNameTextField.setVisible(false);
        outputFileNameText.setVisible(false);
        outputFileNameTextField.setVisible(false);
        pswdText.setVisible(false);
        pswdField.setVisible(false);
        sendButton.setVisible(false);
        progressBar.setVisible(false);
        filesListText.setVisible(false);
        logoutButton.setVisible(false);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.add(login, 0, 1);
        gridPane.add(loginTextField, 1, 1);
        gridPane.add(password, 0, 2);
        gridPane.add(passwordTextField, 1, 2);
        gridPane.add(error, 0, 3);
        gridPane.add(loginButton, 0, 4);
        gridPane.add(logoutButton, 0, 1);
        gridPane.add(encryptionTypeText, 0, 2);
        gridPane.add(encryptionChoiceBox, 1, 2);
        gridPane.add(chooseButton, 0, 3);
        gridPane.add(inputFileNameText, 0, 4);
        gridPane.add(inputFileNameTextField, 1, 4);
        gridPane.add(outputFileNameText, 0, 5);
        gridPane.add(outputFileNameTextField, 1, 5);
        gridPane.add(pswdText, 0, 6);
        gridPane.add(pswdField, 1, 6);
        gridPane.add(sendButton, 0, 7);
        gridPane.add(progressBar, 0, 8);
        gridPane.add(filesListText, 0, 9);

        Scene scene = new Scene(gridPane, 400, 350);

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Closing App");
        });

        primaryStage.setTitle("Klient");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

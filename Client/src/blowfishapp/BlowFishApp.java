/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp;

import blowfishapp.keys.KeysGenerator;
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

/**
 *
 * @author Magdalena
 */
public class BlowFishApp extends Application {

    String address = "127.0.0.3";
    int port = 9999;
    public ProgressBar progressBar = new ProgressBar();
    public KeysGenerator keysGenerator;

    @Override
    public void start(Stage primaryStage) throws UnknownHostException {
        Text encryptionTypeText = new Text("Tryb szyfrowania");
        Text outputFileNameText = new Text("Plik wyjściowy");
        Text errorText = new Text("");
        Text loginText = new Text("Podaj login: ");
        Text pswdText = new Text("Podaj hasło: ");
        TextField loginTextField = new TextField("Login");
        PasswordField loginPswdTextField = new PasswordField();
        TextField outputFileNameTextField = new TextField("Nazwa");
        Text inputFileNameText = new Text("Plik wejściowy");
        TextField inputFileNameTextField = new TextField("Nazwa");
        Text confirmPswdText = new Text("Potwierdz hasłem");
        PasswordField confirmPswdField = new PasswordField();
        Text filesListText = new Text("Pliki do wyboru:");
        progressBar.setPrefWidth(250.0d);
        progressBar.setProgress(0);

        ObservableList<String> modeNames = FXCollections.observableArrayList(
                "ECB", "CBC", "CFB", "OFB", "BRAK");
        ChoiceBox<String> encryptionChoiceBox = new ChoiceBox<>(modeNames);
        
        ObservableList<String> userNames = FXCollections.observableArrayList();
        ChoiceBox<String> receiverChoiceBox = new ChoiceBox<>(userNames);

        InetAddress serverAddress = InetAddress.getByName(address);

        Button chooseButton = new Button();
        chooseButton.setText("Lista plików");
        Button askForFileButton = new Button();
        askForFileButton.setText("Prośba o plik");
        Button sendButton = new Button();
        sendButton.setVisible(false);
        sendButton.setText("Potwierdź");
        Button loginButton = new Button();
        loginButton.setText("Zaloguj");
        Button logoutButton = new Button();
        logoutButton.setText("Wyloguj");

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

        askForFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendButton.setVisible(true);
                confirmPswdField.setVisible(true);
                confirmPswdText.setVisible(true);
            }
        });

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    InetAddress serverAddress = InetAddress.getByName(address);
                    
                    Task<Void> task = new Client(serverAddress, port,
                            encryptionChoiceBox.getValue().getBytes(), inputFileNameTextField.getText().getBytes(),
                            outputFileNameTextField.getText(), receiverChoiceBox.getValue(),
                            confirmPswdField.getText(), keysGenerator, progressBar);
                    executor.submit(task);
                    
                    sendButton.setVisible(false);
                    confirmPswdField.setVisible(false);
                    confirmPswdField.setText(null);
                    confirmPswdText.setVisible(false);
                } catch (Exception ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                encryptionTypeText.setVisible(false);
                encryptionChoiceBox.setVisible(false);
                receiverChoiceBox.setVisible(false);
                chooseButton.setVisible(false);
                inputFileNameText.setVisible(false);
                inputFileNameTextField.setVisible(false);
                outputFileNameText.setVisible(false);
                outputFileNameTextField.setVisible(false);
                askForFileButton.setVisible(false);
                progressBar.setVisible(false);
                filesListText.setVisible(false);
                logoutButton.setVisible(false);
                loginText.setVisible(true);
                pswdText.setVisible(true);
                loginTextField.setVisible(true);
                loginPswdTextField.setVisible(true);
                loginButton.setVisible(true);
                errorText.setVisible(true);
            }
        });

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String pass = loginPswdTextField.getText();
                String user = loginTextField.getText();
                String result = "failed";

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
                    keysGenerator = new KeysGenerator(loginPswdTextField.getText(),loginTextField.getText());
                    //sending public key to server
                    try {
                    Task<ObservableList<String>> task = new KeyBinding(
                            serverAddress, port,loginTextField.getText().getBytes(), keysGenerator.readPublicKey());
                    executor.submit(task);
                    
                        receiverChoiceBox.setItems(task.get());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    loginPswdTextField.setText(null);
                    loginTextField.setText(null);
                    errorText.setText(null);
                    receiverChoiceBox.setVisible(true);
                    encryptionTypeText.setVisible(true);
                    encryptionChoiceBox.setVisible(true);
                    chooseButton.setVisible(true);
                    inputFileNameText.setVisible(true);
                    inputFileNameTextField.setVisible(true);
                    outputFileNameText.setVisible(true);
                    outputFileNameTextField.setVisible(true);
                    askForFileButton.setVisible(true);
                    progressBar.setVisible(true);
                    filesListText.setVisible(true);
                    logoutButton.setVisible(true);
                    loginText.setVisible(false);
                    pswdText.setVisible(false);
                    loginTextField.setVisible(false);
                    loginPswdTextField.setVisible(false);
                    loginButton.setVisible(false);
                    errorText.setVisible(false);
                }
                if (result.contentEquals("failed")) {
                    errorText.setText("Złe hasło lub login. Spróbuj jeszcze raz.");
                }
            }
        });

        GridPane gridPane = new GridPane();

        receiverChoiceBox.setVisible(false);
        encryptionTypeText.setVisible(false);
        encryptionChoiceBox.setVisible(false);
        chooseButton.setVisible(false);
        inputFileNameText.setVisible(false);
        inputFileNameTextField.setVisible(false);
        outputFileNameText.setVisible(false);
        outputFileNameTextField.setVisible(false);
        askForFileButton.setVisible(false);
        sendButton.setVisible(false);
        confirmPswdText.setVisible(false);
        confirmPswdField.setVisible(false);
        progressBar.setVisible(false);
        filesListText.setVisible(false);
        logoutButton.setVisible(false);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.add(loginText, 0, 1);
        gridPane.add(loginTextField, 1, 1);
        gridPane.add(pswdText, 0, 2);
        gridPane.add(loginPswdTextField, 1, 2);
        gridPane.add(errorText, 0, 3);
        gridPane.add(loginButton, 0, 4);
        gridPane.add(logoutButton, 0, 1);
        gridPane.add(encryptionTypeText, 0, 2);
        gridPane.add(encryptionChoiceBox, 1, 2);
        gridPane.add(receiverChoiceBox,2,2);
        gridPane.add(chooseButton, 0, 3);
        gridPane.add(inputFileNameText, 0, 4);
        gridPane.add(inputFileNameTextField, 1, 4);
        gridPane.add(outputFileNameText, 0, 5);
        gridPane.add(outputFileNameTextField, 1, 5);
        gridPane.add(askForFileButton, 0, 7);
        gridPane.add(confirmPswdText, 1, 7);
        gridPane.add(confirmPswdField, 1, 8);
        gridPane.add(sendButton, 2, 8);
        gridPane.add(progressBar, 0, 9);
        gridPane.add(filesListText, 0, 10);

        Scene scene = new Scene(gridPane, 500, 400);

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

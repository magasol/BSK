/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp;

import blowfishapp.decryptionModes.Decryption;
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
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Magdalena
 */
public class BlowFishApp extends Application {

    final private String outputPathDecrypted = "E:\\semestr 6\\bsk\\decrypted";
    Decryption decryption = null;
    String address = "127.0.0.3";
    int port = 9999;

    @Override
    public void start(Stage primaryStage) throws UnknownHostException {
        Text encryptionTypeText = new Text("Tryb szyfrowania");
        Text outputFileNameText = new Text("Plik wyjściowy");
        TextField outputFileNameTextField = new TextField("Nazwa");
        Text inputFileNameText = new Text("Plik wejściowy");
        TextField inputFileNameTextField = new TextField("Nazwa");
        Text filesListText = new Text("Pliki do wyboru:");

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
                    Future<String> callFuture = executor.submit(new FileRequest(serverAddress, port));
                    filesListText.setText("Pliki do wyboru:\n" + callFuture.get());
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

                    Future<Decryption> callFuture = executor.submit(new Client(serverAddress, port,
                            encryptionChoiceBox.getValue().getBytes(), inputFileNameTextField.getText().getBytes(),
                            pswdField.getText()));
                    decryption = callFuture.get();
                    pswdField.clear();
                    System.out.println("Odrzymano zaszyfrowany plik");
                } catch (Exception ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button updateButton = new Button();
        updateButton.setText("Odszyfruj plik");
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (decryption != null) {
                    try {
                        decryption.keysGenerator.decryptSecretKey(pswdField.getText());
                        byte[] decryptedText = decryption.decryptText();
                        decryption.writeFile(outputPathDecrypted, outputFileNameTextField.getText(), decryptedText);
                        System.out.println("Plik odszyfrowany");
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchPaddingException ex) {
                        Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidKeyException ex) {
                        Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalBlockSizeException ex) {
                        Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (BadPaddingException ex) {
                        Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        GridPane gridPane = new GridPane();

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.add(encryptionTypeText, 0, 1);
        gridPane.add(encryptionChoiceBox, 1, 1);
        gridPane.add(chooseButton, 0, 2);
        gridPane.add(inputFileNameText, 0, 3);
        gridPane.add(inputFileNameTextField, 1, 3);
        gridPane.add(outputFileNameText, 0, 4);
        gridPane.add(outputFileNameTextField, 1, 4);
        gridPane.add(pswdText, 0, 5);
        gridPane.add(pswdField, 1, 5);
        gridPane.add(sendButton, 0, 6);
        gridPane.add(updateButton, 0, 7);
        gridPane.add(filesListText, 0, 8);

        Scene scene = new Scene(gridPane, 400, 350);

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Closing App");
        });

        primaryStage.setTitle("Klient");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

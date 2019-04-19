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
import blowfishapp.keys.KeysGenerator;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import blowfishapp.tcp.*;
import java.io.File;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ChoiceBox;
import blowfishapp.file.*;

/**
 *
 * @author Magdalena
 */
public class BlowFishApp extends Application {

    private KeysGenerator keysGenerator;
    Decryption encryption = null;
    String address = "127.0.0.3";
    int port = 9999;
    String path = "C:\\Users\\Aleksandra\\Desktop";

    public void chooseFile(String path)
    {
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new Exception("The file does not exist or the path is incorrect.");
            }
            
            MyComparator comparator=new MyComparator();
            DiskDirectory myDiskDirectory = new DiskDirectory(file, 1, comparator);
            myDiskDirectory.print(100);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    @Override
    public void start(Stage primaryStage) {
        Text encryptionTypeText = new Text("Tryb szyfrowania");
        Text outputFileNameText = new Text("Plik wyjściowy");
        TextField outputFileNameTextField = new TextField("Nazwa");
        Text inputFileNameText = new Text("Plik wejściowy");
        TextField inputFileNameTextField = new TextField("Nazwa");
        
        ObservableList<String> names = FXCollections.observableArrayList(
                "ECB", "CBC", "CFB", "OFB", "NONE");
        ChoiceBox<String> encryptionChoiceBox = new ChoiceBox<>(names);
        
        Text pswdText = new Text("Hasło");
        PasswordField pswdField = new PasswordField();

        //na razie tworzenie kluczy, później będą tworzone w momencie nawiązania połączenia
        Button pswdButton = new Button();
        pswdButton.setText("Create Keys");
        pswdButton.setOnAction(new EventHandler<ActionEvent>() {
           @Override
            public void handle(ActionEvent event) {
                generateKeys(pswdField.getText());
            }
        });

        Button chooseButton = new Button();
        chooseButton.setText("Choose File");
        chooseButton.setOnAction(new EventHandler<ActionEvent>() {
           @Override
            public void handle(ActionEvent event) {
                chooseFile(path);
            }
        });
        
        Button sendButton = new Button();
        sendButton.setText("Send");
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    //pamiętać o zmianie adresu serwera
                    InetAddress serverAddress = InetAddress.getByName(address);
                    //Client client = new Client(serverAddress, port);
                    String file = path + "\\" + inputFileNameTextField.getText();
                    Task<Void> task = new Send(serverAddress, port, "test".getBytes(), encryptionChoiceBox.getValue().getBytes(), file.getBytes());
                    executor.submit(task);
                    //client.send("test".getBytes());
                    //byte[] receivedText = client.receive();
                    //client.decrypt("ECB", receivedText, outputFileNameTextField.getText(), keysGenerator);  //ODEBRANE OD SERWERA 
                    //client.stop();
                    
                } catch (Exception ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
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
        gridPane.add(pswdButton, 2, 5);
        gridPane.add(sendButton, 0, 6);

        Scene scene = new Scene(gridPane, 400, 350);

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

    private void generateKeys(String pswd) {
        this.keysGenerator = new KeysGenerator(pswd);
    }
}

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
import blowfishapp.encryptionModes.*;
import blowfishapp.keys.KeysGenerator;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import blowfishapp.tcp.*;
import java.net.InetAddress;

/**
 *
 * @author Magdalena
 */
public class BlowFishApp extends Application {

    private KeysGenerator keysGenerator;
    Encryption encryption = null;
    String address = "127.0.0.3";
    int port = 9999;

    @Override
    public void start(Stage primaryStage) {
        Text outputFileNameText = new Text("Plik wyjściowy");
        TextField outputFileNameTextField = new TextField("Nazwa");

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

        Button sendButton = new Button();
        sendButton.setText("Send");
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    //pamiętać o zmianie adresu serwera
                    InetAddress serverAddress = InetAddress.getByName(address);

                    Client client = new Client(serverAddress, port);
                    client.send("cos".getBytes());
                    byte[] receivedText = client.receive();
                    client.decrypt("CBC", receivedText,outputFileNameTextField.getText(),keysGenerator);  //ODEBRANE OD SERWERA 
                    client.stop();

                } catch (Exception ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        GridPane gridPane = new GridPane();

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.add(outputFileNameText, 0, 2);
        gridPane.add(outputFileNameTextField, 1, 2);
        gridPane.add(pswdText, 0, 3);
        gridPane.add(pswdField, 1, 3);
        gridPane.add(pswdButton, 2, 3);
        gridPane.add(sendButton, 0, 5);

        Scene scene = new Scene(gridPane, 400, 350);

        primaryStage.setTitle("Aplikacja szyfrująca");
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

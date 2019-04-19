/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import blowfishapp.encryptionModes.*;
import blowfishapp.keys.KeysGenerator;
import blowfishapp.tcp.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Magdalena
 */
public class BlowFishApp extends Application {

    private File file;
    private KeysGenerator keysGenerator;
    Encryption encryption = null;
    int port = 9999;
    String address = "127.0.0.3";
    Server server;
    boolean flag = true;

    @Override
    public void start(Stage primaryStage) {

        //Text inputFileNameText = new Text("Nazwa pliku");
        
        ExecutorService executor = new ThreadPoolExecutor( 
                 3, //minimalna liczba wątków
                 16, //maksymalna liczba wątków 
                 120, //maksymalny czas nieaktywności wątków 
                 TimeUnit.SECONDS, 
                 new LinkedBlockingQueue<>() //kolejka zadań
         );

        //final FileChooser fileChooser = new FileChooser();

        //final Button chooseFileButton = new Button("Wybierz plik");

        /*chooseFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File f = fileChooser.showOpenDialog(primaryStage);
                if (f != null) {
                    file = f;
                    inputFileNameText.setText(f.getName());
                }
            }
        });*/

        String pswdField = "key";
        String outputFileNameTextField = "Plik.txt";

        Button sendButton = new Button();
        sendButton.setText("Send");
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    generateKeys(pswdField);
                    encrypt(new String(server.type), outputFileNameTextField);
                    server.send(port, encryption.encryptedText);
                } catch (Exception ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button startServerButton = new Button();
        startServerButton.setText("Start server");
        startServerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    InetAddress serverAddress = InetAddress.getByName(address);
                    server = new Server(port, serverAddress);
                    System.out.println(server.getAddress());
                } catch (IOException ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button receiveServerButton = new Button();
        receiveServerButton.setText("Receive");
        receiveServerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                while(flag){
                   // try {
                        final Socket connection;
                    try {
                        connection = server.serverSocket.accept();
                        executor.submit(() -> {server.listen(connection);});
                        flag = false;
                        file = new File(new String(server.filePath));
                    } catch (IOException ex) {
                        Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        
                        //connection.close();
                        
                   // } catch (IOException ex) {
                   //     Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    //}
                  
            }
                flag = true;
                
            }
        });

        Button stopServerButton = new Button();
        stopServerButton.setText("Stop server");
        stopServerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                server.stop();
            }
        });

        GridPane gridPane = new GridPane();

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //gridPane.add(chooseFileButton, 0, 2);
        //gridPane.add(inputFileNameText, 1, 2);
        gridPane.add(startServerButton, 0, 0);
        gridPane.add(sendButton, 0, 3);
        gridPane.add(stopServerButton, 1, 0);
        gridPane.add(receiveServerButton, 1, 3);

        Scene scene = new Scene(gridPane, 400, 350);

        primaryStage.setTitle("Serwer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void encrypt(String value, String outputFileName) throws IOException {
        if (file != null) {
            switch (value) {
                case "CBC":
                    System.out.println("tryb szyfrowania cbc");
                    encryption = new EncryptionCBC(file.getPath(), this.keysGenerator);
                    break;
                case "CFB":
                    encryption = new EncryptionCFB(file.getPath(), this.keysGenerator);
                    System.out.println("tryb szyfrowania cfb");
                    break;
                case "ECB":
                    encryption = new EncryptionECB(file.getPath(), this.keysGenerator);
                    System.out.println("tryb szyfrowania ecb");
                    break;
                case "OFB":
                    encryption = new EncryptionOFB(file.getPath(), this.keysGenerator);
                    System.out.println("tryb szyfrowania ofb");
                    break;
                default:
                    encryption = new Encryption(file.getPath(), this.keysGenerator);
                    System.out.println("brak trybu szyfrowania");
            }
            if (encryption != null) {
                encryption.encryptFile();
            }
        }
    }

    private void generateKeys(String pswd) {
        this.keysGenerator = new KeysGenerator(pswd);
    }
}

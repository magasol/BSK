/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp;

import blowfishapp.file.DiskDirectory;
import blowfishapp.file.MyComparator;
import java.io.File;
import java.io.IOException;
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

    File file;
    int port = 9999;
    String address = "127.0.0.3";
    Server server;
    boolean flag = true;
    private String list = "";
    
    @Override
    public void start(Stage primaryStage) {

        Text encryptionTypeText = new Text("Tryb szyfrowania");
        Text inputFileNameText = new Text("Nazwa pliku");

        ExecutorService executor = new ThreadPoolExecutor(
                3, //minimalna liczba wątków
                16, //maksymalna liczba wątków 
                120, //maksymalny czas nieaktywności wątków 
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>() //kolejka zadań
        );

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
        
        Button fileButton = new Button();
        fileButton.setText("Send files");
        fileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                while (flag) {
                    final Socket connection;
                    try {
                        connection = server.serverSocket.accept();
                        executor.submit(() -> {
                            server.listenFiles(connection, port);
                        });
                        flag = false;
                    } catch (IOException ex) {
                        Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                flag = true;
            }
        });

        Button receiveServerButton = new Button();
        receiveServerButton.setText("Do work");
        receiveServerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                while (flag) {
                    final Socket connection;
                    try {
                        connection = server.serverSocket.accept();
                        executor.submit(() -> {
                            server.listen(connection, port);
                        });
                        flag = false;
                    } catch (IOException ex) {
                        Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
        gridPane.add(startServerButton, 0, 0);
        gridPane.add(stopServerButton, 1, 0);
        gridPane.add(fileButton, 0, 1);
        gridPane.add(receiveServerButton, 1, 1);

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
}

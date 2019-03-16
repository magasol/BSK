/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3;

import java.awt.Desktop;
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
import javafxapplication3.files.FileToEncrypt;
import javafxapplication3.files.FileToEncryptCBC;
import javafxapplication3.files.FileToEncryptCFB;
import javafxapplication3.files.FileToEncryptECB;
import javafxapplication3.files.FileToEncryptOFB;

/**
 *
 * @author Magdalena
 */
public class JavaFXApplication3 extends Application {
    
    private final Desktop desktop = Desktop.getDesktop();
    private final int ROW_HEIGHT = 25;
    private File file;
    private String chosenEncryptionType;
    
    
    @Override
    public void start(Stage primaryStage) {
        
        Text encryptionTypeText = new Text("Tryb szyfrowania");
        Text fileNameText = new Text("Nazwa pliku"); 
        
        ObservableList<String> names = FXCollections.observableArrayList( 
          "ECB", "CBC", "CFB", "OFB"); 
        ChoiceBox <String> encryptionChoiceBox = new ChoiceBox <>(names); 
        
        final FileChooser fileChooser = new FileChooser();
 
        final Button chooseFileButton = new Button("Wybierz plik");
 
        chooseFileButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    File f = fileChooser.showOpenDialog(primaryStage);
                    if (f != null) {
                        file = f;
                        fileNameText.setText(f.getName());  
                    }
                }
            });
        
        
        Button encryptButton = new Button();
        encryptButton.setText("Encrypt");
        encryptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    encrypt(encryptionChoiceBox.getValue());
                } catch (IOException ex) {
                    Logger.getLogger(JavaFXApplication3.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        GridPane gridPane = new GridPane();
        
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        
        gridPane.add(encryptionTypeText, 0, 0);
        gridPane.add(encryptionChoiceBox, 1, 0);
        gridPane.add(chooseFileButton, 0, 1);
        gridPane.add(fileNameText, 1,1);
        gridPane.add(encryptButton, 0, 2);
        
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
    
    private void encrypt(String value) throws IOException {
        if(file != null)
        {
            FileToEncrypt fileToEncrypt = null;
            switch(value){
                case "CBC":
                    System.out.println("tryb szyfrowania cbc");
                    fileToEncrypt = new FileToEncryptCBC(file.getPath());
                    break;
                case "CFB":
                    fileToEncrypt = new FileToEncryptCFB(file.getPath());
                    System.out.println("tryb szyfrowania cfb");
                    break;
                case "ECB":
                    fileToEncrypt = new FileToEncryptECB(file.getPath());
                    System.out.println("tryb szyfrowania ecb");
                    break;
                case "OFB":
                    fileToEncrypt = new FileToEncryptOFB(file.getPath());
                    System.out.println("tryb szyfrowania ofb");
                    break;
                default:
                    System.out.println("wybierz tryb szyfrowania.");
            }
            if (fileToEncrypt != null){
                fileToEncrypt.encryptFile();
            }
        }
    }
}


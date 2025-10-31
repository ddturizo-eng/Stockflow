package com.mycompany.stockflow;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SecondaryController {

    @FXML
    private Button btnCerrarSesion;

    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
    try {
        App.setRoot("Login");
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
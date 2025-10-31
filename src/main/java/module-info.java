module com.mycompany.stockflow {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    // requires javafx.swing;        // ← AGREGAR ESTA LÍNEA
   // requires java.desktop; 

    opens com.mycompany.stockflow to javafx.fxml;
    exports com.mycompany.stockflow;
    requires org.json;
}

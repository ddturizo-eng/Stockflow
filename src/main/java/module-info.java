
module com.mycompany.stockflow {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires org.json;
    
    // ==================== ITEXT 7 MODULES ====================
    requires kernel;
    requires layout;
    requires io;
    requires forms;
    requires pdfa;
    requires sign;
    requires barcodes;
    requires font.asian;
    requires hyph;
    
    // ==================== LOGGING ====================
    requires org.slf4j;
    
    // Exports y opens que ya tenías
    opens com.mycompany.stockflow to javafx.fxml;
   // opens com.mycompany.stockflow.Controllers to javafx.fxml;
    opens com.mycompany.stockflow.Modelo to javafx.base;
    
    exports com.mycompany.stockflow;
    //exports com.mycompany.stockflow.Controllers;
    exports com.mycompany.stockflow.Modelo;
    exports com.mycompany.stockflow.Logica;
    exports com.mycompany.stockflow.Persistencia;
    exports com.mycompany.stockflow.utils;
    exports com.mycompany.stockflow.excepciones;
}
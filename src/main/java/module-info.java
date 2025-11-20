module com.mycompany.stockflow {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.media;
    
    requires org.json;
    
    requires kernel;
    requires layout;
    requires io;
    requires forms;
    requires pdfa;
    requires sign;
    requires barcodes;
    requires font.asian;
    requires hyph;
    
    requires org.slf4j;
    
    requires java.mail;
    requires activation;
    requires webcam.capture;
    
    requires java.base;
    requires java.desktop;
    
    opens com.mycompany.stockflow to javafx.fxml;
    opens com.mycompany.stockflow.Modelo to javafx.base;
    opens com.mycompany.stockflow.Logica to java.mail;
   
    exports com.mycompany.stockflow;
    exports com.mycompany.stockflow.Modelo;
    exports com.mycompany.stockflow.Logica;
    exports com.mycompany.stockflow.Persistencia;
    exports com.mycompany.stockflow.utils;
    exports com.mycompany.stockflow.excepciones;
}
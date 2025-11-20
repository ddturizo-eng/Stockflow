module com.mycompany.stockflow {
    
    // ==================== JAVAFX ====================
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.media;
    
    // ==================== JSON ====================
    requires org.json;
    
    // ==================== ITEXT PDF ====================
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
    
    // ==================== EMAIL / JAVAMAIL ====================
    requires java.mail;
    requires activation;
    
    // ==================== WEBCAM ====================
    requires webcam.capture;
    
    // ==================== JAVA BASE ====================
    requires java.base;
    requires java.desktop;
    
    // ==================== OPENS ====================
    opens com.mycompany.stockflow to javafx.fxml;
    opens com.mycompany.stockflow.Modelo to javafx.base;
    opens com.mycompany.stockflow.Logica to java.mail;
    
    // ==================== EXPORTS ====================
    exports com.mycompany.stockflow;
    exports com.mycompany.stockflow.Modelo;
    exports com.mycompany.stockflow.Logica;
    exports com.mycompany.stockflow.Persistencia;
    exports com.mycompany.stockflow.utils;
    exports com.mycompany.stockflow.excepciones;
}
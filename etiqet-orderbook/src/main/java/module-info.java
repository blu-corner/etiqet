module com.neueda.etiqet.orderbook.etiqetorderbook {
    requires javafx.controls;
    requires javafx.fxml;
        requires javafx.web;
            
            requires com.dlsc.formsfx;
            requires validatorfx;
            requires org.kordamp.ikonli.javafx;
            requires org.kordamp.bootstrapfx.core;
            requires eu.hansolo.tilesfx;
    
    opens com.neueda.etiqet.orderbook.etiqetorderbook to javafx.fxml;
    exports com.neueda.etiqet.orderbook.etiqetorderbook;
}
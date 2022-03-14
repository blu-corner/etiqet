module com.neueda.etiqet.orderbook.etiqetorderbook {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires quickfixj.all;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.apache.commons.lang3;
    requires slf4j.api;

    opens com.neueda.etiqet.orderbook.etiqetorderbook to javafx.fxml;
    exports com.neueda.etiqet.orderbook.etiqetorderbook;
}


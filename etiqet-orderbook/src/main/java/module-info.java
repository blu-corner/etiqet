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
    requires java.desktop;
    requires jfxtras.controls;

    opens com.neueda.etiqet.orderbook.etiqetorderbook to javafx.fxml;
    exports com.neueda.etiqet.orderbook.etiqetorderbook;
    exports com.neueda.etiqet.orderbook.etiqetorderbook.entity;
    opens com.neueda.etiqet.orderbook.etiqetorderbook.entity to javafx.fxml;
    exports com.neueda.etiqet.orderbook.etiqetorderbook.controllers;
    opens com.neueda.etiqet.orderbook.etiqetorderbook.controllers to javafx.fxml;
    exports com.neueda.etiqet.orderbook.etiqetorderbook.fix;
    opens com.neueda.etiqet.orderbook.etiqetorderbook.fix to javafx.fxml;
}


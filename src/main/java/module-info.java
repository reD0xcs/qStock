module com.proiect.p3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens qStock to javafx.fxml;
    exports qStock;
}
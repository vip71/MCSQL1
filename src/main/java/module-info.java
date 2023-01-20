module com.example.mcsql1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires jdk.jfr;
    requires java.desktop;


    opens com.example.mcsql1 to javafx.fxml;
    exports com.example.mcsql1;
}
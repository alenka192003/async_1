module com.example.async_1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.async_1 to javafx.fxml;
    exports com.example.async_1;
}
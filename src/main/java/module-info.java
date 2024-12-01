module org.example.my_project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.swing;

    opens org.example.my_project.UI to javafx.fxml;
    exports org.example.my_project.UI to javafx.graphics;
    exports org.example.my_project.BL;
    opens org.example.my_project.BL to javafx.fxml;
}
package javacode.controllers;

import javacode.classresources.ListCellFile;
import javacode.listcellscontrollers.FileListCellController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainWindowController {
    private Stage STAGE;

    private double xOffset;
    private double yOffset;

    @FXML
    private AnchorPane anchorPaneMainHeader;

    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private Button buttonHeaderMax;

    @FXML
    private Button buttonHeaderClose;

    @FXML
    private Button buttonHeaderMin;

    @FXML
    private AnchorPane anchorPaneHeaderButtons;

    @FXML
    private ListView listViewFiles;


    private void initComponents() {
        anchorPaneHeaderButtons.setOnMouseEntered(event -> {
            buttonHeaderClose.setStyle("-fx-background-image: url(" + getClass().getResource("/resources/img/headerbuttons/close-hover.png") + ");");
            buttonHeaderMin.setStyle("-fx-background-image: url(" + getClass().getResource("/resources/img/headerbuttons/min-hover.png") + ");");
            buttonHeaderMax.setStyle("-fx-background-image: url(" + getClass().getResource("/resources/img/headerbuttons/max-hover.png") + ");");
        });

        anchorPaneHeaderButtons.setOnMouseExited(event -> {
            buttonHeaderClose.setStyle("-fx-background-image: url(" + getClass().getResource("/resources/img/headerbuttons/close-normal.png") + ");");
            buttonHeaderMin.setStyle("-fx-background-image: url(" + getClass().getResource("/resources/img/headerbuttons/min-normal.png") + ");");
            buttonHeaderMax.setStyle("-fx-background-image: url(" + getClass().getResource("/resources/img/headerbuttons/max-normal.png") + ");");
        });

        buttonHeaderClose.setOnAction(event -> STAGE.close());

        buttonHeaderMin.setOnAction(event -> STAGE.setIconified(true));

        buttonHeaderMax.setOnAction(event -> {
            if(STAGE.isMaximized()) {
                anchorPaneMain.setPadding(new Insets(20, 20, 20, 20));
                STAGE.setMaximized(false);
            } else {
                STAGE.setMaximized(true);
                anchorPaneMain.setPadding(new Insets(0, 0, 0, 0));
            }
        });


        anchorPaneMainHeader.setOnMousePressed(event -> {
            if(!STAGE.isMaximized()) {
                xOffset = STAGE.getX() - event.getScreenX();
                yOffset = STAGE.getY() - event.getScreenY();
            }
        });

        anchorPaneMainHeader.setOnMouseDragged(event -> {
            if(!STAGE.isMaximized()) {
                STAGE.setX(event.getScreenX() + xOffset);
                STAGE.setY(event.getScreenY() + yOffset);
            }
        });


    }

    public void Initialization(Stage _stage) {
        STAGE = _stage;
        initComponents();

        /*Временная инициализация*/
        ObservableList<ListCellFile> files = FXCollections.observableArrayList();
        files.add(new ListCellFile(null,"..", 0, "21:10 21.03.2017", true));
        files.add(new ListCellFile(null,"Новая папка", 31100, "21:10 21.03.2017", true));
        files.add(new ListCellFile(null,"Новая папка(1)", 45253, "21:10 21.03.2017", true));
        files.add(new ListCellFile(null,"Новая папка(2)", 145544, "21:10 21.03.2017", true));
        files.add(new ListCellFile(null,"Новый документ.txt", 31100, "21:10 21.03.2017", false));
        files.add(new ListCellFile(null,"Новый документ.pdf", 46645, "21:10 21.03.2017", false));
        files.add(new ListCellFile(null,"Новый документ.docx", 31757100, "21:10 21.03.2017", false));
        files.add(new ListCellFile(null,"Новый документ.docx", 314100, "21:10 21.03.2017", false));
        files.add(new ListCellFile(null,"Новый документ.dll", 37600, "21:10 21.03.2017", false));
        files.add(new ListCellFile(null,"Новый документ.cpp", 75800, "21:10 21.03.2017", false));
        listViewFiles.setItems(files);
        listViewFiles.setCellFactory(slw -> new FileListCellController());
    }

}
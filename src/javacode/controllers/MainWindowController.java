package javacode.controllers;

import javacode.classresources.ListCellFile;
import javacode.listcellscontrollers.FileListCellController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainWindowController {
    private Stage STAGE;

    private double xOffset;
    private double yOffset;

    private FTPClient FTP = new FTPClient();
    private boolean isConnect = false;

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
    private ListView<ListCellFile> listViewFiles;

    @FXML
    private Button buttonUpload;

    @FXML
    private Button buttonDownload;

    @FXML
    private Button buttonAddDirectory;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonUpdate;

    @FXML
    private Button buttonAddResetConnect;

    @FXML
    private GridPane gridPaneLogInBlock;

    @FXML
    private TextField textFieldName;

    @FXML
    private PasswordField textFieldPassword;

    @FXML
    private TextField textFieldIPAddress;

    @FXML
    private Button buttonConnect;

    @FXML
    private Button buttonCancelConnect;

    @FXML
    private Label labelShowError;


    private void initComponents() {
        // Реализация отображения кнопок свернуть, развернуть, закрыть.
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
        //

        // Реализация перемещения окна
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
        //

        // Авторизация
        buttonAddResetConnect.setOnMouseClicked(event -> gridPaneLogInBlock.setVisible(true));
        textFieldIPAddress.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                initConnect();
            }
        });

        textFieldName.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                initConnect();
            }
        });

        textFieldPassword.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                initConnect();
            }
        });

        buttonConnect.setOnMouseClicked(event -> initConnect());

        buttonCancelConnect.setOnMouseClicked(event -> gridPaneLogInBlock.setVisible(false));
        //

    }

    public void Initialization(Stage _stage) {
        STAGE = _stage;
        initComponents();
    }

    private void optionButtonDisable(boolean isDisable) {
        buttonUpload.setDisable(isDisable);
        buttonDownload.setDisable(isDisable);
        buttonAddDirectory.setDisable(isDisable);
        buttonDelete.setDisable(isDisable);
        buttonUpdate.setDisable(isDisable);
    }

    //Функции авторизации
    private void initConnect() {
        if(isInputDataCorrect()) {
            optionButtonDisable(true);
            isConnect = false;
            try {
                if(FTP.isConnected()) FTP.disconnect(); // Если уже есть соединение, то разрываем его
                FTP.connect(textFieldIPAddress.getText()); // Создаем новое и проверяем на корректность
                if(FTP.isConnected()) {
                    if(FTP.login(textFieldName.getText(), textFieldPassword.getText())) // Авторизация
                    {
                        int reply = FTP.getReplyCode();
                        if (FTPReply.isPositiveCompletion(reply)) // Проверяем подключение
                        {
                            FTP.enterLocalPassiveMode();
                            labelShowError.setText("Связь установлена.");
                            isConnect = true;
                            gridPaneLogInBlock.setVisible(false);
                            optionButtonDisable(false);
                            updateFilesTree(); // Обновляем список
                        } else {
                            FTP.logout();
                            FTP.disconnect();
                            labelShowError.setText("Ошибка работы с сервером.");
                        }
                    } else {
                        FTP.logout();
                        labelShowError.setText("Неправильный логин или пароль.");
                    }
                } else {
                    labelShowError.setText("Неудачная попыка подключения.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isInputDataCorrect() { // Проверка всех полей на корректность введенных данных с помощью регулярных выражений
        if(!regex(textFieldIPAddress.getText(), "^(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])(\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])){3}$")){
            labelShowError.setText("Некорректный IP адрес.");
            return false;
        }
        if(!regex(textFieldName.getText(), "^[A-Za-z0-9@#&_]+$")){
            labelShowError.setText("Некорректное имя пользователя.");
            return false;
        }
        if(!regex(textFieldPassword.getText(), "^[A-Za-z0-9_*]+$")){
            labelShowError.setText("Некорректный пароль пользователя.");
            return false;
        }
        labelShowError.setText("");
        return true;
    }

    private boolean regex(String text, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        return m.matches();
    }
    //

    //Фукнции отображения дерева файлов
    private void updateFilesTree() {
        ObservableList<ListCellFile> files = FXCollections.observableArrayList();
        if(isConnect) {
            try {
                FTPFile[] ftpFiles = FTP.listFiles(); // Получаем все файлы папки FTP сервера
                if (ftpFiles != null && ftpFiles.length > 0) {
                    for (FTPFile file : ftpFiles) {
                        if (file.isDirectory()) {
                            files.add(new ListCellFile(null, file.getName(), file.getSize(), null, true));
                        } else if (file.isFile()) {
                            files.add(new ListCellFile(null, file.getName(), file.getSize(), null, false));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        files.sort(Comparator.comparing(ListCellFile::isFolder).reversed().thenComparing(ListCellFile::getName));
        listViewFiles.setItems(files);
        listViewFiles.setCellFactory(slw -> new FileListCellController());
    }
    //

}
package javacode.controllers;

import javacode.classresources.ListCellFile;
import javacode.implementationclasses.FTPUtil;
import javacode.listcellscontrollers.FileListCellController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import java.io.*;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainWindowController {
    private Stage STAGE;

    private double xOffset;
    private double yOffset;

    public FTPClient FTP = new FTPClient();
    private boolean isConnect = false;

    private String nowDirectory = "\\";
    private ListCellFile selectFile = null;

    public long dirSize;
    public long deleteDirSize;

    @FXML
    private AnchorPane anchorPaneMainHeader;

    @FXML
    private TextField textFieldSearch;

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

    @FXML
    private GridPane gridPaneQuestion;

    @FXML
    private Button buttonYes;

    @FXML
    private Button buttonNo;

    @FXML
    private Label labelQuestion;

    @FXML
    private Label labelDesQuestion;

    @FXML
    public GridPane gridPaneProgress;

    @FXML
    public Label labelProgressFile;

    @FXML
    public ProgressBar progressBarProgress;

    @FXML
    private GridPane gridPaneError;

    @FXML
    private Button buttonOK;

    @FXML
    private Label labelError;

    @FXML
    private Label labelDesError;


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

        buttonHeaderClose.setOnAction(event -> System.exit(0));

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

        // Событие закрытия окна
        STAGE.setOnCloseRequest(event -> {
            if(isConnect) {
                try {
                    FTP.logout();
                    FTP.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

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

        // Обновление списка
        buttonUpdate.setOnMouseClicked(event -> updateFilesTree(null));

        listViewFiles.setOnMouseClicked(event->{
            if(event.getClickCount() == 1) {
                if(listViewFiles.getSelectionModel().getSelectedItem() != null) {
                    selectFile = listViewFiles.getSelectionModel().getSelectedItem();
                }
            } else  if(event.getClickCount() == 2) {
                if(selectFile != null) {
                    if(selectFile.isFolder()){
                        updateFilesTree(selectFile.getName());
                    } else {
                        try {
                            showQuestion("Загрузить файл " + selectFile.getName() + "?",
                                    "Если Вы хотите загрузить данный файл в память Вашего компьютер, нажмите кнопку \"Да\". В противном случае нажмите кнопку \"Нет\".",
                                    this.getClass().getDeclaredMethod("downloadFileFromServer", String.class, String.class, long.class, MainWindowController.class),
                                    new Object[]{selectFile.getPath(), selectFile.getName(), selectFile.getSize(), this});
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        // Звкрытие окна вопроса
        buttonNo.setOnMouseClicked(event -> gridPaneQuestion.setVisible(false));

        //Инициализация загрузки файла c сервер
        buttonDownload.setOnMouseClicked(event -> {
            if(selectFile != null && !selectFile.isFolder()) {
                try {
                    showQuestion("Загрузить файл " + selectFile.getName() + "?",
                            "Если Вы хотите загрузить данный файл в память Вашего компьютер, нажмите кнопку \"Да\". В противном случае нажмите кнопку \"Нет\".",
                            this.getClass().getDeclaredMethod("downloadFileFromServer", String.class, String.class, long.class, MainWindowController.class),
                            new Object[]{selectFile.getPath(), selectFile.getName(), selectFile.getSize(), this});
                }catch (Exception e) {
                    e.printStackTrace();
                    showError("Ошибка при загрузке файла", "По каким-то причинам невозможно загрузить данных файл.");
            }}});

        // Отправка файла на сервер
        buttonUpload.setOnMouseClicked(event -> {
            MainWindowController MWC = this;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Открыть файл...");
            File selectedFile = fileChooser.showOpenDialog(STAGE);
            if(selectedFile != null){
                progressBarProgress.setProgress(0);
                labelProgressFile.setText("Отправка файла на сервер: " + selectedFile.getName());
                gridPaneProgress.setVisible(true);
                new Thread(() -> {
                    try {
                        String RemoteFile = nowDirectory + selectedFile.getName();
                        FileInputStream inputStream = new FileInputStream(selectedFile);
                        OutputStream outputStream = FTP.storeFileStream(RemoteFile);
                        byte[] bytesIn = new byte[1024];
                        int read;
                        long allBytesRead = 0;
                        while ((read = inputStream.read(bytesIn)) != -1) {
                            outputStream.write(bytesIn, 0, read);
                            allBytesRead += read;
                            MWC.progressBarProgress.setProgress(allBytesRead/(double)selectedFile.length());
                        }
                        inputStream.close();
                        outputStream.close();
                        Platform.runLater(() -> MWC.gridPaneProgress.setVisible(false));
                        Platform.runLater(() -> MWC.updateFilesTree(null));
                    } catch (Exception e){
                        e.printStackTrace();
                        showError("Ошибка при отправке файла", "По каким-то причинам невозможно отправить данных файл.");
                    }
                }).start();
            }
        });

        // Удаление файла/директории
        buttonDelete.setOnMouseClicked(event -> {
            if(selectFile != null && !Objects.equals(selectFile.getName(), "..")) {
                try {
                    showQuestion("Удалить файл " + selectFile.getName() + "?",
                            "Если Вы хотите удалить данный файл безвозвратно, нажмите кнопку \"Да\". В противном случае нажмите кнопку \"Нет\".",
                            this.getClass().getDeclaredMethod("deleteFileFromServer", String.class, boolean.class, MainWindowController.class),
                            new Object[]{selectFile.getPath(), selectFile.isFolder(), this});
                }catch (Exception e) {
                    e.printStackTrace();
                    showError("Не удается удалить файл.", "По каким-то причинам невозможно удалить файл.");
                }
            }
        });

        buttonOK.setOnMouseClicked(event -> gridPaneError.setVisible(false));
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
                            labelShowError.setText("");
                            isConnect = true;
                            gridPaneLogInBlock.setVisible(false);
                            optionButtonDisable(false);
                            updateFilesTree(null); // Обновляем список
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
                labelShowError.setText("Неудачная попыка подключения.");
            }
        }
    }
        // Проверка всех полей на корректность введенных данных с помощью регулярных выражений
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
    private void updateFilesTree(String directory) {
        if(directory != null) {
            if(Objects.equals(directory, "..")){
                if(!Objects.equals(nowDirectory, "")) {
                        nowDirectory = nowDirectory.substring(0, nowDirectory.substring(0, nowDirectory.length() - 1).lastIndexOf("\\") + 1);
                }
            }
            else {
                nowDirectory += directory + "\\";
            }
        }
        selectFile = null;
        ObservableList<ListCellFile> files = FXCollections.observableArrayList();
        if(isConnect) {
            try {
                FTP.changeWorkingDirectory(nowDirectory);
                FTPFile[] ftpFiles = FTP.listFiles(); // Получаем все файлы папки FTP сервера
                if (ftpFiles != null && ftpFiles.length > 0) {
                    for (FTPFile file : ftpFiles) {
                        if (file.isDirectory()) {
                            if(Objects.equals(file.getName(), ".")) continue;
                            if(Objects.equals(file.getName(), "..") && Objects.equals(nowDirectory, "\\")) continue;
                            files.add(new ListCellFile(nowDirectory +  file.getName(), file.getName(), file.getSize(), null, true));
                        } else if (file.isFile()) {
                            files.add(new ListCellFile(nowDirectory +  file.getName(), file.getName(), file.getSize(), null, false));
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

    // Вывод вопроса
    private void showQuestion (String title, String description, Method method, Object[] params){
        labelQuestion.setText(title);
        labelDesQuestion.setText(description);
        gridPaneQuestion.setVisible(true);
        buttonYes.setOnMouseClicked(event -> {
            gridPaneQuestion.setVisible(false);
            new Thread(()-> {
                try {
                    Class c = method.getDeclaringClass();
                    Object t = c.newInstance();
                    method.setAccessible(true);
                    method.invoke(t, params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        });
    }

    // Загрузка файла с сервера
    private void downloadFileFromServer (String path, String name, long size, MainWindowController MWC){
        try {
            Platform.runLater(() -> {
                MWC.progressBarProgress.setProgress(0);
                MWC.labelProgressFile.setText("Загрузка файла на диск: " + name);
                MWC.gridPaneProgress.setVisible(true);
            });

            File downloadFile = new File(new File("").getAbsolutePath() + "/temp/" + name);
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
            InputStream inputStream = MWC.FTP.retrieveFileStream(path);
            byte[] bytesArray = new byte[1024];
            int bytesRead;
            long allBytesRead = 0;
            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream.write(bytesArray, 0, bytesRead);
                allBytesRead += bytesRead;
                MWC.progressBarProgress.setProgress(allBytesRead/(double)size);
            }
            outputStream.close();
            inputStream.close();

            Platform.runLater(() -> MWC.gridPaneProgress.setVisible(false));

            if (MWC.FTP.completePendingCommand()) {
                Platform.runLater(()-> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Сохранить как...");
                    fileChooser.setInitialFileName(name);
                    File selectedFile = fileChooser.showSaveDialog(MWC.STAGE);
                    if (selectedFile != null) {
                        downloadFile.renameTo(selectedFile);
                    } else {
                        downloadFile.delete();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка при загрузке файла", "По каким-то причинам невозможно загрузить данных файл.");
        }
    }

    private void deleteFileFromServer (String path, boolean isDirectory, MainWindowController MWC){
        try {
            if(isDirectory) {
                Platform.runLater(() -> {
                    MWC.progressBarProgress.setProgress(0);
                    MWC.labelProgressFile.setText("Подготовка к удалению...");
                    MWC.gridPaneProgress.setVisible(true);
                    MWC.deleteDirSize = 0;
                });
                try {
                    MWC.dirSize = FTPUtil.calculateDirectoryInfo(MWC.FTP, path, "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FTPUtil.removeDirectory(MWC, path, "");
                Platform.runLater(() -> MWC.gridPaneProgress.setVisible(false));
            } else {
                MWC.FTP.deleteFile(path);
            }
            Platform.runLater(() -> MWC.updateFilesTree(null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDeleteProgress(String file, long size){
        labelProgressFile.setText("Удаление файла: " + file);
        deleteDirSize += size;
        progressBarProgress.setProgress(deleteDirSize/(double)dirSize);
    }

    public void showError(String title, String description) {
        Platform.runLater(() -> {
            labelError.setText(title);
            labelDesError.setText(description);
            gridPaneError.setVisible(true);
        });
    }

}
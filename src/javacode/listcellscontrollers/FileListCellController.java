package javacode.listcellscontrollers;

import javacode.classresources.ListCellFile;
import javacode.controllers.MainWindowController;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.util.Objects;

public class FileListCellController extends ListCell<ListCellFile> {
    @FXML
    private GridPane gridPane;

    @FXML
    private ImageView icon;

    @FXML
    private Label name;

    @FXML
    private Label type;

    @FXML
    private Label size;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(ListCellFile file, boolean empty) {
        super.updateItem(file, empty);

        if (empty || file == null) {
            setText(null);
            setGraphic(null);
        } else {
            mLLoader = new FXMLLoader(getClass().getResource("/resources/listcells/fxml/file_listcell.fxml"));
            mLLoader.setController(this);

            try {
                mLLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(file.isFolder()) {
                icon.setImage(new Image("/resources/img/fileicons/folder.png"));
                type.setText("");
            } else {
                type.setText(file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase());
            }

            name.setText(file.getName());
            size.setText(stringSize(file.getSize()));

            setText(null);
            setGraphic(gridPane);

            selectedProperty().addListener( (obsVal, oldVal, newVal) -> {
                if(newVal){
                    name.setStyle("-fx-text-fill: white");
                    size.setStyle("-fx-text-fill: white");
                    type.setStyle("-fx-text-fill: white");
                } else {
                    name.setStyle("");
                    size.setStyle("");
                    type.setStyle("");
                }
            });

            widthProperty().addListener((observable, oldValue, newValue) -> {
                gridPane.setPrefWidth(getWidth() - 15);
            });

            ContextMenu contextMenu = new ContextMenu();
            if(!file.isFolder()) {
                MenuItem downloadItem = new MenuItem();
                downloadItem.setText("Загрузить на компьютер");
                downloadItem.setOnAction(event -> {
                    try {
                        MainWindowController.MWC.showQuestion("Загрузить файл " + file.getName() + "?",
                                "Если Вы хотите загрузить данный файл в память Вашего компьютер, нажмите кнопку \"Да\". В противном случае нажмите кнопку \"Нет\".",
                                MainWindowController.MWC.getClass().getDeclaredMethod("downloadFileFromServer", String.class, String.class, long.class, MainWindowController.class),
                                new Object[]{file.getPath(), file.getName(), file.getSize(), MainWindowController.MWC});
                    }catch (Exception e) {
                        MainWindowController.MWC.showError("Ошибка при загрузке файла", "По каким-то причинам невозможно загрузить данных файл.");
                        e.printStackTrace();
                    }
                });
                contextMenu.getItems().add(downloadItem);
            }
            MenuItem deleteItem = new MenuItem();
            deleteItem.setText("Удалить");
            deleteItem.setOnAction(event -> {
                if(!Objects.equals(file.getName(), "..")) {
                    try {
                        MainWindowController.MWC.showQuestion("Удалить файл " + file.getName() + "?",
                                "Если Вы хотите удалить данный файл безвозвратно, нажмите кнопку \"Да\". В противном случае нажмите кнопку \"Нет\".",
                                MainWindowController.MWC.getClass().getDeclaredMethod("deleteFileFromServer", String.class, boolean.class, MainWindowController.class),
                                new Object[]{file.getPath(), file.isFolder(), MainWindowController.MWC});
                    } catch (Exception e) {
                        MainWindowController.MWC.showError("Не удается удалить файл.", "По каким-то причинам невозможно удалить файл.");
                        e.printStackTrace();
                    }
                }
            });
            MenuItem editItem = new MenuItem();
            editItem.setText("Переименовать");
            editItem.setOnAction(event -> {
                if(!Objects.equals(file.getName(), "..")) {
                    MainWindowController.MWC.isFolderRenameFile = file.isFolder();
                    if (file.isFolder()) {
                        MainWindowController.MWC.oldNameRenameFile = file.getName();
                        MainWindowController.MWC.textFieldRename.setText(file.getName());
                    } else {
                        MainWindowController.MWC.oldNameRenameFile = file.getName().substring(0, file.getName().lastIndexOf("."));
                        MainWindowController.MWC.textFieldRename.setText(MainWindowController.MWC.oldNameRenameFile);
                    }
                    MainWindowController.MWC.renameFilePath = file.getPath();
                    MainWindowController.MWC.gridPaneRename.setVisible(true);
                }
            });
            MenuItem newDirItem = new MenuItem();
            newDirItem.setText("Новая папка");
            newDirItem.setOnAction(event -> {
                MainWindowController.MWC.textFieldNameNewDir.setText("");
                MainWindowController.MWC.gridPaneNewDir.setVisible(true);
            });

            contextMenu.getItems().add(deleteItem);
            contextMenu.getItems().add(editItem);
            contextMenu.getItems().add(newDirItem);

            setContextMenu(contextMenu);
        }

    }

    private static String stringSize(long size){
        if (size == 0) {
            return "";
        } else if (size < 1000) {
            return String.valueOf(size) + " Б";
        } else if (size < 1000000) {
            return String.format("%.1f", (float)size/1000) + " КБ";
        } else if (size < 1000000000) {
            return String.format("%.1f", (float)size/1000000) + " МБ";
        } else {
            return String.format("%.1f", (float)size/1000000000) + " ГБ";
        }
    }
}

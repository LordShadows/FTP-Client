package javacode.listcellscontrollers;

import javacode.classresources.ListCellFile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.io.IOException;

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

            widthProperty().addListener((observable, oldValue, newValue) -> {
                gridPane.setPrefWidth(getWidth() - 15);
            });
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

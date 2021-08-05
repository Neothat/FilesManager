package ru.geekbrains;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class Controller {

    @FXML
    public VBox leftPanel;
    @FXML
    public VBox rightPanel;


    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void copyBtnAction(ActionEvent actionEvent) {
        PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        PanelController rightPC = (PanelController) rightPanel.getProperties().get("ctrl");

        if (leftPC.getSelectedFilename() == null && rightPC.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ни один файл не был выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        PanelController sourcePC = null;
        PanelController distinctPC = null;

        if (leftPC.getSelectedFilename() != null) {
            sourcePC = leftPC;
            distinctPC = rightPC;
        }
        if (rightPC.getSelectedFilename() != null) {
            sourcePC = rightPC;
            distinctPC = leftPC;
        }

        Path southPath = Paths.get(sourcePC.getCurrentPath(), sourcePC.getSelectedFilename());
        Path distinctPath = Paths.get(distinctPC.getCurrentPath()).resolve(southPath.getFileName().toString());

        try {
            Files.copy(southPath, distinctPath);
            distinctPC.updateList(Paths.get(distinctPC.getCurrentPath()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось скопирывать указаный файл", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void removeBtnAction(ActionEvent actionEvent) {
        PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        PanelController rightPC = (PanelController) rightPanel.getProperties().get("ctrl");

        if (leftPC.getSelectedFilename() == null && rightPC.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ни один файл не был выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        PanelController removablePC = null;

        if (leftPC.getSelectedFilename() != null) {
            removablePC = leftPC;
        }
        if (rightPC.getSelectedFilename() != null) {
            removablePC = rightPC;
        }

        Path pathToBeDeleted = Paths.get(removablePC.getCurrentPath(), removablePC.getSelectedFilename());

        try {
            Files.walk(pathToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось удалить указаный файл", ButtonType.OK);
            alert.showAndWait();
        }
        removablePC.updateList(Paths.get(removablePC.getCurrentPath()));
    }
}

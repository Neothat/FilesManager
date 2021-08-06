package ru.geekbrains;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;

public class Controller {
    @FXML
    public VBox leftPanel, rightPanel;
    @FXML
    public Button removeButton, moveButton, copyButton;

    private PanelController leftPC = null;
    private PanelController rightPC = null;

    public void btnExitAction() {
        Platform.exit();
    }

    public void buttonAction(ActionEvent actionEvent) {
        if (!definingPanelsAndCheckingSelection()) {
            return;
        }

        String selectedButton = ((Button) actionEvent.getSource()).getId();
        if ((selectedButton.equals("removeButton"))) {
            removeBtnAction();
        } else {
            moveOrRemoveBtnAction(selectedButton);
        }

        leftPC = null;
        rightPC = null;
    }

    private void removeBtnAction() {
        PanelController removablePC = null;

        if (leftPC.getSelectedFilename() != null) {
            removablePC = leftPC;
        }
        if (rightPC.getSelectedFilename() != null) {
            removablePC = rightPC;
        }

        Path pathToBeDeleted = Paths.get(Objects.requireNonNull(removablePC).getCurrentPath(), removablePC.getSelectedFilename());

        try {
            //noinspection ResultOfMethodCallIgnored
            Files.walk(pathToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось удалить указаный файл", ButtonType.OK);
            alert.showAndWait();
        } finally {
            removablePC.updateList(Paths.get(removablePC.getCurrentPath()));
        }
    }

    private void moveOrRemoveBtnAction(String selectedButton) {
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

        Path southPath = Paths.get(Objects.requireNonNull(sourcePC).getCurrentPath(), sourcePC.getSelectedFilename());
        Path distinctPath = Paths.get(Objects.requireNonNull(distinctPC).getCurrentPath()).resolve(southPath.getFileName().toString());

        try {
            if (selectedButton.equals("moveButton")) {
                Files.move(southPath, distinctPath);
            } else if (selectedButton.equals("copyButton")) {
                Files.copy(southPath, distinctPath);
            } else {
                throw new RuntimeException("Неизвестная кнопка");
            }
            distinctPC.updateList(Paths.get(distinctPC.getCurrentPath()));
            sourcePC.updateList(Paths.get(sourcePC.getCurrentPath()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось скопирывать или переместить указаный файл", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private boolean definingPanelsAndCheckingSelection() {
        leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        rightPC = (PanelController) rightPanel.getProperties().get("ctrl");

        if (leftPC.getSelectedFilename() == null && rightPC.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ни один файл не был выбран", ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }
}


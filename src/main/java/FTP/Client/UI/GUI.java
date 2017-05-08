package FTP.Client.UI;

import FTP.Client.ClientImpl;
import FTP.Exceptions.IncorrectArgsException;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

public class GUI extends Application {

    public static void main(String[] args) throws IOException {
        Application.launch(args);
    }

    @Override
    public void start(@NotNull Stage primaryStage) throws IOException {
        primaryStage.setTitle("FTP");
        Label label = new Label("Enter path:");
        TextField pathTextField = new TextField();
        Button getButton = new Button("Get");
        getButton.prefWidthProperty().bind(primaryStage.widthProperty().multiply(3 / 7.));
        Button listButton = new Button("List");
        listButton.prefWidthProperty().bind(primaryStage.widthProperty().multiply(3 / 7.));
        Button goToParentButton = new Button("..");
        goToParentButton.prefWidthProperty().bind(primaryStage.widthProperty().multiply(1 / 7.));
        TableView<ListCommandRespond> tableView = setupTableView();
        tableView.prefHeightProperty().bind(primaryStage.heightProperty().multiply(9 / 10.));
        final Path[] currentPath = new Path[]{Paths.get("").toAbsolutePath()};
        getButton.setOnAction(action -> {
            try {
                executeGet(pathTextField.getText());
            } catch (Exception e) {
                showAlert(e);
            }
        });
        listButton.setOnAction(action -> {
            try {
                System.out.println("currentPath = " + currentPath[0]);
                boolean isZeroSize = executeList(pathTextField.getText(), tableView);
                if (!isZeroSize) {
                    currentPath[0] = Paths.get(pathTextField.getText()).toAbsolutePath();
                    pathTextField.setText(currentPath[0].toString());
                }
                System.out.println("currentPath = " + currentPath[0]);

            } catch (Exception e) {
                showAlert(e);
            }
        });
        goToParentButton.setOnAction(action -> {
            try {
                Path path = Paths.get(pathTextField.getText()).toAbsolutePath().getParent();
                boolean isZeroSize = executeList(path.toString(), tableView);
                if (!isZeroSize) {
                    currentPath[0] = path;
                    pathTextField.setText(currentPath[0].toString());
                }
            } catch (NullPointerException e) {
                //Do nothing
            } catch (Exception e) {
                showAlert(e);
            }
        });
        tableView.setRowFactory( tv -> {
            TableRow<ListCommandRespond> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                try {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        ListCommandRespond rowData = row.getItem();
                        Path path = Paths.get(currentPath[0].toString(), rowData.getPath());
                        if (rowData.getType().equals("Folder")) {
                            boolean isZeroSize = executeList(path.toString(), tableView);
                            if (!isZeroSize) {
                                currentPath[0] = path;
                                pathTextField.setText(currentPath[0].toString());
                            }
                        } else {
                            executeGet(path.toString());
                        }
                    }
                } catch (Exception e) {
                    showAlert(e);
                }
            });
            return row ;
        });
        HBox hbox = new HBox(getButton, listButton, goToParentButton);
        VBox vbox = new VBox(label, pathTextField, hbox, tableView);
        Scene scene = new Scene(vbox, 300, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean executeList(String path, TableView<ListCommandRespond> tableView)
            throws IOException, IncorrectArgsException {
        System.out.println(Paths.get(path).toAbsolutePath().toString());
        String folderContent = new ClientImpl().execute(new String[]{
                "list", Paths.get(path).toAbsolutePath().toString()});

        ObservableList<ListCommandRespond> data = FXCollections.observableArrayList();
        String[] tmp = folderContent.split("\n");
        for (int i = 2; i < tmp.length; i++) {
            if (tmp[i].endsWith(" true")) {
                data.add(new ListCommandRespond(
                        tmp[i].substring(0, tmp[i].length() - 5), "Folder"));
            } else {
                data.add(new ListCommandRespond(
                        tmp[i].substring(0, tmp[i].length() - 6), "File"));
            }
        }
        if (data.size() == 0) {
            showAlert(INFORMATION, "No such folder or empty folder!");
            return true;
        } else {
            tableView.setItems(data);
            return false;
        }
    }

    private void executeGet(String path) throws IOException, IncorrectArgsException {
        System.out.println(Paths.get(path).toAbsolutePath().toString());
        String[] response = new ClientImpl().execute(new String[]{
                "get", Paths.get(path).toAbsolutePath().toString()}).split("\n");
        showAlert(INFORMATION, response[response.length - 1]);
    }

    private void showAlert(Alert.AlertType alertType, String s) {
        showAlert(alertType, null, s);
    }

    private void showAlert(Alert.AlertType alertType, String header, String s) {
        Alert alert = new Alert(alertType);
        alert.setTitle(null);
        alert.setHeaderText(header);
        alert.setContentText(s);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(400, 100);
        alert.showAndWait();
    }

    private void showAlert(Exception e) {
        e.printStackTrace();
        showAlert(ERROR, e.getMessage(), null);
    }

    private TableView<ListCommandRespond> setupTableView() {
        TableView<ListCommandRespond> tableView = new TableView<>();
        TableColumn<ListCommandRespond, String> pathTableColumn = new TableColumn<>("Path");
        TableColumn<ListCommandRespond, String> extraTableColumn = new TableColumn<>("Type");
        pathTableColumn.setCellValueFactory(x ->
                new SimpleObjectProperty<>(x.getValue().getPath()));
        pathTableColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(2 / 3.));
        extraTableColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        extraTableColumn.setCellValueFactory(x ->
                new SimpleObjectProperty<>(x.getValue().getType()));
        extraTableColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(1 / 3.));
        tableView.getColumns().add(pathTableColumn);
        tableView.getColumns().add(extraTableColumn);
        return tableView;
    }

}

class ListCommandRespond {

    private final SimpleStringProperty path;
    private final SimpleStringProperty type;

    ListCommandRespond(String path, String type) {
        this.path = new SimpleStringProperty(path);
        this.type = new SimpleStringProperty(type);
    }

    String getPath() {
        return path.get();
    }

    String getType() {
        return type.get();
    }

}
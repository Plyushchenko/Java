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
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

/** GUI*/
public class GUI extends Application {

    public static void main(@NotNull String[] args) {
        Application.launch(args);
    }

    /**
     * Setup everything.
     * <pre>
     * How-to.
     * Starting from current folder
     * Double click on row:
     * list <- table row with folder
     * get <- table row with file
     * Button pressing:
     * list <- 'List' button
     * get <- 'Get' button
     * go to parent folder (or do nothing if current folder is root folder) <- '..' button
     * Alert window (error/information types)
     * </pre>
     * @param primaryStage Stage
     */
    @Override
    public void start(@NotNull Stage primaryStage) {
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
        //Effectively final trick, sorry
        final Path[] currentPath = new Path[]{Paths.get(System.getProperty("user.home"))};
        pathTextField.setText(currentPath[0].toString());
        getButton.setOnAction(action -> {
            try {
                executeGet(pathTextField.getText());
            } catch (Exception e) {
                showAlert(e);
            }
        });
        listButton.setOnAction(action -> {
            try {
                boolean isZeroSize = executeList(pathTextField.getText(), tableView);
                if (!isZeroSize) {
                    currentPath[0] = Paths.get(pathTextField.getText()).toAbsolutePath();
                    pathTextField.setText(currentPath[0].toString());
                }
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
        tableView.setRowFactory(tv -> {
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
            return row;
        });
        listButton.fire();
        HBox hbox = new HBox(getButton, listButton, goToParentButton);
        VBox vbox = new VBox(label, pathTextField, hbox, tableView);
        Scene scene = new Scene(vbox, 300, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean executeList(@NotNull String path,
                                @NotNull TableView<ListCommandRespond> tableView)
            throws IOException, IncorrectArgsException {
        String folderContent = new ClientImpl().execute(new String[]{
                "list", Paths.get(path).toAbsolutePath().toString()});
        ObservableList<ListCommandRespond> data = FXCollections.observableArrayList();
        String[] tmp = folderContent.split("\n");
        for (int i = 2; i < tmp.length; i++) {
            if (tmp[i].endsWith("true")) {
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

    private void executeGet(@NotNull String path) throws IOException, IncorrectArgsException {
        String[] response = new ClientImpl().execute(new String[]{
                "get", Paths.get(path).toAbsolutePath().toString()}).split("\n");
        showAlert(INFORMATION, response[response.length - 1]);
    }

    private void showAlert(@NotNull Alert.AlertType alertType, @NotNull String s) {
        showAlert(alertType, null, s);
    }

    private void showAlert(@NotNull Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        showAlert(ERROR, e.getMessage(), errors.toString());
    }

    private void showAlert(@NotNull Alert.AlertType alertType, @Nullable String header,
                           @Nullable String s) {
        Alert alert = new Alert(alertType);
        alert.setTitle(null);
        alert.setHeaderText(header);
        alert.setContentText(s);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(400, 100);
        alert.showAndWait();
    }

    @NotNull
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

    @NotNull private final SimpleStringProperty path;
    @NotNull private final SimpleStringProperty type;

    ListCommandRespond(@NotNull String path, @NotNull String type) {
        this.path = new SimpleStringProperty(path);
        this.type = new SimpleStringProperty(type);
    }

    @NotNull String getPath() {
        return path.get();
    }

    @NotNull String getType() {
        return type.get();
    }

}
package client.gui.controllers;

import client.Client;
import client.ConsoleParser;
import client.gui.App;
import client.gui.animation.CoinAnimation;
import client.gui.inter.DataFormatter;
import client.gui.inter.Language;
import client.gui.inter.StringResource;
import client.gui.visualHumanBeing.HumanBeingFactory;
import client.gui.visualHumanBeing.VisualBeing;
import client.gui.visualHumanBeing.VisualCollection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import network.command.Command;
import network.human.Coordinates;
import network.human.HumanBeing;
import network.message.CommandMessage;
import network.storageCommands.Add;
import network.storageCommands.Clear;
import network.storageCommands.ExecuteScript;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class MainSceneController {

    @FXML
    public TextField tf_command;

    @FXML
    public Menu mn_language;

    @FXML
    public ChoiceBox<Language> cb_lang;

    @FXML
    public Tab tab_table;

    @FXML
    public Tab tab_visualisation;

    @FXML
    public Menu mn_profile;

    @FXML
    public Menu mn_history;

    @FXML
    public ListView<String> lv_history;

    @FXML
    public MenuBar mnBar;

    @FXML
    public Button cmd_add;

    @FXML
    public Button cmd_clear;

    @FXML
    public Button cmd_show;

    @FXML
    public Button cmd_script;

    @FXML
    public Button cmd_exit;

    @FXML
    public Button cmd_changeColors;

    @FXML
    public TableView<VisualBeing> table;

    @FXML
    public TableColumn<VisualBeing, String> cl_owner;

    @FXML
    public TableColumn<VisualBeing, Integer> cl_id;

    @FXML
    public TableColumn<VisualBeing, String> cl_name;

    @FXML
    public TableColumn<VisualBeing, Integer> cl_x;

    @FXML
    public TableColumn<VisualBeing, Double> cl_y;

    @FXML
    public TableColumn<VisualBeing, Boolean> cl_realHero;

    @FXML
    public TableColumn<VisualBeing, Boolean> cl_hasToothpick;

    @FXML
    public TableColumn<VisualBeing, Double> cl_impactSpeed;

    @FXML
    public TableColumn<VisualBeing, String> cl_soundtrackName;

    @FXML
    public TableColumn<VisualBeing, Integer> cl_minutesOfWaiting;

    @FXML
    public TableColumn<VisualBeing, String> cl_mood;

    @FXML
    public TableColumn<VisualBeing, Boolean> cl_coolCar;

    @FXML
    public TableColumn<VisualBeing, String> cl_creationDate;

    @FXML
    public Group visual_group;

    @FXML
    public AnchorPane visualPane;

    @FXML
    public Menu mn_help;

    @FXML
    public MenuItem mi_profile;

    @FXML
    public Button btn_logOut;

    @FXML
    public Label lbl_user;

    @FXML
    public TextField tf_user;

    @FXML
    public Label lbl_numOfObjects;

    @FXML
    public TextField tf_numOfObjects;

    @FXML
    public MenuItem mi_add = new MenuItem(StringResource.getBundle().getString("c_add"));

    @FXML
    public BorderPane borderPane_main;

    @FXML
    public MenuItem mi_about;

    public Menu mn_help_help;
    public Menu mn_help_table;
    public TextArea ta_table;
    public Menu mn_help_visualisation;
    public TextArea ta_visualisation;
    public Menu mn_help_ObjectContextMenu;
    public Menu mn_help_info;
    public TextArea ta_info;
    public Menu mn_help_edit;
    public TextArea ta_edit;
    public Menu mn_help_update;
    public TextArea ta_update;
    public Menu mn_help_remove;
    public TextArea ta_remove;
    public Menu mn_help_removeGreaterThis;
    public TextArea ta_removeGreaterThis;
    public Menu mn_help_countBySoundtrackName;
    public TextArea ta_countBySoundtrackName;
    public Menu mn_help_collectionControlBar;
    public Menu mn_help_add;
    public TextArea ta_add;
    public Menu mn_help_clear;
    public TextArea ta_clear;
    public Menu mn_help_myObjects;
    public TextArea ta_myObjects;
    public Menu mn_help_changeColors;
    public TextArea ta_changeColors;
    public Menu mn_help_exit;
    public TextArea ta_exit;

    double x = -1, y = -1;

    public void localize(ResourceBundle res) {

        mn_help_help.setText(res.getString("c_help"));
        mn_help_table.setText(res.getString("c_table"));
        mn_help_visualisation.setText(res.getString("c_visualisation"));
        mn_help_ObjectContextMenu.setText(res.getString("c_objectContextMenu"));
        mn_help_info.setText(res.getString("c_info"));
        mn_help_edit.setText(res.getString("c_edit"));
        mn_help_update.setText(res.getString("c_update"));
        mn_help_remove.setText(res.getString("c_remove"));
        mn_help_removeGreaterThis.setText(res.getString("c_removeGreaterThis"));
        mn_help_countBySoundtrackName.setText(res.getString("c_countBySoundtrackName"));
        mn_help_collectionControlBar.setText(res.getString("c_collectionControlBar"));
        mn_help_add.setText(res.getString("c_add"));
        mn_help_clear.setText(res.getString("c_clear"));
        mn_help_myObjects.setText(res.getString("c_show"));
        mn_help_changeColors.setText(res.getString("c_changeColors"));
        mn_help_exit.setText(res.getString("c_exit"));

        ta_table.setText(res.getString("c_info_table"));
        ta_visualisation.setText(res.getString("c_info_visualisation"));
        ta_info.setText(res.getString("c_info_info"));
        ta_edit.setText(res.getString("c_info_edit"));
        ta_update.setText(res.getString("c_info_update"));
        ta_remove.setText(res.getString("c_info_remove"));
        ta_removeGreaterThis.setText(res.getString("c_info_removeGreaterThis"));
        ta_countBySoundtrackName.setText(res.getString("c_info_countBySoundtrackName"));
        ta_add.setText(res.getString("c_info_add"));
        ta_clear.setText(res.getString("c_info_clear"));
        ta_myObjects.setText(res.getString("c_info_myObjects"));
        ta_changeColors.setText(res.getString("c_info_changeColors"));
        ta_exit.setText(res.getString("c_info_exit"));


        tf_command.setPromptText(res.getString("c_execute"));
        btn_logOut.setText(res.getString("c_logOut"));
        lbl_user.setText(res.getString("c_user"));
        mi_add.setText(res.getString("c_add"));
        mi_about.setText(res.getString("c_about"));
        mn_language.setText(res.getString("c_language"));
        mn_help.setText(res.getString("c_help"));
        mn_profile.setText(res.getString("c_profile"));
        tab_table.setText(res.getString("c_table"));
        tab_visualisation.setText(res.getString("c_visualisation"));
        lbl_numOfObjects.setText(res.getString("c_objects"));
        mn_history.setText(res.getString("c_history"));

        cmd_add.setText(res.getString("c_add"));
        cmd_clear.setText(res.getString("c_clear"));
        cmd_show.setText(res.getString("c_show"));
        cmd_script.setText(res.getString("c_script"));
        cmd_exit.setText(res.getString("c_exit"));
        cmd_changeColors.setText(res.getString("c_changeColors"));

        cl_owner.setText(res.getString("c_owner"));
        cl_name.setText(res.getString("c_name"));
        cl_realHero.setText(res.getString("c_realHero"));
        cl_hasToothpick.setText(res.getString("c_hasToothpick"));
        cl_impactSpeed.setText(res.getString("c_impactSpeed"));
        cl_soundtrackName.setText(res.getString("c_soundtrackName"));
        cl_minutesOfWaiting.setText(res.getString("c_minutesOfWaiting"));
        cl_mood.setText(res.getString("c_mood"));
        cl_coolCar.setText(res.getString("c_coolCar"));
        cl_creationDate.setText(res.getString("c_creationDate"));

        if (cb_lang.getItems().size() > 0)
            cb_lang.getItems().removeAll(Language.getLanguages());
        cb_lang.getItems().addAll(Language.getLanguages());
        cb_lang.getSelectionModel().select(StringResource.getLanguage());

        App.collection.reload();
    }

    private boolean isUpdating;

    public void chooseLanguage() {
        cb_lang.getSelectionModel().select(StringResource.getLanguage());
    }

    public void initUserName(String userName) {
        tf_user.setText(userName);
    }

    public void initHistory() {
        lv_history.getItems().clear();
    }

    public void addHistory(String string) {
        lv_history.getItems().add(0, string);
        if (lv_history.getItems().size() == 6)
            lv_history.getItems().remove(5);
    }

    @FXML
    public void initialize() {
        double size = table.getColumns().stream().mapToDouble(TableColumn::getPrefWidth).sum();
        table.getColumns().forEach(c -> c.setPrefWidth((c.getPrefWidth() / size) * table.getPrefWidth()));

        mn_profile.setOnShowing(e -> {
            tf_user.setText(Client.account.login());
            tf_numOfObjects.setText(String.valueOf(App.collection.getNumOfUserObjects()));
        });

        cb_lang.getItems().addAll(Language.getLanguages());

        System.out.println("hello world");

        visualPane.setOnMouseMoved(e -> {
            tf_command.setPromptText("x: " + e.getX() + "  y: " + e.getY());
        });

        cb_lang.setOnAction(e -> {
            if (!isUpdating) {
                isUpdating = true;
                Language current = cb_lang.getValue();
                ResourceBundle res = StringResource.updateBundle(current);
                Language.update();
                localize(res);
                isUpdating = false;
            }
        });

        App.collection = new VisualCollection(table, visual_group);
        cl_owner.setCellValueFactory(new PropertyValueFactory("owner"));
        cl_id.setCellValueFactory(new PropertyValueFactory("id"));
        cl_name.setCellValueFactory(new PropertyValueFactory("name"));
        cl_x.setCellValueFactory(param -> new ReadOnlyObjectWrapper(new DataFormatter(StringResource.getLocale()).stringOf(param.getValue().getCoordinates().getX())));
        cl_y.setCellValueFactory(param -> new ReadOnlyObjectWrapper(new DataFormatter(StringResource.getLocale()).stringOf(param.getValue().getCoordinates().getY())));
        cl_realHero.setCellValueFactory(new PropertyValueFactory("realHero"));
        cl_hasToothpick.setCellValueFactory(param -> new ReadOnlyObjectWrapper(param.getValue().hasToothpick()));
        cl_impactSpeed.setCellValueFactory(param -> new ReadOnlyObjectWrapper(new DataFormatter(StringResource.getLocale()).stringOf(param.getValue().getImpactSpeed())));
        cl_soundtrackName.setCellValueFactory(new PropertyValueFactory("soundtrackName"));
        cl_minutesOfWaiting.setCellValueFactory(new PropertyValueFactory("minutesOfWaiting"));
        cl_mood.setCellValueFactory(param -> new ReadOnlyObjectWrapper(param.getValue().getMood().toString()));
        cl_coolCar.setCellValueFactory(param -> new ReadOnlyObjectWrapper(param.getValue().getCar().isCool()));
        cl_creationDate.setCellValueFactory(param -> new ReadOnlyObjectWrapper(new DataFormatter(StringResource.getLocale()).stringOf(param.getValue().getCreationDate())));

        cmd_script.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(App.primaryStage);

            String warning = "";
            if (file != null) {
                ResourceBundle res = StringResource.getBundle();

                if (!file.toString().endsWith(".scr")) {
                    warning = res.getString("c_fileMustBeScr");

                } else {
                    try {
                        List<Command> script = ConsoleParser.parseScript(file);

                        if (script == null) {
                            warning = res.getString("c_invalidScript");

                        } else {
                            Client.out.send(new CommandMessage("script", new ExecuteScript(script, Client.account)));
                        }

                    } catch (IOException ioException) {
                        warning = res.getString("c_somethingWentWrong");
                    }
                }
            }

            if (!warning.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(warning);
                alert.setContentText(null);
                alert.showAndWait();
            }
        });

        tf_command.setOnAction(e -> {
            Client.log.info("submit button was pressed");
            Command command = new ConsoleParser().getCommand(tf_command.getText());
            tf_command.clear();
            if (command != null)
                Client.out.send(new CommandMessage("command", command));
        });

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAutoFix(true);
        contextMenu.setAutoHide(true);
        contextMenu.getItems().add(mi_add);
        mi_add.setOnAction(e -> {
            contextMenu.hide();
            HumanBeing humanBeing = HumanBeingFactory.getInstance().getHuman(x, y);
            if (humanBeing != null) {
                Client.out.send(new CommandMessage("add", new Add(new HumanBeing[]{humanBeing}, Client.account)));
            }
            x = -1;
            y = -1;
        });

        visualPane.setOnContextMenuRequested(e -> {
            if (e.getTarget().equals(visualPane)) {
                x = e.getX();
                y = e.getY();
                contextMenu.show(visualPane, e.getScreenX(), e.getScreenY());
            } else {
                x = -1;
                y = -1;
            }
        });

        visualPane.setOnMouseClicked(e -> {
            if (e.isControlDown()) {
                HumanBeing humanBeing = HumanBeing.getHuman();
                humanBeing.setCoordinates(new Coordinates((int) e.getX(), e.getY()));
                Client.out.send(new CommandMessage("add", new Add(new HumanBeing[]{humanBeing}, Client.account)));
            }
        });

        cmd_exit.setOnAction(e -> App.quit());

        cmd_add.setOnAction(e -> {
            HumanBeingFactory factory = HumanBeingFactory.getInstance();
            HumanBeing humanBeing = factory.getHuman();
            if (humanBeing != null) {
                Client.out.send(new CommandMessage("add", new Add(new HumanBeing[]{humanBeing}, Client.account)));
            }
        });

        cmd_clear.setOnAction(e -> Client.out.send(new CommandMessage("clear", new Clear(Client.account))));

        cmd_show.setOnAction(e -> App.collection.animate(h -> Client.account.login().equals(h.getOwner())));

        cmd_changeColors.setOnAction(e -> App.collection.changeColors());

        btn_logOut.setOnAction(e -> App.authenticate());

        mi_about.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UNDECORATED);
            ResourceBundle res = StringResource.getBundle();

            alert.setHeaderText(res.getString("c_about"));
            alert.setContentText(res.getString("c_createdByBurachevsky"));
            alert.show();
            new CoinAnimation(alert.getGraphic()).start();
        });
    }
}
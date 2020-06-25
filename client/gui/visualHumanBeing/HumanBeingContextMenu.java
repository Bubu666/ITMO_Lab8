package client.gui.visualHumanBeing;

import client.Client;
import client.gui.App;
import client.gui.inter.StringResource;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import network.human.HumanBeing;
import network.message.CommandMessage;
import network.storageCommands.Edit;
import network.storageCommands.RemoveById;
import network.storageCommands.RemoveGreater;
import network.storageCommands.Update;

import java.util.ResourceBundle;
import java.util.function.Predicate;

public class HumanBeingContextMenu extends ContextMenu {

    public final MenuItem mi_info = new MenuItem();
    public final MenuItem mi_edit = new MenuItem();
    public final MenuItem mi_update = new MenuItem();
    public final MenuItem mi_remove = new MenuItem();
    public final MenuItem mi_removeGreaterThis = new MenuItem();
    public final MenuItem mi_countBySoundtrackName = new MenuItem();
    private static final Alert warningAlert = new Alert(Alert.AlertType.WARNING);

    private static final HumanBeingFactory factory = HumanBeingFactory.getInstance();

    private VisualBeing humanBeing;

    public HumanBeingContextMenu() {
        this.getItems().addAll(mi_info, mi_edit, mi_update, mi_remove, mi_removeGreaterThis, mi_countBySoundtrackName);
        localize(StringResource.getBundle());

        mi_info.setOnAction(e -> {
            if (humanBeing == null) {
                warningAlert.setContentText("Null humanBeing");
                warningAlert.showAndWait();

            } else {
                factory.init(humanBeing);
                factory.setEditable(false);

                this.hide();
                factory.show();
                factory.setEditable(true);
            }
        });

        mi_edit.setOnAction(e -> {
            if (humanBeing == null) {
                warningAlert.setContentText("Null humanBeing");
                warningAlert.showAndWait();

            } else {
                factory.init(humanBeing);
                factory.setEditable(true);

                this.hide();
                HumanBeing newHumanBeing = factory.getHuman();


                if (newHumanBeing != null) {
                    newHumanBeing.init(humanBeing.getOwner(), humanBeing.getId());
                    Client.out.send(new CommandMessage("edit", new Edit(Client.account, newHumanBeing)));
                    humanBeing = null;
                }
            }
        });
        mi_update.setOnAction(e -> {
            factory.init(null);
            factory.setEditable(true);

            if (humanBeing == null) {
                warningAlert.setContentText("Null humanBeing");
                warningAlert.showAndWait();

            } else {
                this.hide();
                HumanBeing newHumanBeing = factory.getHuman();

                if (newHumanBeing != null) {
                    Client.out.send(new CommandMessage("update",
                            new Update(humanBeing.getId(), newHumanBeing, Client.account)));
                }
                humanBeing = null;
            }
        });
        mi_remove.setOnAction(e -> {
            if (humanBeing == null) {
                warningAlert.setContentText("null HumanBeing");
                warningAlert.showAndWait();

            } else {
                this.hide();

                Client.out.send(new CommandMessage("remove_by_id",
                        new RemoveById(Client.account, humanBeing.getId())));

                humanBeing = null;
            }
        });
        mi_removeGreaterThis.setOnAction(e -> {
            if (humanBeing == null) {
                warningAlert.setContentText("null HumanBeing");
                warningAlert.showAndWait();

            } else {
                this.hide();

                Client.out.send(new CommandMessage("remove_greater",
                        new RemoveGreater(Client.account, humanBeing.toHumanBeingInstance())));

                humanBeing = null;
            }
        });
        mi_countBySoundtrackName.setOnAction(e -> {
            if (humanBeing == null) {
                warningAlert.setContentText("null HumanBeing");
                warningAlert.showAndWait();

            } else {
                this.hide();

                Predicate<VisualBeing> predicate = h -> h.getSoundtrackName().equals(humanBeing.getSoundtrackName());

                int cnt = App.collection.countBy(predicate);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(String.valueOf(cnt));
                alert.setTitle(null);
                alert.showAndWait();

                App.collection.animate(predicate);

                humanBeing = null;
            }
        });
    }

    public void localize(ResourceBundle res) {
        mi_edit.setText(res.getString("c_edit"));
        mi_info.setText(res.getString("c_info"));
        mi_remove.setText(res.getString("c_remove"));
        mi_update.setText(res.getString("c_update"));
        mi_removeGreaterThis.setText(res.getString("c_removeGreaterThis"));
        mi_countBySoundtrackName.setText(res.getString("c_countBySoundtrackName"));
    }

    public void disabled(boolean b) {
        mi_edit.setDisable(b);
        mi_remove.setDisable(b);
        mi_update.setDisable(b);
    }

    public HumanBeingContextMenu(VisualBeing humanBeing) {
        this();
        this.humanBeing = humanBeing;
    }

    public void show(Node node, VisualBeing humanBeing, double screenX, double screenY) {
        localize(StringResource.getBundle());
        this.humanBeing = humanBeing;

        this.show(node, screenX, screenY);
    }
}
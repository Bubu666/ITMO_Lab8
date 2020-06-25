package client.gui.visualHumanBeing;

import client.Client;
import client.gui.animation.GrowAnimation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import network.human.HumanBeing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class VisualCollection {
    private TableView<VisualBeing> tableView;
    private Group visualGroup;
    private ObservableList<Node> visualList;
    private ObservableList<VisualBeing> tableList;
    private static final HashMap<Integer, VisualBeing> visualBeingHashMap = new HashMap<>();


    public VisualCollection(TableView<VisualBeing> table, Group group) {
        this.tableView = table;
        visualGroup = group;
        tableList = table.getItems();
        visualList = visualGroup.getChildren();

        table.setRowFactory(tableView -> {
            final TableRow<VisualBeing> row = new TableRow<>();
            row.setOnContextMenuRequested(e -> {
                VisualBeing visualBeing = row.getItem();
                VisualBeing.contextMenu.show(table, visualBeing, e.getScreenX(), e.getScreenY());
                VisualBeing.contextMenu.disabled(!visualBeing.getOwner().equals(Client.account.login()));
            });
            return row;
        });
    }

    public void add(HumanBeing humanBeing) {
        VisualBeing vb = new VisualBeing(humanBeing);
        visualBeingHashMap.put(vb.getId(), vb);
        visualList.add(vb.outerCircle);
        visualList.add(vb.innerCircle);
        new GrowAnimation(vb.outerCircle).start();
        tableList.add(0, vb);
    }

    public void addAll(HumanBeing[] humanBeings) {
        for (HumanBeing h : humanBeings) {
            this.add(h);
        }
    }

    public void remove(HumanBeing humanBeing) {
        VisualBeing vb = visualBeingHashMap.get(humanBeing.getId());
        tableList.remove(vb);
        visualList.removeAll(vb.innerCircle, vb.outerCircle);
        visualBeingHashMap.remove(vb.getId());
    }

    public void removeAll(int[] ids) {
        for (int i : ids) {
            remove(visualBeingHashMap.get(i));
        }
    }

    public void removeAll(HumanBeing[] humanBeings) {
        for (HumanBeing h : humanBeings) {
            this.remove(h);
        }
    }

    public void replace(int id, HumanBeing humanBeing) {
        this.remove(visualBeingHashMap.get(id));
        this.add(humanBeing);
    }

    public int getNumOfUserObjects() {
        return (int) tableList.stream().filter(h -> h.getOwner().equals(Client.account.login())).count();
    }

    public void clear() {
        visualList.clear();
        tableList.clear();
        visualBeingHashMap.clear();
    }

    public void animate(Predicate<VisualBeing> predicate) {
        tableList.stream().filter(predicate).forEach(e  -> new GrowAnimation(e.outerCircle).start());
    }

    public int countBy(Predicate<VisualBeing> predicate) {
        return (int) tableList.stream().filter(predicate).count();
    }

    public void changeColors() {
        VisualBeing.ownerColor.clear();
        tableList.forEach(VisualBeing::initCircles);
    }

    public void reload() {
        ArrayList<VisualBeing> visualBeings = new ArrayList<>(tableView.getItems().size());
        visualBeings.addAll(tableList);
        tableList.clear();
        tableList.addAll(visualBeings);
    }
}
package client.gui.visualHumanBeing;

import client.Client;
import client.gui.App;
import client.gui.animation.SelectAnimation;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import network.human.HumanBeing;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VisualBeing extends HumanBeing {

    public final Circle innerCircle;
    public final Circle outerCircle;
    public static final Map<String, Color> ownerColor = new HashMap<>();
    private SelectAnimation selectAnimation;

    public static final Alert infoAlert = new Alert(Alert.AlertType.NONE);
    public static Stage infoStage;
    public static Label infoLabel = new Label();
    public static HumanBeingContextMenu contextMenu = new HumanBeingContextMenu();

    static {
        infoAlert.initStyle(StageStyle.UNDECORATED);
        infoAlert.getButtonTypes().add(new ButtonType("Ok"));
        infoStage = new Stage();
        infoStage.initStyle(StageStyle.UNDECORATED);
        infoStage.setResizable(false);
        Scene infoScene = new Scene(infoLabel);
        infoStage.setScene(infoScene);
    }

    public VisualBeing(HumanBeing humanBeing) {
        super(humanBeing);
        innerCircle = new Circle();
        outerCircle = new Circle();
        initCircles();
        initListeners();
    }

    public void initCircles() {
        innerCircle.setRadius((getMinutesOfWaiting() + 10) / 2.0);
        innerCircle.setFill(getInnerColor());
        innerCircle.setCenterX(getCoordinates().getX());
        innerCircle.setCenterY(getCoordinates().getY());

        Color outerColor = ownerColor.get(owner);
        if (outerColor == null) {
            outerColor = getRandomColor();
            ownerColor.put(owner, outerColor);
        }

        outerCircle.setFill(outerColor);
        outerCircle.setRadius(innerCircle.getRadius() + 10);
        outerCircle.setCenterX(innerCircle.getCenterX());
        outerCircle.setCenterY(innerCircle.getCenterY());
    }

    public HumanBeing toHumanBeingInstance() {
        return new HumanBeing(this);
    }

    private void initListeners() {
        selectAnimation = new SelectAnimation(outerCircle);

        innerCircle.setOnMouseEntered(e -> select());

        /*innerCircle.setOnMousePressed(e -> {
            if (!infoStage.isShowing() && e.isPrimaryButtonDown()) {
                infoLabel.setText(this.toString());
                double mX = e.getScreenX();
                double mY = e.getScreenY();
                double x = mX + infoStage.getWidth() > App.screen.getWidth() ? mX - infoStage.getWidth() : mX;
                double y = mY + infoStage.getHeight() > App.screen.getHeight() ? mY - infoStage.getHeight() : mY;
                infoStage.setX(x);
                infoStage.setY(y);
                infoStage.show();
            }
        });

        innerCircle.setOnMouseExited(e -> {
            if (infoStage.isShowing()) infoStage.close();
        });*/

        innerCircle.setOnContextMenuRequested(e -> {
            contextMenu.show(innerCircle, this, e.getScreenX(), e.getScreenY());
            contextMenu.disabled(!this.getOwner().equals(Client.account.login()));
        });
    }

    public Color getRandomColor() {
        Random r = new Random();
        Color color;
        while (true) {
            color = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));
            if (Color.RED.equals(color) || Color.BLACK.equals(color) || Color.LIGHTGREY.equals(color) || Color.AQUAMARINE.equals(color) || ownerColor.containsValue(color)) {
                color = null;
            }
            if (color != null) {
                return color;
            }
        }
    }

    private Color getInnerColor() {
        switch (getMood()) {
            case RAGE:
                return Color.RED;
            case CALM:
                return Color.AQUAMARINE;
            case SADNESS:
                return Color.LIGHTGREY;
            case GLOOM:
                return Color.BLACK;
        }
        return null;
    }

    public void select() {
        selectAnimation.start();
    }
}
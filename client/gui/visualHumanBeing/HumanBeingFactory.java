package client.gui.visualHumanBeing;

import client.Client;
import client.gui.animation.ShakeAnimation;
import client.gui.inter.DataFormatter;
import client.gui.inter.StringResource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DataFormat;
import javafx.stage.Stage;
import network.human.Car;
import network.human.Coordinates;
import network.human.HumanBeing;
import network.human.Mood;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

public class HumanBeingFactory {
    private final static Stage stage = new Stage();
    private static final HumanBeingFactory instance;

    private static ShakeAnimation animation;

    @FXML
    public TextField tf_id;

    @FXML
    public TextField tf_owner;

    @FXML
    public TextField tf_creationDate;

    @FXML
    public Label lbl_id;

    @FXML
    public Label lbl_owner;

    @FXML
    public Label lbl_creationDate;

    @FXML
    public Label lbl_name;

    @FXML
    public Label lbl_coordinates;

    @FXML
    public Label lbl_impactSpeed;

    @FXML
    public Label lbl_soundtrackName;

    @FXML
    public Label lbl_minutesOfWaiting;

    @FXML
    public Label lbl_mood;

    @FXML
    private TextField tf_name;

    @FXML
    private TextField tf_x;

    @FXML
    private TextField tf_y;

    @FXML
    private TextField tf_impactSpeed;

    @FXML
    private TextField tf_soundtrackName;

    @FXML
    private TextField tf_minutesOfWaiting;

    @FXML
    private CheckBox cb_realHero;

    @FXML
    private CheckBox cb_hasToothPick;

    @FXML
    private CheckBox cb_coolCar;

    @FXML
    private ChoiceBox<String> cb_mood;

    @FXML
    private Button btn_create;

    @FXML
    private Button btn_cancel;

    @FXML
    private Button btn_random;

    private HumanBeing result;

    private boolean randomCoords = true;

    static {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(HumanBeingFactory.class.getResource("/scenes/humanBeingFactoryScene.fxml"));
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);

        instance = loader.getController();
    }

    public static HumanBeingFactory getInstance() {
        return instance;
    }

    public void initialize() {
        animation = new ShakeAnimation(btn_create);

        btn_cancel.setOnAction(e -> {
            result = null;
            stage.close();
        });

        btn_create.setOnAction(e -> {
            DataFormatter df = new DataFormatter(StringResource.getLocale());

            String name = tf_name.getText();
            String soundtrackName = tf_soundtrackName.getText();
            if (name.equals("") || soundtrackName.equals("")) {
                result = null;
                animation.start();
                return;
            }

            int x;
            double y;
            double impactSpeed;
            int minutesOfWaiting;

            try {
                x = df.intOf(tf_x.getText());
                y = df.doubleOf(tf_y.getText());
                impactSpeed = df.doubleOf(tf_impactSpeed.getText());
                minutesOfWaiting = df.intOf(tf_minutesOfWaiting.getText());
            } catch (NumberFormatException | ParseException | ClassCastException ex) {
                System.out.println(ex);
                result = null;
                animation.start();
                return;
            }

            Mood mood = Mood.of(cb_mood.getValue());

            result = new HumanBeing(name, new Coordinates(x, y),
                    cb_realHero.isSelected(), cb_hasToothPick.isSelected(),
                    impactSpeed, soundtrackName, minutesOfWaiting, mood,
                    new Car(cb_coolCar.isSelected()));
            stage.close();
        });

        btn_random.setOnAction(e -> instance.random());
    }

    public void show() {
        if (!stage.isShowing()) {
            localize(StringResource.getBundle());
            stage.showAndWait();
        }
        clear();
    }

    public HumanBeing getHuman() {
        if (!stage.isShowing()) {
            localize(StringResource.getBundle());
            stage.showAndWait();
        }
        HumanBeing humanBeing = result;
        clear();
        return humanBeing;
    }

    public HumanBeing getHuman(double x, double y) {
        randomCoords = false;

        DataFormatter df = new DataFormatter(StringResource.getLocale());

        tf_x.setText(df.stringOf((int) x));
        tf_y.setText(df.stringOf(y));

        String autoGen = StringResource.getBundle().getString("c_generatedAutomatically");

        tf_id.setText(autoGen);
        tf_creationDate.setText(autoGen);

        tf_owner.setText(Client.account.login());

        return getHuman();
    }

    private void clear() {
        result = null;
        randomCoords = true;
        tf_id.setText("");
        tf_owner.setText(Client.account.login());
        tf_name.setText("");
        tf_x.setText("");
        tf_y.setText("");
        tf_impactSpeed.setText("");
        tf_minutesOfWaiting.setText("");
        tf_soundtrackName.setText("");
        cb_coolCar.setSelected(false);
        cb_hasToothPick.setSelected(false);
        cb_realHero.setSelected(false);
        tf_creationDate.setText("");
    }

    private void random() {
        init(HumanBeing.getHuman().init(Client.account.login(), 1337));
    }

    public void init(HumanBeing h) {
        if (h == null) {
            clear();
            return;
        }

        Locale locale = StringResource.getLocale();
        DataFormatter df = new DataFormatter(locale);

        tf_owner.setText(h.getOwner());

        if (h.getId() == 1337) {
            String generatedAutomatically = StringResource.getBundle().getString("c_generatedAutomatically");

            tf_id.setText(generatedAutomatically);
            tf_creationDate.setText(generatedAutomatically);
        } else {
            tf_id.setText(df.stringOf(h.getId()));
            tf_creationDate.setText(df.stringOf(h.getCreationDate()));
        }

        tf_name.setText(h.getName());

        if (randomCoords) {
            tf_x.setText(df.stringOf(h.getCoordinates().getX()));
            tf_y.setText(df.stringOf(h.getCoordinates().getY()));
        }

        tf_impactSpeed.setText(df.stringOf(h.getImpactSpeed()));
        tf_minutesOfWaiting.setText(df.stringOf(h.getMinutesOfWaiting()));
        tf_soundtrackName.setText(h.getSoundtrackName());
        cb_coolCar.setSelected(h.getCar().isCool());
        cb_hasToothPick.setSelected(h.hasToothpick());
        cb_realHero.setSelected(h.isRealHero());
        cb_mood.setValue(h.getMood().toString().toLowerCase());
    }

    public void setEditable(boolean b) {
        tf_name.setEditable(b);
        tf_x.setEditable(b);
        tf_y.setEditable(b);
        tf_impactSpeed.setEditable(b);
        tf_minutesOfWaiting.setEditable(b);
        tf_soundtrackName.setEditable(b);
        cb_coolCar.setDisable(!b);
        cb_hasToothPick.setDisable(!b);
        cb_realHero.setDisable(!b);
        cb_mood.setDisable(!b);
        btn_random.setVisible(b);
    }

    public void localize(ResourceBundle res) {
        btn_cancel.setText(res.getString("c_cancel"));
        btn_create.setText(res.getString("c_create"));
        btn_random.setText(res.getString("c_random"));
        lbl_coordinates.setText(res.getString("c_coordinates"));
        lbl_creationDate.setText(res.getString("c_creationDate"));
        lbl_impactSpeed.setText(res.getString("c_impactSpeed"));
        lbl_minutesOfWaiting.setText(res.getString("c_minutesOfWaiting"));
        lbl_mood.setText(res.getString("c_mood"));
        lbl_name.setText(res.getString("c_name"));
        lbl_owner.setText(res.getString("c_owner"));
        lbl_soundtrackName.setText(res.getString("c_soundtrackName"));
    }
}
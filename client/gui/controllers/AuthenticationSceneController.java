package client.gui.controllers;

import client.Client;
import client.gui.App;
import client.gui.animation.ShakeAnimation;
import client.gui.inter.Language;
import client.gui.inter.StringResource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import network.Encryption;
import network.User;
import network.message.AuthorizationMessage;

import java.util.ResourceBundle;

public class AuthenticationSceneController {
    public static boolean isRegistration;

    @FXML
    public AnchorPane pn_authorization;

    @FXML
    public TextField tf_login;

    @FXML
    public TextField tf_password;

    @FXML
    public Button btn_changeMode;

    @FXML
    public Button btn_enter;

    @FXML
    public Label lbl_mode;

    @FXML
    public Button btn_exit;

    @FXML
    public Label lbl_warning;

    @FXML
    public ChoiceBox<Language> cb_lang;


    public void localize(ResourceBundle res) {
        btn_enter.setText(isRegistration ? res.getString("c_register") : res.getString("c_enter"));
        btn_changeMode.setText(isRegistration ? res.getString("c_hasAccount") : res.getString("c_noAccount"));
        lbl_mode.setText(isRegistration ? res.getString("c_registration") : res.getString("c_authorisation"));
        tf_login.setPromptText(res.getString("c_login"));
        tf_password.setPromptText(res.getString("c_password"));
        btn_exit.setText(res.getString("c_exit"));
        if (cb_lang.getItems().size() > 0)
            cb_lang.getItems().removeAll(Language.getLanguages());
        cb_lang.getItems().addAll(Language.getLanguages());
        cb_lang.getSelectionModel().select(StringResource.getLanguage());
    }

    public void chooseLanguage() {
        cb_lang.getSelectionModel().select(StringResource.getLanguage());
    }

    @FXML
    public void onChangeMode() {
        lbl_warning.setText("");
        isRegistration = !isRegistration;
        ResourceBundle res = StringResource.getBundle();
        btn_enter.setText(isRegistration ? res.getString("c_register") : res.getString("c_enter"));
        btn_changeMode.setText(isRegistration ? res.getString("c_hasAccount") : res.getString("c_noAccount"));
        lbl_mode.setText(isRegistration ? res.getString("c_registration") : res.getString("c_authorisation"));
    }


    boolean isUpdating = false;

    @FXML
    public void initialize() {
        cb_lang.getItems().addAll(Language.getLanguages());

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

        pn_authorization.setOnMouseClicked(e -> lbl_warning.setText(""));
    }

    public void showWarning(String warning) {
        lbl_warning.setText(warning);
    }

    @FXML
    public void onEnter() {
        boolean success = true;
        if (tf_login.getText().equals("")) {
            success = false;
            new ShakeAnimation(tf_login).start();
        }
        if (tf_password.getText().equals("")) {
            success = false;
            new ShakeAnimation(tf_password).start();
        }

        if (success) {
            pn_authorization.setDisable(true);

            boolean changingAccount = Client.account != null;

            Client.account = new User(tf_login.getText(), Encryption.SHA_384(tf_password.getText()));

            AuthorizationMessage message = new AuthorizationMessage(Client.account.login(), Client.account.password(), isRegistration);
            message.changingAccount = changingAccount;

            Client.out.send(message);
            Client.selector.wakeup();

            pn_authorization.setDisable(false);
        }
    }

    @FXML
    public void onExit() {
        App.quit();
    }
}
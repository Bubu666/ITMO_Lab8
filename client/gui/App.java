package client.gui;

import client.Client;
import client.command.ClientCommandWorker;
import client.gui.animation.ShakeAnimation;
import client.gui.controllers.AuthenticationSceneController;

import client.gui.controllers.MainSceneController;
import client.gui.visualHumanBeing.VisualCollection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;

import javafx.scene.Scene;

import javafx.stage.Screen;
import javafx.stage.Stage;


import java.io.*;

public class App extends Application {
    public static Stage primaryStage;
    private static Scene authenticationScene;
    private static Scene mainScene;
    public static Rectangle2D screen = Screen.getPrimary().getVisualBounds();

    public static VisualCollection collection;

    public static AuthenticationSceneController authenticationController;
    public static MainSceneController mainController;

    public static void main(String[] args) {
        launch(args);
    }

    public static void onAuthenticationComplete() {
        Client.log.info("before scene change");

        authenticationController.tf_login.clear();
        authenticationController.tf_password.clear();
        primaryStage.setMaximized(true);
        primaryStage.setScene(mainScene);
        mainController.chooseLanguage();
        mainController.initHistory();
        mainController.initUserName(Client.account.login());
        Client.log.info("after scene change");

        authenticationController.pn_authorization.setDisable(false);
        authenticationStage.close();
        primaryStage.show();
    }

    public static void onAuthenticationNotComplete(String cause) {
        ShakeAnimation s1 = new ShakeAnimation(authenticationController.tf_login);
        ShakeAnimation s2 = new ShakeAnimation(authenticationController.tf_password);
        ShakeAnimation s3 = new ShakeAnimation(authenticationController.btn_enter);
        ShakeAnimation.startAll(s1, s2, s3);

        Platform.runLater(() -> authenticationController.showWarning("* " + cause));

        authenticationController.pn_authorization.setDisable(false);
    }

    public static Stage authenticationStage;

    public static void authenticate() {
        if (primaryStage.isShowing()) primaryStage.close();
        if (authenticationStage == null) authenticationStage = new Stage();
        authenticationStage.setScene(authenticationScene);
        authenticationStage.setX((screen.getWidth() - 600) / 2);
        authenticationStage.setY((screen.getHeight() - 400) / 2);
        authenticationStage.setResizable(false);
        authenticationController.chooseLanguage();

        authenticationStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        App.primaryStage = primaryStage;
        loadScenes();

        authenticate();
        authenticationController.pn_authorization.setDisable(true);

        primaryStage.setOnCloseRequest(e -> App.quit());

        Client.mainPool.execute(Client::start);
    }

    public void loadScenes() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("/scenes/authenticationScene.fxml"));
        authenticationScene = new Scene(loader.load());
        authenticationController = loader.getController();

        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scenes/mainScene.fxml"));
        mainScene = new Scene(loader.load());

        mainController = loader.getController();
    }

    public static void addHistory(String command) {
        mainController.addHistory(command);
    }

    public static void quit() {
        ClientCommandWorker.exit();
        Platform.exit();
    }
}
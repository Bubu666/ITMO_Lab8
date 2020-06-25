package client.gui.animation;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class ShakeAnimation extends Animation {
    private final TranslateTransition tt;

    public ShakeAnimation(Node node) {
        tt = new TranslateTransition(Duration.millis(50), node);
        tt.setAutoReverse(true);
        tt.setFromX(0);
        tt.setFromY(0);
        tt.setCycleCount(4);
        tt.setByX(40);
    }

    public void start() {
        tt.playFromStart();
    }

    public static void startAll(ShakeAnimation...animations) {
        for (ShakeAnimation a : animations) {
            a.start();
        }
    }
}

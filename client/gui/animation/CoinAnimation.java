package client.gui.animation;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class CoinAnimation extends Animation {
    private final ScaleTransition st;

    public CoinAnimation(Node node) {
        st = new ScaleTransition(Duration.millis(500), node);
        st.setAutoReverse(true);
        st.setCycleCount(32);
        st.setByX(-1);
    }

    public void start() {
        st.playFromStart();
    }
}
package client.gui.animation;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class SelectAnimation extends Animation {
    private final ScaleTransition st;
    private boolean isRunning;

    public SelectAnimation(Node node) {
        st = new ScaleTransition(Duration.millis(100), node);
        st.setOnFinished(e -> isRunning = false);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.setToX(1.2);
        st.setToY(1.2);
    }

    public void start() {
        if (!isRunning && !protectedAnimations.contains(st.getNode()))  {
            isRunning = true;
            st.playFromStart();
        }
    }
}

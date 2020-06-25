package client.gui.animation;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class GrowAnimation extends Animation {
    private final ScaleTransition st;


    public GrowAnimation(Node node) {
        st = new ScaleTransition(Duration.millis(1000), node);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.setByX(1.3);
        st.setByY(1.3);
        st.setOnFinished(e -> protectedAnimations.remove(node));
    }

    public void start() {
        if (!protectedAnimations.contains(st.getNode())) {
            protectedAnimations.add(st.getNode());
            st.play();
        }
    }
}

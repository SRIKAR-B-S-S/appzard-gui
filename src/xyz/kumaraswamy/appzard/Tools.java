package xyz.kumaraswamy.appzard;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

public class Tools {
    public static void setTopMargin(JComponent component) {
        component.setBorder(BorderFactory.createEmptyBorder(20,
                10, 10, 10));
    }
}

package xyz.kumaraswamy.appzard;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class MouseClickListener implements MouseListener {

    public abstract void whenClick();

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        whenClick();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}

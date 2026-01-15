package ui;

import javax.swing.*;
import java.awt.*;

public class BasePopup extends JDialog {

    protected final int dialogWidth;
    protected final int dialogHeight;

    protected final JPanel root;
    protected final JPanel card;

    public BasePopup(JFrame owner, int width, int height) {
        super(owner, true);
        this.dialogWidth = width;
        this.dialogHeight = height;

        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        setSize(dialogWidth, dialogHeight);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int)(screen.width * 0.59), screen.height / 3);

        root = new JPanel(null);
        root.setOpaque(false);

        card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 35;
                int shadow = 6;

                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(shadow, shadow, w - shadow, h - shadow, arc, arc);

                g2.setColor(new Color(60, 60, 60, 235));
                g2.fillRoundRect(0, 0, w - shadow, h - shadow, arc, arc);
            }
        };
        card.setOpaque(false);
        card.setBounds(0, 0, dialogWidth, dialogHeight);

        root.add(card);
        setContentPane(root);
    }

    protected JButton createCircleCloseButton() {
        JButton btn = new JButton("X") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int d = Math.min(getWidth(), getHeight());
                g2.setColor(new Color(0,0,0,150));
                g2.fillOval(0, 0, d, d);

                g2.setColor(new Color(200,200,200));
                g2.fillOval(2, 2, d-4, d-4);

                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(null);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Black", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
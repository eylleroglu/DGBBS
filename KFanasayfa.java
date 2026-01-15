package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class KFanasayfa extends JFrame {

    private int kullaniciID;

    public KFanasayfa(int kullaniciID) {
        this.kullaniciID = kullaniciID;


        Image bgImage = new ImageIcon(getClass().getResource("/img/walp.jpg")).getImage();
        BackgroundPanel bgPanel = new BackgroundPanel(bgImage);
        setContentPane(bgPanel);

        setTitle("StructCheck - Kurum/Gönüllü Anasayfa");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bgPanel.setLayout(null);


        JButton btnSol = createIconButton(
                "Test Talepleri",
                "/icon/mektupicon.png"
        );
        btnSol.setBounds(370, 250, 280, 280);
        btnSol.addActionListener(e -> {
            Talepler t = new Talepler(kullaniciID); // 🔹 ID taşındı
            t.setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnSol);


        JButton btnSag = createIconButton(
                "Test Sonuçları",
                "/icon/sonucicon.png"
        );
        btnSag.setBounds(950, 250, 280, 280);
        btnSag.addActionListener(e -> {
            Sonuclar s = new Sonuclar(kullaniciID);
            s.setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnSag);
    }


    private JButton createIconButton(String text, String iconPath) {

        ImageIcon icon = null;
        try {
            Image img = new ImageIcon(getClass().getResource(iconPath)).getImage();
            img = img.getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        } catch (Exception ignored) {}

        JButton btn = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 35;
                int shadow = 10;

                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(shadow, shadow, getWidth() - shadow * 2, getHeight() - shadow * 2, arc, arc);

                g2.setColor(new Color(60, 60, 60, 235));
                g2.fillRoundRect(0, 0, getWidth() - shadow * 2, getHeight() - shadow * 2, arc, arc);

                super.paintComponent(g);
            }
        };

        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setFont(new Font("Segoe UI Black", Font.PLAIN, 17));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // Hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setSize(btn.getWidth() + 8, btn.getHeight() + 8);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setSize(btn.getWidth() - 8, btn.getHeight() - 8);
            }
        });

        return btn;
    }


    class BackgroundPanel extends JPanel {
        private final Image image;
        public BackgroundPanel(Image image) { this.image = image; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null)
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
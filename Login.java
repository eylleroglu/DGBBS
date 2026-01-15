package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.text.JTextComponent;

public class Login extends JFrame {

    public Login() {

        Image backgroundImage = new ImageIcon(getClass().getResource("/img/walp.jpg")).getImage();
        BackgroundPanel bgPanel = new BackgroundPanel(backgroundImage);
        setContentPane(bgPanel);

        setTitle("StructCheck - Giriş");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bgPanel.setLayout(null);

        Font modernFont = new Font("Segoe UI Black", Font.PLAIN, 18);

        JPanel panel = new RoundedPanel(50, new Color(60, 60, 60, 235));
        panel.setBounds(500, 120, 550, 550);
        panel.setLayout(null);

        JLabel lblBaslik = new JLabel("KULLANICI GİRİŞİ", SwingConstants.CENTER);
        lblBaslik.setBounds(0, 40, 550, 40);
        lblBaslik.setFont(new Font("Segoe UI Black", Font.BOLD, 26));
        lblBaslik.setForeground(Color.WHITE);
        panel.add(lblBaslik);


        JTextField txtTC = createRoundedField();
        txtTC.setBounds(70, 175, 400, 45);
        String tcPlaceholder = "TC Kimlik Numarası";
        addPlaceholder(txtTC, tcPlaceholder);
        panel.add(txtTC);

        JLabel lblTC = new JLabel("TC:");
        lblTC.setBounds(70, 140, 200, 30);
        lblTC.setFont(modernFont);
        lblTC.setForeground(Color.WHITE);
        panel.add(lblTC);


        JPasswordField txtSifre = createRoundedPasswordField();
        txtSifre.setBounds(70, 275, 400, 45);
        String sifrePlaceholder = "Şifre";
        addPlaceholder(txtSifre, sifrePlaceholder);
        panel.add(txtSifre);

        JLabel lblSifre = new JLabel("Şifre:");
        lblSifre.setBounds(70, 240, 200, 30);
        lblSifre.setFont(modernFont);
        lblSifre.setForeground(Color.WHITE);
        panel.add(lblSifre);


        JButton btnGiris = new JButton("Giriş") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 30;
                int shadow = 6;

                g2.setColor(new Color(0,0,0,70));
                g2.fillRoundRect(0, shadow, getWidth(), getHeight()-shadow, arc, arc);

                g2.setColor(new Color(165,165,165));
                g2.fillRoundRect(0, 0, getWidth(), getHeight()-shadow, arc, arc);

                super.paintComponent(g);
            }
        };

        btnGiris.setBounds(70, 355, 400, 55);
        btnGiris.setFocusPainted(false);
        btnGiris.setForeground(Color.WHITE);
        btnGiris.setFont(modernFont);
        btnGiris.setContentAreaFilled(false);
        btnGiris.setOpaque(false);
        btnGiris.setBorder(BorderFactory.createEmptyBorder());
        panel.add(btnGiris);


        JLabel lblKayitOl = new JLabel("<html><u>Kayıt Ol</u></html>");
        lblKayitOl.setBounds(243, 425, 200, 30);
        lblKayitOl.setForeground(Color.white);
        lblKayitOl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblKayitOl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(lblKayitOl);

        bgPanel.add(panel);


        btnGiris.addActionListener(e -> {

            String tc = txtTC.getText().trim();
            String sifre = new String(txtSifre.getPassword()).trim();


            if(tc.equals(tcPlaceholder)) tc = "";
            if(sifre.equals(sifrePlaceholder)) sifre = "";

            if(tc.isEmpty() || sifre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "TC ve şifre boş bırakılamaz!");
                return;
            }

            if(tc.length() != 11) {
                JOptionPane.showMessageDialog(this, "TC kimlik numarası 11 haneli olmalıdır!");
                return;
            }
             //------------------------------------------------SQL----------------------------------------------------------------------------------------------
            try {
                Connection con = DriverManager.getConnection(
                        "jdbc:sqlserver://localhost:1433;databaseName=DGBBS;encrypt=false",
                        "sa",
                        "250525"
                );

                String sql = "SELECT KullaniciID, Ad, Rol FROM Kullanici WHERE TC = ? AND Sifre = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, tc);
                pst.setString(2, sifre);

                ResultSet rs = pst.executeQuery();

                if(rs.next()) {
                    int kullaniciID = rs.getInt("KullaniciID");
                    String ad = rs.getString("Ad");
                    String rol = rs.getString("Rol");

                    JOptionPane.showMessageDialog(this, "Giriş başarılı! Hoş geldin " + ad);

                    // ROL’E GÖRE YÖNLENDİRME
                    if(rol.equalsIgnoreCase("Bireysel")) {
                        new Kanasayfa(kullaniciID).setVisible(true);
                    } else {
                        new KFanasayfa(kullaniciID).setVisible(true);
                    }

                    this.dispose();

                } else {
                    JOptionPane.showMessageDialog(this, "TC veya Şifre hatalı!");
                }

                con.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Veritabanı Hatası: " + ex.getMessage());
            }
        });
        //--------------------------------------------------------------SQL----------------------------------------------------------------------------------------------


        lblKayitOl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Kkayit().setVisible(true);
                dispose();
            }
        });
    }

    private void addPlaceholder(JTextComponent field, String placeholder) {

        field.setForeground(Color.GRAY);
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if(field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if(field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }


    private JTextField createRoundedField() {
        JTextField txt = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 30;
                int shadow = 4;

                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(2, shadow, getWidth() - 4, getHeight() - shadow - 2, arc, arc);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - shadow, getHeight() - shadow, arc, arc);

                super.paintComponent(g2);
            }
        };
        txt.setOpaque(false);
        txt.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        return txt;
    }

    private JPasswordField createRoundedPasswordField() {
        JPasswordField txt = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 30;
                int shadow = 4;

                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(2, shadow, getWidth() - 4, getHeight() - shadow - 2, arc, arc);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - shadow, getHeight() - shadow, arc, arc);

                super.paintComponent(g2);
            }
        };
        txt.setOpaque(false);
        txt.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        return txt;
    }


    class BackgroundPanel extends JPanel {
        private Image image;
        public BackgroundPanel(Image image) { this.image = image; }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color color;
        RoundedPanel(int radius, Color color) {
            this.radius = radius;
            this.color = color;
            setOpaque(false);
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0,0,18,100));
            g2.fillRoundRect(8, 8, getWidth()-16, getHeight()-16, radius, radius);

            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth()-16, getHeight()-16, radius, radius);

            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
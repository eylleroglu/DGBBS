package ui;

import ui.KFanasayfa;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Gkayit extends JFrame {

    private JTextField txtUniversite;
    private JTextField txtOgrNo;
    private JTextField txtUzmanlik;
    private JTextField txtSinif;

    private int kullaniciID;

    private static final String DB_URL  = "jdbc:sqlserver://localhost:1433;databaseName=DGBBS;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "250525";

    public Gkayit(int kullaniciID) {

        this.kullaniciID = kullaniciID;

        Image backgroundImage = new ImageIcon(getClass().getResource("/img/walp.jpg")).getImage();
        BackgroundPanel bgPanel = new BackgroundPanel(backgroundImage);
        setContentPane(bgPanel);

        setTitle("StructCheck - Gönüllü Kayıt");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bgPanel.setLayout(null);

        Font modernFont = new Font("Segoe UI Black", Font.PLAIN, 18);

        JPanel panel = new RoundedPanel(50, new Color(60, 60, 60, 235));
        panel.setBounds(170, 18, 550, 600);
        panel.setLayout(null);

        JLabel lblBaslik = new JLabel("GÖNÜLLÜ KAYIT", SwingConstants.CENTER);
        lblBaslik.setBounds(0, 20, 550, 40);
        lblBaslik.setFont(new Font("Segoe UI Black", Font.BOLD, 26));
        lblBaslik.setForeground(Color.WHITE);
        panel.add(lblBaslik);

        int xLabel = 70, xField = 70;
        int widthField = 400, heightField = 45, y = 100, gap = 80;

        // Üniversite
        JLabel lblUniversite = new JLabel("Üniversite:");
        lblUniversite.setBounds(xLabel, y - 30, 200, 20);
        lblUniversite.setFont(modernFont);
        lblUniversite.setForeground(Color.WHITE);
        panel.add(lblUniversite);

        txtUniversite = createRoundedField();
        txtUniversite.setBounds(xField, y + 5, widthField, heightField);
        txtUniversite.setText("Üniversite adı");
        txtUniversite.setForeground(Color.GRAY);
        addPlaceholder(txtUniversite, "Üniversite adı");
        panel.add(txtUniversite);

        // Öğrenci No
        y += gap;
        JLabel lblOgrNo = new JLabel("Öğrenci No:");
        lblOgrNo.setBounds(xLabel, y - 30, 200, 20);
        lblOgrNo.setFont(modernFont);
        lblOgrNo.setForeground(Color.WHITE);
        panel.add(lblOgrNo);

        txtOgrNo = createRoundedField();
        txtOgrNo.setBounds(xField, y + 5, widthField, heightField);
        txtOgrNo.setText("Öğrenci numarası");
        txtOgrNo.setForeground(Color.GRAY);
        addPlaceholder(txtOgrNo, "Öğrenci numarası");
        panel.add(txtOgrNo);

        // Uzmanlık Alanı
        y += gap;
        JLabel lblUzmanlik = new JLabel("Uzmanlık Alanı:");
        lblUzmanlik.setBounds(xLabel, y - 30, 200, 20);
        lblUzmanlik.setFont(modernFont);
        lblUzmanlik.setForeground(Color.WHITE);
        panel.add(lblUzmanlik);

        txtUzmanlik = createRoundedField();
        txtUzmanlik.setBounds(xField, y + 5, widthField, heightField);
        txtUzmanlik.setText("İnşaat/Harita/Jeoloji/Mimarlık");
        txtUzmanlik.setForeground(Color.GRAY);
        addPlaceholder(txtUzmanlik, "İnşaat/Harita/Jeoloji/Mimarlık");
        panel.add(txtUzmanlik);

        // Sınıf
        y += gap;
        JLabel lblSinif = new JLabel("Sınıf:");
        lblSinif.setBounds(xLabel, y - 30, 200, 20);
        lblSinif.setFont(modernFont);
        lblSinif.setForeground(Color.WHITE);
        panel.add(lblSinif);

        txtSinif = createRoundedField();
        txtSinif.setBounds(xField, y + 5, widthField, heightField);
        txtSinif.setText("2 - 3 - 4. sınıflar");
        txtSinif.setForeground(Color.GRAY);
        addPlaceholder(txtSinif, "2 - 3 - 4. sınıflar");
        panel.add(txtSinif);

        // Kayıt Tamamlama
        JButton btnKayitOl = new JButton("Kayıt Tamamla") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 70));
                g2.fillRoundRect(0, 6, getWidth(), getHeight() - 6, 30, 30);

                g2.setColor(new Color(165, 165, 165));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - 6, 30, 30);

                super.paintComponent(g);
            }
        };
        btnKayitOl.setBounds(xField, y + 60, widthField, 55);
        btnKayitOl.setForeground(Color.WHITE);
        btnKayitOl.setFont(modernFont);
        btnKayitOl.setContentAreaFilled(false);
        btnKayitOl.setBorder(BorderFactory.createEmptyBorder());
        panel.add(btnKayitOl);

        bgPanel.add(panel);

        btnKayitOl.addActionListener(e -> handleGonulluKayit());
    }

    private void handleGonulluKayit() {

        String universite = txtUniversite.getText().trim();
        String ogrNo      = txtOgrNo.getText().trim();
        String uzmanlik   = txtUzmanlik.getText().trim();
        String sinif      = txtSinif.getText().trim();

        if (
                universite.isEmpty() || ogrNo.isEmpty() || uzmanlik.isEmpty() || sinif.isEmpty() ||
                        universite.equals("Üniversite adı") ||
                        ogrNo.equals("Öğrenci numarası") ||
                        uzmanlik.equals("İnşaat/Harita/Jeoloji/Mimarlık") ||
                        sinif.equals("2 - 3 - 4. sınıflar")
        ) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz.");
            return;
        }

        if (!ogrNo.chars().allMatch(Character::isDigit)) {
            JOptionPane.showMessageDialog(this, "Öğrenci numarası sadece rakamlardan oluşmalıdır.");
            return;
        }

        if (!sinif.chars().allMatch(Character::isDigit)) {
            JOptionPane.showMessageDialog(this, "Sınıf sadece rakamlardan oluşmalıdır.");
            return;
        }

        int sinifDeger = Integer.parseInt(sinif);
        if (sinifDeger < 2 || sinifDeger > 4) {
            JOptionPane.showMessageDialog(this, "Sadece 2, 3 veya 4. sınıf öğrencileri başvuru yapabilir.");
            return;
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            String sql = "INSERT INTO Gonullu (KullaniciID, Universite, OgrenciNo, Uzmanlik, Sinif) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, kullaniciID);
                pst.setString(2, universite);
                pst.setString(3, ogrNo);
                pst.setString(4, uzmanlik);
                pst.setString(5, sinif);
                pst.executeUpdate();
                PreparedStatement logPs = con.prepareStatement(
                        "INSERT INTO LogKaydi (KullaniciID, IslemTuru, TabloAdi, KayitID, Aciklama) " +
                                "VALUES (?, 'INSERT', 'Gonullu', ?, 'Gönüllü kaydı oluşturuldu')"
                );
                logPs.setInt(1, kullaniciID);   // işlemi yapan kullanıcı
                logPs.setInt(2, kullaniciID);   // Gonullu kaydı bu kullanıcıya ait
                logPs.executeUpdate();

            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Veritabanı Hatası: " + ex.getMessage());
            return;
        }

        showSuccessPopupAndGoHome();
    }

    private void showSuccessPopupAndGoHome() {

        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0,0,0,0));

        int dialogWidth = 420, dialogHeight = 220;
        dialog.setSize(dialogWidth, dialogHeight);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation((int)(screen.width*0.63), screen.height/3);

        JPanel root = new JPanel(null);
        root.setOpaque(false);

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0,0,0,120));
                g2.fillRoundRect(6, 6, getWidth()-6, getHeight()-6, 35, 35);

                g2.setColor(new Color(60,60,60,235));
                g2.fillRoundRect(0, 0, getWidth()-6, getHeight()-6, 35, 35);
            }
        };
        card.setBounds(0, 0, dialogWidth, dialogHeight);

        JLabel lbl = new JLabel("Kayıt Tamamlandı", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        lbl.setBounds(0, 35, dialogWidth, 30);
        card.add(lbl);

        JButton btnAnaSayfa = new JButton("Ana Sayfa") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0,0,0,80));
                g2.fillRoundRect(0, 5, getWidth(), getHeight()-5, 25, 25);

                g2.setColor(new Color(165,165,165));
                g2.fillRoundRect(0, 0, getWidth(), getHeight()-5, 25, 25);
            }
        };

        btnAnaSayfa.setBounds((dialogWidth-220)/2, 110, 220, 50);
        btnAnaSayfa.setForeground(Color.WHITE);
        btnAnaSayfa.setFont(new Font("Segoe UI Black", Font.PLAIN, 18));
        btnAnaSayfa.setContentAreaFilled(false);
        btnAnaSayfa.setBorder(BorderFactory.createEmptyBorder());

        btnAnaSayfa.addActionListener(e -> {
            dialog.dispose();
            KFanasayfa ana = new KFanasayfa(kullaniciID);  // 💥 DÜZELTİLDİ — ID ARTIK TAŞIYOR
            ana.setVisible(true);
            this.dispose();
        });

        card.add(btnAnaSayfa);
        root.add(card);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private void addPlaceholder(JTextField field, String text) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(text)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(text);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private JTextField createRoundedField() {
        JTextField txt = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0,0,0,40));
                g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-6, 30, 30);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-6, getHeight()-6, 30, 30);

                super.paintComponent(g2);
            }
        };
        txt.setOpaque(false);
        txt.setBorder(BorderFactory.createEmptyBorder(10,15,10,10));
        return txt;
    }

    class BackgroundPanel extends JPanel {
        private Image image;
        public BackgroundPanel(Image image) { this.image = image; }
        @Override protected void paintComponent(Graphics g) {
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
        @Override protected void paintComponent(Graphics g) {
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
        SwingUtilities.invokeLater(() -> new Gkayit(1).setVisible(true));
    }
}
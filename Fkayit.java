package ui;

import ui.KFanasayfa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Fkayit extends JFrame {

    private JTextField txtKurumAdi;
    private JTextField txtVergiNo;
    private JTextField txtIlce;
    private JTextField txtMetrekareFiyat;
    private JTextArea txtAdres;

    private final int kullaniciId;

    private static final String DB_URL  = "jdbc:sqlserver://localhost:1433;databaseName=DGBBS;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "250525";

    private static final String VERGI_PLACEHOLDER = "Vergi No (10 haneli)";
    private static final String ADRES_PLACEHOLDER = "Adresinizi yazın…";

    public Fkayit(int kullaniciId) {
        this.kullaniciId = kullaniciId;

        Image backgroundImage = new ImageIcon(getClass().getResource("/img/walp.jpg")).getImage();
        BackgroundPanel bgPanel = new BackgroundPanel(backgroundImage);
        setContentPane(bgPanel);

        setTitle("StructCheck - Kurum Kayıt");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bgPanel.setLayout(null);

        Font modernFont = new Font("Segoe UI Black", Font.PLAIN, 18);


        JPanel panel = new RoundedPanel(50, new Color(60, 60, 60, 235));
        panel.setBounds(170, 18, 550, 770);
        panel.setLayout(null);

        JLabel lblBaslik = new JLabel("KURUM KAYIT", SwingConstants.CENTER);
        lblBaslik.setBounds(0, 12, 550, 30);
        lblBaslik.setFont(new Font("Segoe UI Black", Font.BOLD, 26));
        lblBaslik.setForeground(Color.WHITE);
        panel.add(lblBaslik);

        int xLabel = 70;
        int xField = 70;
        int widthField = 400;
        int heightField = 45;
        int y = 80;
        int gap = 80;


        JLabel lblKurumAdi = new JLabel("Kurum Adı:");
        lblKurumAdi.setBounds(xLabel, y - 30, 200, 20);
        lblKurumAdi.setFont(modernFont);
        lblKurumAdi.setForeground(Color.WHITE);
        panel.add(lblKurumAdi);

        txtKurumAdi = createRoundedField();
        txtKurumAdi.setBounds(xField, y + 5, widthField, heightField);
        panel.add(txtKurumAdi);


        y += gap;
        JLabel lblVergiNo = new JLabel("Vergi No:");
        lblVergiNo.setBounds(xLabel, y - 30, 200, 20);
        lblVergiNo.setFont(modernFont);
        lblVergiNo.setForeground(Color.WHITE);
        panel.add(lblVergiNo);

        txtVergiNo = createRoundedField();
        txtVergiNo.setBounds(xField, y + 5, widthField, heightField);
        txtVergiNo.setText(VERGI_PLACEHOLDER);
        txtVergiNo.setForeground(Color.GRAY);

        txtVergiNo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtVergiNo.getText().equals(VERGI_PLACEHOLDER)) {
                    txtVergiNo.setText("");
                    txtVergiNo.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtVergiNo.getText().trim().isEmpty()) {
                    txtVergiNo.setText(VERGI_PLACEHOLDER);
                    txtVergiNo.setForeground(Color.GRAY);
                }
            }
        });
        panel.add(txtVergiNo);


        y += gap;
        JLabel lblIlce = new JLabel("İlçe:");
        lblIlce.setBounds(xLabel, y - 30, 200, 20);
        lblIlce.setFont(modernFont);
        lblIlce.setForeground(Color.WHITE);
        panel.add(lblIlce);

        txtIlce = createRoundedField();
        txtIlce.setBounds(xField, y + 5, widthField, heightField);
        panel.add(txtIlce);


        y += gap;
        JLabel lblAdres = new JLabel("Adres:");
        lblAdres.setBounds(xLabel, y - 30, 200, 20);
        lblAdres.setFont(modernFont);
        lblAdres.setForeground(Color.WHITE);
        panel.add(lblAdres);

        txtAdres = createRoundedTextArea(3);
        txtAdres.setBounds(xField, y + 5, widthField, 90);
        txtAdres.setLineWrap(true);
        txtAdres.setWrapStyleWord(true);
        txtAdres.setText(ADRES_PLACEHOLDER);
        txtAdres.setForeground(Color.GRAY);

        txtAdres.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtAdres.getText().equals(ADRES_PLACEHOLDER)) {
                    txtAdres.setText("");
                    txtAdres.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtAdres.getText().trim().isEmpty()) {
                    txtAdres.setText(ADRES_PLACEHOLDER);
                    txtAdres.setForeground(Color.GRAY);
                }
            }
        });

        panel.add(txtAdres);


        y += gap + 40;
        JLabel lblMetrekare = new JLabel("Metrekare Fiyatı (TL):");
        lblMetrekare.setBounds(xLabel, y - 30, 250, 20);
        lblMetrekare.setFont(modernFont);
        lblMetrekare.setForeground(Color.WHITE);
        panel.add(lblMetrekare);

        txtMetrekareFiyat = createRoundedField();
        txtMetrekareFiyat.setBounds(xField, y + 5, widthField, heightField);
        panel.add(txtMetrekareFiyat);


        JButton btnKayitOl = new JButton("Kayıt Ol") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 30;
                int shadow = 6;

                g2.setColor(new Color(0, 0, 0, 70));
                g2.fillRoundRect(0, shadow, getWidth(), getHeight() - shadow, arc, arc);

                g2.setColor(new Color(165, 165, 165));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - shadow, arc, arc);

                super.paintComponent(g);
            }
        };
        btnKayitOl.setBounds(xField, y + 50, widthField, 55);
        btnKayitOl.setForeground(Color.WHITE);
        btnKayitOl.setFont(modernFont);
        btnKayitOl.setContentAreaFilled(false);
        btnKayitOl.setBorder(BorderFactory.createEmptyBorder());
        panel.add(btnKayitOl);

        bgPanel.add(panel);

        btnKayitOl.addActionListener(e -> handleKayitOl());
    }

    private void handleKayitOl() {
        String kurumAdi = txtKurumAdi.getText().trim();
        String vergiNo  = txtVergiNo.getText().trim();
        String ilce     = txtIlce.getText().trim();
        String adres    = txtAdres.getText().trim();
        String metrekareStr = txtMetrekareFiyat.getText().trim();

        if (VERGI_PLACEHOLDER.equals(vergiNo)) vergiNo = "";
        if (ADRES_PLACEHOLDER.equals(adres)) adres = "";

        if (kurumAdi.isEmpty() || vergiNo.isEmpty() || ilce.isEmpty() ||
                adres.isEmpty() || metrekareStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz.");
            return;
        }

        if (vergiNo.length() != 10 || !vergiNo.chars().allMatch(Character::isDigit)) {
            JOptionPane.showMessageDialog(this, "Vergi numarası 10 haneli olmalıdır.");
            return;
        }

        double metrekareFiyat;
        try {
            metrekareFiyat = Double.parseDouble(metrekareStr.replace(",", "."));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Metrekare fiyatı sayı olmalıdır.");
            return;
        }

        String il = null;

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            String sehirSql = "SELECT Sehir FROM Kullanici WHERE KullaniciID = ?";
            try (PreparedStatement pstSehir = con.prepareStatement(sehirSql)) {
                pstSehir.setInt(1, kullaniciId);
                try (ResultSet rs = pstSehir.executeQuery()) {
                    if (rs.next()) il = rs.getString(1);
                }
            }

            String sql = "INSERT INTO Kurum (KullaniciID, KurumAdi, VergiNo, Il, Ilce, Adres, MetrekareFiyati) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, kullaniciId);
                pst.setString(2, kurumAdi);
                pst.setString(3, vergiNo);
                pst.setString(4, il);
                pst.setString(5, ilce);
                pst.setString(6, adres);
                pst.setDouble(7, metrekareFiyat);
                pst.executeUpdate();
                PreparedStatement logPs = con.prepareStatement(
                        "INSERT INTO LogKaydi (KullaniciID, IslemTuru, TabloAdi, KayitID, Aciklama) " +
                                "VALUES (?, 'INSERT', 'Kurum', ?, 'Kurum kaydı oluşturuldu')"
                );
                logPs.setInt(1, kullaniciId);   // işlemi yapan kullanıcı
                logPs.setInt(2, kullaniciId);   // Kurum kaydı bu kullanıcıya ait
                logPs.executeUpdate();

            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
            return;
        }

        showSuccessPopupAndGoHome();
    }

    private void showSuccessPopupAndGoHome() {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0,0,0,0));

        int dialogWidth  = 420;
        int dialogHeight = 220;
        dialog.setSize(dialogWidth, dialogHeight);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) (screen.width * 0.63);
        int y = screen.height / 3;
        dialog.setLocation(x, y);

        JPanel root = new JPanel(null);
        root.setOpaque(false);

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 35;
                int shadow = 6;

                g2.setColor(new Color(0,0,0,120));
                g2.fillRoundRect(shadow, shadow, w - shadow, h - shadow, arc, arc);

                g2.setColor(new Color(60,60,60,235));
                g2.fillRoundRect(0, 0, w - shadow, h - shadow, arc, arc);
            }
        };
        card.setBounds(0, 0, dialogWidth, dialogHeight);

        JLabel lbl = new JLabel("Kayıt Başarılı", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        lbl.setBounds(0, 35, dialogWidth, 30);
        card.add(lbl);

        JButton btnAnaSayfa = new JButton("Ana Sayfa") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 25;
                int shadow = 5;

                g2.setColor(new Color(0,0,0,80));
                g2.fillRoundRect(0, shadow, getWidth(), getHeight() - shadow, arc, arc);

                g2.setColor(new Color(165,165,165));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - shadow, arc, arc);

                super.paintComponent(g);
            }
        };

        btnAnaSayfa.setBounds((dialogWidth - 220)/2, 110, 220, 50);
        btnAnaSayfa.setForeground(Color.WHITE);
        btnAnaSayfa.setFont(new Font("Segoe UI Black", Font.PLAIN, 18));
        btnAnaSayfa.setContentAreaFilled(false);
        btnAnaSayfa.setBorder(BorderFactory.createEmptyBorder());

        btnAnaSayfa.addActionListener(e -> {
            dialog.dispose();
            KFanasayfa ana = new KFanasayfa(kullaniciId); // ✔ DÜZELTİLMİŞ
            ana.setVisible(true);
            this.dispose();
        });

        card.add(btnAnaSayfa);
        root.add(card);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private JTextField createRoundedField() {
        JTextField txt = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 30;
                int shadow = 4;

                g2.setColor(new Color(0,0,0,40));
                g2.fillRoundRect(2, shadow, getWidth()-4, getHeight()-shadow-2, arc, arc);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-shadow, getHeight()-shadow, arc, arc);

                super.paintComponent(g2);
            }
        };
        txt.setOpaque(false);
        txt.setBorder(BorderFactory.createEmptyBorder(10,15,10,10));
        return txt;
    }

    private JTextArea createRoundedTextArea(int rows) {
        JTextArea txt = new JTextArea(rows, 20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints
                        .VALUE_ANTIALIAS_ON);

                int arc = 30;
                int shadow = 4;

                g2.setColor(new Color(0,0,0,40));
                g2.fillRoundRect(2, shadow, getWidth()-4, getHeight()-shadow-2, arc, arc);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-shadow, getHeight()-shadow, arc, arc);

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
        @Override
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
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0,0,18,100));
            g2.fillRoundRect(8, 8, getWidth()-16, getHeight()-16, radius, radius);

            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth()-16, getHeight()-16, radius, radius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Fkayit(1).setVisible(true));
    }
}
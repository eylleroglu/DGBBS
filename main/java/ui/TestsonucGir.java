package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;

public class TestsonucGir extends JFrame {


    private static final String DB_URL  = "jdbc:sqlserver://localhost:1433;databaseName=DGBBS;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "250525";


    private final int talepID;
    private final int binaID;
    private final int kullaniciID;


    private BackgroundPanel bgPanel;
    private RoundedPanel mainPanel;

    private JTextField txtYapiTuru;
    private JTextField txtZeminYapisi;
    private JTextField txtBinaTuru;
    private JTextField txtYapimYili;
    private JTextField txtKatSayisi;

    private JTextField txtDepremBolgesi;
    private JTextField txtMalzemeKalitesi;
    private JTextField txtKullanimAmaci;
    private JTextField txtHasarGecmisi;

    private JTextArea txtAciklama;

    private JButton btnKaydet;
    private JButton btnIptal;


    private final Font titleFont = new Font("Segoe UI Black", Font.BOLD, 28);
    private final Font labelFont = new Font("Segoe UI Black", Font.PLAIN, 18);

    public TestsonucGir(int talepID, int binaID, int kullaniciID) {
        this.talepID = talepID;
        this.binaID = binaID;
        this.kullaniciID = kullaniciID;

        initUI();
        loadKatSayisiFromDB();

        setVisible(true);
    }

    private void initUI() {
        Image bgImage = new ImageIcon(getClass().getResource("/img/walp.jpg")).getImage();
        bgPanel = new BackgroundPanel(bgImage);
        setContentPane(bgPanel);

        setTitle("StructCheck - Test Sonucu Girişi");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bgPanel.setLayout(null);


        mainPanel = new RoundedPanel(40, new Color(60, 60, 60, 230));
        mainPanel.setLayout(null);
        mainPanel.setBounds(55, 45, 1450, 720);
        bgPanel.add(mainPanel);

        JLabel lblBaslik = new JLabel("TEST SONUCU GİRİŞİ", SwingConstants.CENTER);
        lblBaslik.setFont(titleFont);
        lblBaslik.setForeground(Color.WHITE);
        lblBaslik.setBounds(0, 25, mainPanel.getWidth(), 40);
        mainPanel.add(lblBaslik);

        JLabel lblInfo = new JLabel("Talep ID: " + talepID + "   |   Bina ID: " + binaID, SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblInfo.setForeground(Color.LIGHT_GRAY);
        lblInfo.setBounds(0, 65, mainPanel.getWidth(), 25);
        mainPanel.add(lblInfo);


        int leftX  = 140;
        int rightX = 880;
        int fieldW = 420;
        int fieldH = 48;
        int y = 140;
        int gap = 85;


        txtYapiTuru     = addField("Yapı Türü", leftX, y, fieldW, fieldH); y += gap;
        txtZeminYapisi  = addField("Zemin Yapısı", leftX, y, fieldW, fieldH); y += gap;
        txtBinaTuru     = addField("Bina Türü", leftX, y, fieldW, fieldH); y += gap;
        txtYapimYili    = addField("Yapım Yılı", leftX, y, fieldW, fieldH); y += gap;
        txtKatSayisi    = addField("Kat Sayısı", leftX, y, fieldW, fieldH);
        txtKatSayisi.setEditable(false);


        int yr = 140;
        txtDepremBolgesi     = addField("Deprem Bölgesi (1-5)", rightX, yr, fieldW, fieldH); yr += gap;
        txtMalzemeKalitesi   = addField("Malzeme Kalitesi (1-10)", rightX, yr, fieldW, fieldH); yr += gap;
        txtKullanimAmaci     = addField("Kullanım Amacı", rightX, yr, fieldW, fieldH); yr += gap;
        txtHasarGecmisi      = addField("Hasar Geçmişi", rightX, yr, fieldW, fieldH);


        JLabel lblAciklama = new JLabel("Kısa Açıklama:");
        lblAciklama.setFont(labelFont);
        lblAciklama.setForeground(Color.WHITE);
        lblAciklama.setBounds(140, 540, 300, 25);
        mainPanel.add(lblAciklama);


        txtAciklama = new JTextArea();
        txtAciklama.setLineWrap(true);
        txtAciklama.setWrapStyleWord(true);
        txtAciklama.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAciklama.setOpaque(false);
        txtAciklama.setBorder(
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        );


        JScrollPane spAciklama = new JScrollPane(txtAciklama);
        spAciklama.setOpaque(false);
        spAciklama.getViewport().setOpaque(false);
        spAciklama.setBorder(null);


        RoundedPanel aciklamaPanel =
                new RoundedPanel(25, Color.WHITE);
        aciklamaPanel.setLayout(new BorderLayout());
        aciklamaPanel.setBounds(140, 568, 1020, 100);
        aciklamaPanel.add(spAciklama);

        mainPanel.add(aciklamaPanel);

        btnIptal = createRoundedButton("İptal");
        btnIptal.setBounds(1200, 560, 180, 55);
        btnIptal.addActionListener(e -> dispose());
        mainPanel.add(btnIptal);

        btnKaydet = createRoundedButton("Kaydet");
        btnKaydet.setBounds(1200, 630, 220, 55);
        btnKaydet.addActionListener(this::handleKaydet);
        mainPanel.add(btnKaydet);
    }


    private JTextField addField(String label, int x, int y, int w, int h) {
        JLabel l = new JLabel(label + ":");
        l.setFont(labelFont);
        l.setForeground(Color.WHITE);
        l.setBounds(x, y - 28, 420, 25);
        mainPanel.add(l);

        JTextField f = createRoundedField();
        f.setBounds(x, y, w, h);
        mainPanel.add(f);
        return f;
    }

    private void loadKatSayisiFromDB() {
        String sql = "SELECT KatSayisi FROM Bina WHERE BinaID = ?";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, binaID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int kat = rs.getInt("KatSayisi");
                    if (!rs.wasNull()) {
                        txtKatSayisi.setText(String.valueOf(kat));
                    }
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Kat sayısı yüklenirken hata oluştu:\n" + ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void handleKaydet(ActionEvent e) {
        String yapiTuru         = txtYapiTuru.getText().trim();
        String zeminYapisi      = txtZeminYapisi.getText().trim();
        String binaTuru         = txtBinaTuru.getText().trim();
        String yapimYiliStr     = txtYapimYili.getText().trim();
        String katSayisiStr     = txtKatSayisi.getText().trim();
        String depremBolgesiStr = txtDepremBolgesi.getText().trim();
        String malzemeKalStr    = txtMalzemeKalitesi.getText().trim();
        String kullanimAmaci    = txtKullanimAmaci.getText().trim();
        String hasarGecmisi     = txtHasarGecmisi.getText().trim();
        String aciklama         = txtAciklama.getText().trim();

        if (yapiTuru.isEmpty() || zeminYapisi.isEmpty() || binaTuru.isEmpty()
                || yapimYiliStr.isEmpty() || katSayisiStr.isEmpty()
                || depremBolgesiStr.isEmpty() || malzemeKalStr.isEmpty()
                || kullanimAmaci.isEmpty() || hasarGecmisi.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Lütfen tüm alanları doldurunuz.",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int yapimYili;
        int katSayisi;
        int depremBolgesi;
        int malzemeKal;

        try {
            yapimYili      = Integer.parseInt(yapimYiliStr);
            katSayisi      = Integer.parseInt(katSayisiStr);
            depremBolgesi  = Integer.parseInt(depremBolgesiStr);
            malzemeKal     = Integer.parseInt(malzemeKalStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Sayısal alanlar (yapım yılı, kat sayısı, deprem bölgesi, malzeme kalitesi) geçerli sayı olmalıdır.",
                    "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int currentYear = LocalDate.now().getYear();
        int yas = Math.max(0, currentYear - yapimYili);

        int riskPuani = 0;
        riskPuani += katSayisi * 2;
        riskPuani += depremBolgesi * 5;
        riskPuani += Math.max(0, 11 - malzemeKal) * 3;
        riskPuani += yas / 3;

        if (riskPuani < 0)  riskPuani = 0;
        if (riskPuani > 100) riskPuani = 100;

        String sonuc = (riskPuani >= 50) ? "Riskli" : "Güvenli";

        int donusumPuani = 0;
        donusumPuani += Math.min(30, hasarGecmisi.length() / 5);
        donusumPuani += Math.min(20, kullanimAmaci.length() / 5);
        donusumPuani += riskPuani / 2;
        if (donusumPuani > 100) donusumPuani = 100;

        if (aciklama.isEmpty()) aciklama = hasarGecmisi;

        Connection con = null;
        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            con.setAutoCommit(false);

            int sonucID;

            String sqlTestSonucu =
                    "INSERT INTO TestSonucu (TalepID, TestTarihi, Sonuc, Aciklama, RiskPuani, DonusumPuani) " +
                            "OUTPUT INSERTED.SonucID " +
                            "VALUES (?, GETDATE(), ?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(sqlTestSonucu)) {
                ps.setInt(1, talepID);
                ps.setString(2, sonuc);
                ps.setString(3, aciklama);
                ps.setInt(4, riskPuani);
                ps.setInt(5, donusumPuani);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("TestSonucu eklenemedi, SonucID alınamadı.");
                    sonucID = rs.getInt(1);

                }
            }

            String sqlRisk =
                    "INSERT INTO RiskAnalizi " +
                            " (SonucID, KatSayisi, YapiTuru, ZeminYapisi, BinaTuru, YapimYili, DepremBolgesi, MalzemeKalitesi, BinaID) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(sqlRisk)) {
                ps.setInt(1, sonucID);
                ps.setInt(2, katSayisi);
                ps.setString(3, yapiTuru);
                ps.setString(4, zeminYapisi);
                ps.setString(5, binaTuru);
                ps.setInt(6, yapimYili);
                ps.setInt(7, depremBolgesi);
                ps.setInt(8, malzemeKal);
                ps.setInt(9, binaID);
                ps.executeUpdate();
            }

            String sqlDonusum =
                    "INSERT INTO KentselDonusum (BinaID, KullanimAmaci, HasarGecmisi, SonucID) " +
                            "VALUES (?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(sqlDonusum)) {
                ps.setInt(1, binaID);
                ps.setString(2, kullanimAmaci);
                ps.setString(3, hasarGecmisi);
                ps.setInt(4, sonucID);
                ps.executeUpdate();
            }

            String sqlBina =
                    "UPDATE Bina SET SonTestTarihi = GETDATE(), SonTestDurumu = ? WHERE BinaID = ?";

            try (PreparedStatement ps = con.prepareStatement(sqlBina)) {
                ps.setString(1, sonuc);
                ps.setInt(2, binaID);
                ps.executeUpdate();
            }

            String sqlTalep =
                    "UPDATE TestTalebi SET Durum = 'Tamamlandı' WHERE TalepID = ?";

            try (PreparedStatement ps = con.prepareStatement(sqlTalep)) {
                ps.setInt(1, talepID);
                ps.executeUpdate();
            }

            // LOG KAYITLARI
            logKaydiEkle(con, kullaniciID, "INSERT", "TestSonucu", sonucID,
                    "Test sonucu kaydedildi");

            logKaydiEkle(con, kullaniciID, "INSERT", "RiskAnalizi", sonucID,
                    "Risk analizi kaydı oluşturuldu");

            logKaydiEkle(con, kullaniciID, "INSERT", "KentselDonusum", binaID,
                    "Kentsel dönüşüm değerlendirmesi oluşturuldu");

            logKaydiEkle(con, kullaniciID, "UPDATE", "Bina", binaID,
                    "Bina test durumu güncellendi");

            logKaydiEkle(con, kullaniciID, "UPDATE", "TestTalebi", talepID,
                    "Test talebi tamamlandı");

            con.commit();

            JOptionPane.showMessageDialog(this,
                    "Test sonucu başarıyla kaydedildi.\nSonuç: " + sonuc +
                            "\nRisk Puanı: " + riskPuani +
                            "\nDönüşüm Puanı: " + donusumPuani,
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (Exception ex) {
            try { if (con != null) con.rollback(); } catch (Exception ignore) {}

            JOptionPane.showMessageDialog(this,
                    "Test sonucu kaydedilirken hata oluştu:\n" + ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);

        } finally {
            try { if (con != null) con.close(); } catch (Exception ignore) {}
        }
    }

    private void logKaydiEkle(Connection con,
                              int kullaniciID,
                              String islemTuru,
                              String tabloAdi,
                              Integer kayitID,
                              String aciklama) throws SQLException {

    }


    private JTextField createRoundedField() {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 28;
                int shadow = 5;

                // gölge
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillRoundRect(shadow, shadow, getWidth() - shadow - 1, getHeight() - shadow - 1, arc, arc);

                // arka zemin
                g2.setColor(new Color(255, 255, 255, 235));
                g2.fillRoundRect(0, 0, getWidth() - shadow, getHeight() - shadow, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tf.setForeground(Color.DARK_GRAY);
        tf.setCaretColor(Color.DARK_GRAY);
        return tf;
    }

    private JButton createRoundedButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 28;
                int shadow = 6;

                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillRoundRect(shadow, shadow, getWidth() - shadow, getHeight() - shadow, arc, arc);

                g2.setColor(new Color(170, 170, 170, 220));
                g2.fillRoundRect(0, 0, getWidth() - shadow, getHeight() - shadow, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Black", Font.PLAIN, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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


    class RoundedPanel extends JPanel {
        private final int radius;
        private final Color color;
        public RoundedPanel(int radius, Color color) {
            this.radius = radius;
            this.color = color;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 18, 110));
            g2.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, radius, radius);

            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth() - 20, getHeight() - 20, radius, radius);
        }
    }


    class RoundedBorder implements javax.swing.border.Border {
        private final int r;
        private final Color c;
        RoundedBorder(int r, Color c) { this.r = r; this.c = c; }
        public Insets getBorderInsets(Component comp) { return new Insets(10, 10, 10, 10); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component comp, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c);
            g2.drawRoundRect(x, y, width - 1, height - 1, r, r);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TestsonucGir(1, 1, 1).setVisible(true));
    }
}
package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Testtalep extends JFrame {

    private JTextField txtIl, txtIlce, txtMahalle, txtSokak, txtBinaNo, txtBinaAdi, txtKatSayisi;

    private static final String DB_URL  = "jdbc:sqlserver://localhost:1433;databaseName=DGBBS;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "250525";


    private final int kullaniciID;


    public Testtalep(int kullaniciID) {
        this.kullaniciID = kullaniciID;


        Image backgroundImage = new ImageIcon(getClass().getResource("/img/walp.jpg")).getImage();
        BackgroundPanel bgPanel = new BackgroundPanel(backgroundImage);
        setContentPane(bgPanel);

        setTitle("StructCheck - Test Talebi");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bgPanel.setLayout(null);

        Font modernFont = new Font("Segoe UI Black", Font.PLAIN, 18);


        JPanel panel = new RoundedPanel(50, new Color(60, 60, 60, 235));
        panel.setBounds(170, 18, 550, 770);
        panel.setLayout(null);

        JLabel lblBaslik = new JLabel("BINA BILGILERI", SwingConstants.CENTER);
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


        JLabel lblIl = new JLabel("İl:");
        lblIl.setBounds(xLabel, y - 30, 200, 20);
        lblIl.setFont(modernFont);
        lblIl.setForeground(Color.WHITE);
        panel.add(lblIl);

        txtIl = createRoundedField();
        txtIl.setBounds(xField, y + 5, widthField, heightField);
        panel.add(txtIl);


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
        JLabel lblMahalle = new JLabel("Mahalle:");
        lblMahalle.setBounds(xLabel, y - 30, 200, 20);
        lblMahalle.setFont(modernFont);
        lblMahalle.setForeground(Color.WHITE);
        panel.add(lblMahalle);

        txtMahalle = createRoundedField();
        txtMahalle.setBounds(xField, y + 5, widthField, heightField);
        panel.add(txtMahalle);


        y += gap;
        JLabel lblSokak = new JLabel("Sokak:");
        lblSokak.setBounds(xLabel, y - 30, 200, 20);
        lblSokak.setFont(modernFont);
        lblSokak.setForeground(Color.WHITE);
        panel.add(lblSokak);

        txtSokak = createRoundedField();
        txtSokak.setBounds(xField, y + 5, widthField, heightField);
        panel.add(txtSokak);


        y += gap;
        JLabel lblBinaNo = new JLabel("Bina No:");
        lblBinaNo.setBounds(xLabel, y - 30, 200, 20);
        lblBinaNo.setFont(modernFont);
        lblBinaNo.setForeground(Color.WHITE);
        panel.add(lblBinaNo);

        txtBinaNo = createRoundedField();
        txtBinaNo.setBounds(xField, y + 5, widthField, heightField);
        panel.add(txtBinaNo);


        y += gap;
        JLabel lblBinaAdi = new JLabel("Bina Adı:");
        lblBinaAdi.setBounds(xLabel, y - 30, 200, 20);
        lblBinaAdi.setFont(modernFont);
        lblBinaAdi.setForeground(Color.WHITE);
        panel.add(lblBinaAdi);

        txtBinaAdi = createRoundedField();
        txtBinaAdi.setBounds(xField, y + 5, widthField, heightField);
        panel.add(txtBinaAdi);


        y += gap;
        JLabel lblKat = new JLabel("Kat Sayısı:");
        lblKat.setBounds(xLabel, y - 30, 200, 20);
        lblKat.setFont(modernFont);
        lblKat.setForeground(Color.WHITE);
        panel.add(lblKat);

        txtKatSayisi = createRoundedField();
        txtKatSayisi.setBounds(xField, y + 5, widthField, heightField);
        panel.add(txtKatSayisi);


        JButton btnSorgula = new JButton("SORGULA") {
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
        btnSorgula.setBounds(xField, y + 70, widthField, 55);
        btnSorgula.setFocusPainted(false);
        btnSorgula.setForeground(Color.WHITE);
        btnSorgula.setFont(modernFont);
        btnSorgula.setContentAreaFilled(false);
        btnSorgula.setBorder(BorderFactory.createEmptyBorder());
        btnSorgula.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(btnSorgula);

        bgPanel.add(panel);


        btnSorgula.addActionListener(e -> handleSorgula());
    }


    private void handleSorgula() {
        String il       = txtIl.getText().trim();
        String ilce     = txtIlce.getText().trim();
        String mahalle  = txtMahalle.getText().trim();
        String sokak    = txtSokak.getText().trim();
        String binaNo   = txtBinaNo.getText().trim();
        String binaAdi  = txtBinaAdi.getText().trim();
        String katStr   = txtKatSayisi.getText().trim();

        if (il.isEmpty() || ilce.isEmpty() || mahalle.isEmpty() || sokak.isEmpty() || binaNo.isEmpty()) {
            showPopup("<html>Lütfen zorunlu alanları doldurunuz.</html>", -1);
            return;
        }

        Integer katSayisi = null;
        if (!katStr.isEmpty()) {
            try {
                katSayisi = Integer.parseInt(katStr);
            } catch (NumberFormatException ex) {
                showPopup("<html>Kat sayısı sayı olmalıdır.</html>", -1);
                return;
            }
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            int binaID;


            String selectSql =
                    "SELECT BinaID, SonTestTarihi, SonTestDurumu " +
                            "FROM Bina " +
                            "WHERE Il COLLATE Turkish_CI_AS = ? " +
                            "AND Ilce COLLATE Turkish_CI_AS = ? " +
                            "AND Mahalle COLLATE Turkish_CI_AS = ? " +
                            "AND Sokak COLLATE Turkish_CI_AS = ? " +
                            "AND BinaNumarasi COLLATE Turkish_CI_AS = ?";   // <<< DÜZELTİLEN YER

            try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                ps.setString(1, il);
                ps.setString(2, ilce);
                ps.setString(3, mahalle);
                ps.setString(4, sokak);
                ps.setString(5, binaNo);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        // Bina yok → yeni bina kaydet, binaID'yi al
                        String insertSql =
                                "INSERT INTO Bina (Il, Ilce, Mahalle, Sokak, BinaNumarasi, BinaAdi, KatSayisi, SonTestTarihi, SonTestDurumu) " +
                                        "OUTPUT INSERTED.BinaID " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, NULL, NULL)";

                        try (PreparedStatement ins = con.prepareStatement(insertSql)) {
                            ins.setString(1, il);
                            ins.setString(2, ilce);
                            ins.setString(3, mahalle);
                            ins.setString(4, sokak);
                            ins.setString(5, binaNo);
                            ins.setString(6, binaAdi.isEmpty() ? null : binaAdi);
                            if (katSayisi != null) {
                                ins.setInt(7, katSayisi);
                            } else {
                                ins.setNull(7, Types.INTEGER);
                            }

                            try (ResultSet rsIns = ins.executeQuery()) {
                                if (rsIns.next()) {
                                    binaID = rsIns.getInt(1);

                                    PreparedStatement logPs = con.prepareStatement(
                                            "INSERT INTO LogKaydi (KullaniciID, IslemTuru, TabloAdi, KayitID, Aciklama) " +
                                                    "VALUES (?, ?, ?, ?, ?)"
                                    );
                                    logPs.setInt(1, kullaniciID);
                                    logPs.setString(2, "INSERT");
                                    logPs.setString(3, "Bina");
                                    logPs.setInt(4, binaID);
                                    logPs.setString(5, "Yeni bina kaydı oluşturuldu");
                                    logPs.executeUpdate();

                                } else {
                                    showPopup("<html>Bina kaydedilirken hata oluştu.</html>", -1);
                                    return;
                                }
                            }
                        }

                        showPopup("<html>Geçmiş test bulunamadı.</html>", binaID);

                    } else {
                        // Bina var → test bilgisine bak
                        binaID = rs.getInt("BinaID");
                        Timestamp ts = rs.getTimestamp("SonTestTarihi");
                        String durum = rs.getString("SonTestDurumu");

                        if (ts == null && (durum == null || durum.isEmpty())) {
                            showPopup("<html>Geçmiş test bulunamadı.</html>", binaID);
                        } else {
                            String tarihStr = (ts != null)
                                    ? ts.toLocalDateTime().toLocalDate().toString()
                                    : "-";
                            String durumStr = (durum != null && !durum.isEmpty()) ? durum : "-";

                            String html =
                                    "<html>Son Test Tarihi: " + tarihStr +
                                            "<br>Durum: " + durumStr + "</html>";
                            showPopup(html, binaID);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            showPopup("<html>Hata: " + ex.getMessage() + "</html>", -1);
        }
    }


    private void showPopup(String messageHtml, int binaID) {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        int dialogWidth  = 420;
        int dialogHeight = 220;
        dialog.setSize(dialogWidth, dialogHeight);

        // Sağda açılsın
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) (screen.width * 0.59);
        int y = screen.height / 3;
        dialog.setLocation(x, y);

        JPanel root = new JPanel(null);
        root.setOpaque(false);

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 35;

                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, arc, arc);


                g2.setColor(new Color(60, 60, 60, 235));
                g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12, arc, arc);
            }
        };
        card.setOpaque(false);
        card.setBounds(0, 0, dialogWidth, dialogHeight);

        JLabel lbl = new JLabel(messageHtml, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        lbl.setBounds(20, 35, dialogWidth - 40, 60);
        card.add(lbl);

        JButton btnTalep = new JButton("Test Talep Et") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 25;

                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(0, 4, getWidth(), getHeight() - 4, arc, arc);

                g2.setColor(new Color(165,165,165));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - 4, arc, arc);

                super.paintComponent(g);
            }
        };
        btnTalep.setBounds((dialogWidth - 220) / 2, 120, 220, 50);
        btnTalep.setForeground(Color.WHITE);
        btnTalep.setFont(new Font("Segoe UI Black", Font.PLAIN, 18));
        btnTalep.setContentAreaFilled(false);
        btnTalep.setBorderPainted(false);
        btnTalep.setFocusPainted(false);
        btnTalep.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnTalep.addActionListener(e -> {
            if (binaID > 0) {
                new Sirala(kullaniciID, binaID).setVisible(true);
                dialog.dispose();
                this.dispose();
            } else {
                dialog.dispose();
            }
        });

        card.add(btnTalep);
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

            g2.setColor(new Color(0, 0, 18, 100));
            g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, radius, radius);

            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth() - 16, getHeight() - 16, radius, radius);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Testtalep(1).setVisible(true));
    }
}
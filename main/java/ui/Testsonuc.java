package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Testsonuc extends JFrame {


    private static final String DB_URL  = "jdbc:sqlserver://localhost:1433;databaseName=DGBBS;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "250525";


    private static final int RISK_ESIK = 55;


    private final int kullaniciID;


    private BackgroundPanel bgPanel;
    private JPanel listPanel;
    private JScrollPane scrollPane;

    private final List<SonucItem> sonuclar = new ArrayList<>();


    public Testsonuc(int kullaniciID) {
        this.kullaniciID = kullaniciID;

        initUI();
        loadResultsFromDB();
        rebuildList();

        setVisible(true);
    }


    private void initUI() {
        Image bgImage = new ImageIcon(getClass().getResource("/img/walp.jpg")).getImage();
        bgPanel = new BackgroundPanel(bgImage);
        setContentPane(bgPanel);

        setTitle("StructCheck - Test Sonuçları");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bgPanel.setLayout(null);


        JLabel lblBaslik = new JLabel("Test Sonuçlarım", SwingConstants.LEFT);
        lblBaslik.setBounds(80, 80, 700, 40);
        lblBaslik.setFont(new Font("Segoe UI Black", Font.BOLD, 28));
        lblBaslik.setForeground(Color.WHITE);
        bgPanel.add(lblBaslik);


        JButton btnGeri = createRoundedButton("<- Anasayfa");
        btnGeri.setBounds(80, 20, 180, 45);
        btnGeri.addActionListener(e -> {
            Kanasayfa a = new Kanasayfa(kullaniciID);
            a.setVisible(true);
            dispose();
        });
        bgPanel.add(btnGeri);


        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);


        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBounds(80, 150, 1050, 650);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new RoundedScrollBarUI());

        bgPanel.add(scrollPane);
    }


    private void loadResultsFromDB() {
        sonuclar.clear();

        //  DOĞRU EŞLEŞME: tt.TalepEdenKullaniciID = kullaniciID
        String sql =
                "SELECT " +
                        "   ts.SonucID, ts.TestTarihi, ts.Sonuc, ts.Aciklama, ts.RiskPuani, ts.DonusumPuani, " +
                        "   tt.TalepID, tt.BinaID, " +
                        "   b.Il, b.Ilce, b.Mahalle, b.Sokak, b.BinaNumarasi, ISNULL(b.BinaAdi,'') AS BinaAdi, " +
                        "   kd.SonucID AS KD_SonucID " +
                        "FROM TestTalebi tt " +
                        "JOIN TestSonucu ts ON ts.TalepID = tt.TalepID " +
                        "JOIN Bina b ON b.BinaID = tt.BinaID " +
                        "LEFT JOIN KentselDonusum kd ON kd.SonucID = ts.SonucID " +
                        "WHERE tt.TalepEdenKullaniciID = ? " +
                        "ORDER BY ts.TestTarihi DESC";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, kullaniciID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SonucItem s = new SonucItem();

                    s.sonucID      = rs.getInt("SonucID");
                    s.talepID      = rs.getInt("TalepID");
                    s.binaID       = rs.getInt("BinaID");

                    s.testTarihi   = rs.getTimestamp("TestTarihi");
                    s.sonuc        = safeStr(rs.getString("Sonuc"));
                    s.aciklama     = safeStr(rs.getString("Aciklama"));

                    s.riskPuani    = rs.getInt("RiskPuani");
                    if (rs.wasNull()) s.riskPuani = -1;

                    s.donusumPuani = rs.getInt("DonusumPuani");
                    if (rs.wasNull()) s.donusumPuani = -1;

                    String il      = safeStr(rs.getString("Il"));
                    String ilce    = safeStr(rs.getString("Ilce"));
                    String mah     = safeStr(rs.getString("Mahalle"));
                    String sok     = safeStr(rs.getString("Sokak"));
                    String no      = safeStr(rs.getString("BinaNumarasi"));
                    String binaAdi = safeStr(rs.getString("BinaAdi"));

                    s.adres = il + " / " + ilce + " • " + mah + " • " + sok + " No:" + no +
                            (binaAdi.isEmpty() ? "" : " (" + binaAdi + ")");

                    sonuclar.add(s);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Test sonuçları yüklenirken hata:\n" + ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void rebuildList() {
        listPanel.removeAll();

        if (sonuclar.isEmpty()) {
            JLabel lblBos = new JLabel("Henüz görüntülenecek test sonucu yok.");
            lblBos.setForeground(Color.WHITE);
            lblBos.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblBos.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(lblBos);

            listPanel.revalidate();
            listPanel.repaint();
            return;
        }

        int cardW = 1020;
        int cardH = 180;

        for (SonucItem s : sonuclar) {
            JPanel card = createSonucCard(s, cardW, cardH);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(15));
        }

        listPanel.revalidate();
        listPanel.repaint();
    }


    private JPanel createSonucCard(SonucItem s, int width, int height) {

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 30;
                int shadow = 8;


                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(shadow, shadow,
                        getWidth() - shadow * 2, getHeight() - shadow * 2, arc, arc);

                g2.setColor(new Color(60, 60, 60, 235));
                g2.fillRoundRect(0, 0,
                        getWidth() - shadow * 2, getHeight() - shadow * 2, arc, arc);
            }
        };

        card.setOpaque(false);
        card.setPreferredSize(new Dimension(width, height));
        card.setMaximumSize(new Dimension(width, height));

        Font titleFont = new Font("Segoe UI Black", Font.BOLD, 18);
        Font infoFont  = new Font("Segoe UI", Font.PLAIN, 15);


        JLabel lblAdres = new JLabel("Bina: " + s.adres);
        lblAdres.setFont(titleFont);
        lblAdres.setForeground(Color.WHITE);
        lblAdres.setBounds(30, 20, width - 60, 25);
        card.add(lblAdres);


        String tarihStr = "-";
        if (s.testTarihi != null) {
            tarihStr = s.testTarihi.toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        String riskStr = (s.riskPuani >= 0) ? String.valueOf(s.riskPuani) : "-";
        String donStr  = (s.donusumPuani >= 0) ? String.valueOf(s.donusumPuani) : "-";

        JLabel lblAlt = new JLabel(
                "Tarih: " + tarihStr +
                        "   |   Sonuç: " + s.sonuc +
                        "   |   Risk Puanı: " + riskStr);
        lblAlt.setFont(infoFont);
        lblAlt.setForeground(Color.WHITE);
        lblAlt.setBounds(30, 55, width - 60, 25);
        card.add(lblAlt);


        String aciklama = s.aciklama.isEmpty() ? "-" : s.aciklama;
        JLabel lblAciklama = new JLabel("Açıklama: " + aciklama);
        lblAciklama.setFont(infoFont);
        lblAciklama.setForeground(Color.WHITE);
        lblAciklama.setBounds(30, 85, width - 60, 25);
        card.add(lblAciklama);


        if (s.riskPuani > RISK_ESIK) {
            JButton btnDonusum = createRoundedButton("Kentsel Dönüşüm Uygunluğu");
            btnDonusum.setBounds(width - 420, 110, 360, 45);
            btnDonusum.addActionListener(e -> showDonusumPopup(s));
            card.add(btnDonusum);
        }

        return card;
    }


    private void showDonusumPopup(SonucItem s) {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        int dialogW = 430;
        int dialogH = 260;
        dialog.setSize(dialogW, dialogH);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) (screen.width * 0.62);
        int y = screen.height / 2;
        dialog.setLocation(x, y);

        JPanel root = new JPanel(null);
        root.setOpaque(false);

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 35;
                int shadow = 6;

                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(shadow, shadow, getWidth() - shadow, getHeight() - shadow, arc, arc);

                g2.setColor(new Color(60, 60, 60, 235));
                g2.fillRoundRect(0, 0, getWidth() - shadow, getHeight() - shadow, arc, arc);
            }
        };
        card.setBounds(0, 0, dialogW, dialogH);
        card.setOpaque(false);


        JButton btnX = new JButton("×");
        btnX.setBounds(dialogW - 60, 18, 40, 40);
        btnX.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        btnX.setForeground(Color.WHITE);
        btnX.setContentAreaFilled(false);
        btnX.setBorderPainted(false);
        btnX.setFocusPainted(false);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> dialog.dispose());
        card.add(btnX);

        JLabel lblTitle = new JLabel("Kentsel Dönüşüm Bilgisi", SwingConstants.CENTER);
        lblTitle.setBounds(20, 25, dialogW - 40, 30);
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        card.add(lblTitle);

        int dp = (s.donusumPuani >= 0) ? s.donusumPuani : 0;



        boolean uygun = dp >= 50;
        String uygunStr = uygun ? "Uygun" : "Uygun Değil";

        JLabel lblPuan = new JLabel("Dönüşüm Puanı: " + dp, SwingConstants.CENTER);
        lblPuan.setBounds(20, 90, dialogW - 40, 30);
        lblPuan.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        lblPuan.setForeground(Color.WHITE);
        card.add(lblPuan);

        JLabel lblDurum = new JLabel("Uygunluk: " + uygunStr, SwingConstants.CENTER);
        lblDurum.setBounds(20, 130, dialogW - 40, 30);
        lblDurum.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        lblDurum.setForeground(Color.WHITE);
        card.add(lblDurum);

        JLabel lblInfo = new JLabel();
        lblInfo.setBounds(20, 170, dialogW - 40, 60);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblInfo.setForeground(Color.LIGHT_GRAY);
        card.add(lblInfo);

        root.add(card);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }


    private JButton createRoundedButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 30;
                int shadow = 6;

                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillRoundRect(0, shadow, getWidth(), getHeight() - shadow, arc, arc);

                g2.setColor(new Color(165, 165, 165));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - shadow, arc, arc);

                super.paintComponent(g);
            }
        };

        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Black", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }


    private static class RoundedScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {

        }
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (!scrollbar.isEnabled() || thumbBounds.width > thumbBounds.height) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 15;
            g2.setColor(new Color(0, 0, 0, 130));
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y,
                    thumbBounds.width, thumbBounds.height, arc, arc);
        }
        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(12, 12);
        }
    }


    class BackgroundPanel extends JPanel {
        private final Image image;
        public BackgroundPanel(Image image) { this.image = image; }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }


    private static class SonucItem {
        int sonucID;
        int talepID;
        int binaID;

        Timestamp testTarihi;
        String sonuc;
        String aciklama;

        int riskPuani;
        int donusumPuani;

        String adres;
    }

    private static String safeStr(String s) {
        return s == null ? "" : s.trim();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Testsonuc(1));
    }
}
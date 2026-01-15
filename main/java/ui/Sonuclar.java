package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Sonuclar extends JFrame {

    private static final String DB_URL  = "jdbc:sqlserver://localhost:1433;databaseName=DGBBS;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "250525";


    private final int kullaniciID;

    private int atanabilirID;


    private BackgroundPanel bgPanel;
    private JPanel listPanel;
    private JScrollPane scrollPane;
    private JButton btnGeri;


    private final List<TalepItem> talepler = new ArrayList<>();


    public Sonuclar(int kullaniciID) {
        this.kullaniciID = kullaniciID;


        Image bgImage = new ImageIcon(getClass().getResource("/img/walp.jpg")).getImage();
        bgPanel = new BackgroundPanel(bgImage);
        setContentPane(bgPanel);

        setTitle("StructCheck - Test Sonuçları");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bgPanel.setLayout(null);

        Font titleFont = new Font("Segoe UI Black", Font.BOLD, 28);


        JLabel lblBaslik = new JLabel("Test Sonucu Girilecek Talepler", SwingConstants.LEFT);
        lblBaslik.setBounds(80, 80, 800, 40);
        lblBaslik.setFont(titleFont);
        lblBaslik.setForeground(Color.WHITE);
        bgPanel.add(lblBaslik);


        btnGeri = createRoundedButton("<- Anasayfa");
        btnGeri.setBounds(80, 20, 180, 45);
        btnGeri.addActionListener(e -> {
            // Kurum/Gönüllü anasayfa ne ise onu aç
            KFanasayfa ana = new KFanasayfa(kullaniciID);
            ana.setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnGeri);


        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBounds(80, 150, 1950, 600);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new RoundedScrollBarUI());

        bgPanel.add(scrollPane);


        atanabilirID = getAtanabilirID(kullaniciID);


        loadTaleplerFromDB();
        rebuildList();

        setVisible(true);
    }


    private int getAtanabilirID(int kullaniciID) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {


            String qKurum = "SELECT KurumID FROM Kurum WHERE KullaniciID = ?";
            try (PreparedStatement ps1 = con.prepareStatement(qKurum)) {
                ps1.setInt(1, kullaniciID);
                try (ResultSet rs1 = ps1.executeQuery()) {
                    if (rs1.next()) {
                        return rs1.getInt("KurumID");
                    }
                }
            }


            String qGonullu = "SELECT GonulluID FROM Gonullu WHERE KullaniciID = ?";
            try (PreparedStatement ps2 = con.prepareStatement(qGonullu)) {
                ps2.setInt(1, kullaniciID);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) {
                        return rs2.getInt("GonulluID");
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return -1;
    }


    private void loadTaleplerFromDB() {
        talepler.clear();

        if (atanabilirID == -1) {

            return;
        }

        String sql =
                "SELECT tt.TalepID, tt.BinaID, tt.TalepTarihi, " +
                        "       b.Il, b.Ilce, b.Mahalle, b.Sokak, b.BinaNumarasi, ISNULL(b.BinaAdi, '') AS BinaAdi, " +
                        "       k.Ad, k.Soyad " +
                        "FROM TestTalebi tt " +
                        "JOIN Bina b      ON b.BinaID = tt.BinaID " +
                        "JOIN Kullanici k ON k.KullaniciID = tt.TalepEdenKullaniciID " +
                        "WHERE tt.AtananID = ? AND tt.Durum = 'Kabul Edildi' " +
                        "ORDER BY tt.TalepTarihi DESC";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, atanabilirID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TalepItem t = new TalepItem();
                    t.talepID     = rs.getInt("TalepID");
                    t.binaID      = rs.getInt("BinaID");
                    t.talepTarihi = rs.getTimestamp("TalepTarihi");

                    String il      = rs.getString("Il");
                    String ilce    = rs.getString("Ilce");
                    String mahalle = rs.getString("Mahalle");
                    String sokak   = rs.getString("Sokak");
                    String binaNo  = rs.getString("BinaNumarasi");
                    String binaAdi = rs.getString("BinaAdi");

                    String adres = il + " / " + ilce + " - " + mahalle +
                            " " + sokak + " No:" + binaNo;
                    if (binaAdi != null && !binaAdi.isEmpty()) {
                        adres += " (" + binaAdi + ")";
                    }
                    t.binaAdres = adres;

                    String ad   = rs.getString("Ad");
                    String soyad= rs.getString("Soyad");
                    t.talepEden = ad + " " + soyad;

                    talepler.add(t);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Test sonuçları için talepler yüklenirken hata oluştu:\n" + ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void rebuildList() {
        listPanel.removeAll();

        if (talepler.isEmpty()) {
            JLabel lblBos = new JLabel("Şu anda sonuç girmeniz gereken talep bulunmuyor.");
            lblBos.setForeground(Color.WHITE);
            lblBos.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblBos.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(lblBos);
        } else {
            int cardWidth  = 1900;
            int cardHeight = 150;

            for (TalepItem t : talepler) {
                JPanel card = createTalepCard(t, cardWidth, cardHeight);
                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(15));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }


    private JPanel createTalepCard(TalepItem t, int width, int height) {

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

        JLabel lblAdres = new JLabel("Bina: " + t.binaAdres);
        lblAdres.setFont(titleFont);
        lblAdres.setForeground(Color.WHITE);
        lblAdres.setBounds(30, 20, width - 400, 25);
        card.add(lblAdres);

        String tarihStr = (t.talepTarihi != null)
                ? t.talepTarihi.toLocalDateTime().toString().replace('T',' ')
                : "-";

        JLabel lblAlt = new JLabel(
                "Talep Eden: " + t.talepEden +
                        "   |   Tarih: " + tarihStr +
                        "   |   Durum: Kabul Edildi");
        lblAlt.setFont(infoFont);
        lblAlt.setForeground(Color.WHITE);
        lblAlt.setBounds(30, 55, width - 400, 25);
        card.add(lblAlt);


        JButton btnSonucGir = createRoundedButton("Sonuç Gir");
        btnSonucGir.setBounds(680, 85, 200, 45);
        btnSonucGir.addActionListener(e -> {
            // Sadece form açıyoruz, burada DB'ye hiçbir şey yazmıyoruz
            TestsonucGir frm = new TestsonucGir(t.talepID, t.binaID,kullaniciID);
            frm.setVisible(true);
        });
        card.add(btnSonucGir);

        return card;
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
                g2.fillRoundRect(0, shadow,
                        getWidth(), getHeight() - shadow, arc, arc);

                g2.setColor(new Color(165, 165, 165));
                g2.fillRoundRect(0, 0,
                        getWidth(), getHeight() - shadow, arc, arc);

                super.paintComponent(g2);
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
            // görünmez track
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
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }


    private static class TalepItem {
        int talepID;
        int binaID;
        String binaAdres;
        String talepEden;
        java.sql.Timestamp talepTarihi;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Sonuclar(1));
    }
}
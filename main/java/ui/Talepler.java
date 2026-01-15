package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Talepler extends JFrame {

    private static final String DB_URL  = "jdbc:sqlserver://localhost:1433;databaseName=DGBBS;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "250525";

    private final int kullaniciID;
    private int atananID;

    private BackgroundPanel bgPanel;
    private JPanel listPanel;
    private JScrollPane scrollPane;
    private JButton btnGeri;
    private JButton btnIstatistik; // 🔹 EKLENDİ

    private final List<TalepItem> talepler = new ArrayList<>();

    public Talepler(int kullaniciID) {
        this.kullaniciID = kullaniciID;
        this.atananID = getAtanabilirID(kullaniciID);

        Image bgImage = new ImageIcon(getClass().getResource("/img/walp.jpg")).getImage();
        bgPanel = new BackgroundPanel(bgImage);
        setContentPane(bgPanel);

        setTitle("StructCheck - Gelen Test Talepleri");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bgPanel.setLayout(null);

        JLabel lblBaslik = new JLabel("Gelen Test Talepleri", SwingConstants.LEFT);
        lblBaslik.setBounds(80, 80, 600, 40);
        lblBaslik.setFont(new Font("Segoe UI Black", Font.BOLD, 28));
        lblBaslik.setForeground(Color.WHITE);
        bgPanel.add(lblBaslik);

        btnGeri = createRoundedButton("<- Anasayfa");
        btnGeri.setBounds(80, 20, 180, 45);
        btnGeri.addActionListener(e -> {
            KFanasayfa ana = new KFanasayfa(kullaniciID);
            ana.setVisible(true);
            dispose();
        });
        bgPanel.add(btnGeri);

        //  İSTATİSTİK
        btnIstatistik = createRoundedButton("İstatistik");
        btnIstatistik.setBounds(1200, 650, 180, 50);
        btnIstatistik.addActionListener(e -> showIstatistikPopup());
        bgPanel.add(btnIstatistik);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBounds(80, 150, 1950, 600);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new RoundedScrollBarUI());
        bgPanel.add(scrollPane);

        loadTalepler();
        rebuildList();

        setVisible(true);
    }

    private int getAtanabilirID(int kullaniciID) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            String q1 = "SELECT KurumID FROM Kurum WHERE KullaniciID = ?";
            PreparedStatement ps1 = con.prepareStatement(q1);
            ps1.setInt(1, kullaniciID);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) return rs1.getInt("KurumID");

            String q2 = "SELECT GonulluID FROM Gonullu WHERE KullaniciID = ?";
            PreparedStatement ps2 = con.prepareStatement(q2);
            ps2.setInt(1, kullaniciID);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) return rs2.getInt("GonulluID");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private void loadTalepler() {
        talepler.clear();

        String sql =
                "SELECT tt.TalepID, tt.TalepTarihi, tt.Durum, " +
                        "b.Il, b.Ilce, b.Mahalle, b.Sokak, b.BinaNumarasi, ISNULL(b.BinaAdi,'') AS BinaAdi, " +
                        "k.Ad, k.Soyad " +
                        "FROM TestTalebi tt " +
                        "JOIN Bina b ON b.BinaID = tt.BinaID " +
                        "JOIN Kullanici k ON k.KullaniciID = tt.TalepEdenKullaniciID " +
                        "WHERE tt.AtananID = ? " +
                        "ORDER BY tt.TalepTarihi DESC";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, atananID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TalepItem t = new TalepItem();
                    t.talepID = rs.getInt("TalepID");
                    t.durum = rs.getString("Durum");
                    t.talepTarihi = rs.getTimestamp("TalepTarihi");

                    String il = rs.getString("Il");
                    String ilce = rs.getString("Ilce");
                    String mah = rs.getString("Mahalle");
                    String sok = rs.getString("Sokak");
                    String no = rs.getString("BinaNumarasi");
                    String adi = rs.getString("BinaAdi");

                    t.binaAdres = il + " / " + ilce + " - " + mah + " " + sok + " No:" + no +
                            (adi.isEmpty() ? "" : " (" + adi + ")");

                    t.talepEden = rs.getString("Ad") + " " + rs.getString("Soyad");

                    talepler.add(t);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Talepler yüklenirken hata:\n" + ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rebuildList() {
        listPanel.removeAll();

        if (talepler.isEmpty()) {
            JLabel lbl = new JLabel("Şu anda size atanmış talep bulunmuyor.");
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(lbl);
        } else {
            for (TalepItem t : talepler) {
                JPanel card = createTalepCard(t);
                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(15));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createTalepCard(TalepItem t) {

        int width = 2000;
        int height = 180;

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

        JLabel lblAdres = new JLabel("Bina: " + t.binaAdres);
        lblAdres.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        lblAdres.setForeground(Color.WHITE);
        lblAdres.setBounds(30, 20, width - 450, 25);
        card.add(lblAdres);

        String tarihStr = t.talepTarihi.toLocalDateTime().toString().replace('T',' ');

        JLabel lblAlt = new JLabel(
                "Talep Eden: " + t.talepEden +
                        "   |   Tarih: " + tarihStr +
                        "   |   Durum: " + t.durum);
        lblAlt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblAlt.setForeground(Color.WHITE);
        lblAlt.setBounds(30, 55, width - 450, 25);
        card.add(lblAlt);

        JButton btnKabul = createRoundedButton("Kabul Et");
        btnKabul.setBounds(35, 95, 150, 45);
        btnKabul.addActionListener(e -> guncelleDurum(t, "Kabul Edildi"));
        card.add(btnKabul);

        JButton btnRed = createRoundedButton("Reddet");
        btnRed.setBounds(200, 95, 150, 45);
        btnRed.addActionListener(e -> reddetVeLogla(t));
        card.add(btnRed);

        return card;
    }

    private void reddetVeLogla(TalepItem t) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM TestTalebi WHERE TalepID = ?"
            );
            ps.setInt(1, t.talepID);
            ps.executeUpdate();

            PreparedStatement logPs = con.prepareStatement(
                    "INSERT INTO LogKaydi (KullaniciID, IslemTuru, TabloAdi, KayitID, Aciklama) " +
                            "VALUES (?, 'DELETE', 'TestTalebi', ?, 'Test talebi reddedildi')"
            );
            logPs.setInt(1, kullaniciID);
            logPs.setInt(2, t.talepID);
            logPs.executeUpdate();

            JOptionPane.showMessageDialog(this, "Talep reddedildi.");

            loadTalepler();
            rebuildList();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Talep reddedilirken hata:\n" + ex.getMessage());
        }
    }

    private void guncelleDurum(TalepItem t, String yeniDurum) {
        String sql = "UPDATE TestTalebi SET Durum = ? WHERE TalepID = ?";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, yeniDurum);
            ps.setInt(2, t.talepID);
            ps.executeUpdate();

            PreparedStatement logPs = con.prepareStatement(
                    "INSERT INTO LogKaydi (KullaniciID, IslemTuru, TabloAdi, KayitID, Aciklama) " +
                            "VALUES (?, 'UPDATE', 'TestTalebi', ?, 'Test talebi kabul edildi')"
            );
            logPs.setInt(1, kullaniciID);
            logPs.setInt(2, t.talepID);
            logPs.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Talep \"" + yeniDurum + "\" olarak güncellendi.");

            loadTalepler();
            rebuildList();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Durum güncellenirken hata:\n" + ex.getMessage());
        }
    }


    private void showIstatistikPopup() {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        dialog.setUndecorated(true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);


        JPanel panel = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0,0,0,120));
                g2.fillRoundRect(8,8,getWidth()-16,getHeight()-16,30,30);

                g2.setColor(new Color(60,60,60,235));
                g2.fillRoundRect(0,0,getWidth()-16,getHeight()-16,30,30);
            }
        };
        panel.setOpaque(false);

        JTextArea txt = new JTextArea(loadIstatistikText());
        txt.setEditable(false);
        txt.setOpaque(false);
        txt.setForeground(Color.WHITE);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txt.setBounds(30, 60, 440, 330);

        JButton btnKapat = createRoundedButton("Kapat");
        btnKapat.setBounds(160, 390, 180, 45);
        btnKapat.addActionListener(e -> dialog.dispose());

        JLabel lbl = new JLabel("Talep İstatistikleri", SwingConstants.CENTER);
        lbl.setBounds(0,20,500,30);
        lbl.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        lbl.setForeground(Color.WHITE);

        panel.add(lbl);
        panel.add(txt);
        panel.add(btnKapat);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private String loadIstatistikText() {
        StringBuilder sb = new StringBuilder();

        String sql =
                "SELECT Durum, COUNT(*) AS TalepSayisi, " +
                        "AVG(DATEDIFF(day, TalepTarihi, GETDATE())) AS OrtalamaGun " +
                        "FROM TestTalebi " +
                        "GROUP BY Durum " +
                        "HAVING COUNT(*) > 0";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sb.append("Durum: ").append(rs.getString("Durum")).append("\n");
                sb.append("Talep Sayısı: ").append(rs.getInt("TalepSayisi")).append("\n");
                sb.append("Ortalama Gün: ").append(rs.getDouble("OrtalamaGun")).append("\n\n");
            }
        } catch (Exception e) {
            sb.append("İstatistik verisi alınamadı.");
        }
        return sb.toString();
    }

    private JButton createRoundedButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 30;
                int shadow = 6;

                g2.setColor(new Color(0,0,0,90));
                g2.fillRoundRect(0, shadow,
                        getWidth(), getHeight() - shadow, arc, arc);

                g2.setColor(new Color(165,165,165));
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
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(0,0,0,130));
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 15, 15);
        }
        @Override public Dimension getPreferredSize(JComponent c) { return new Dimension(12, 12); }
    }

    private class BackgroundPanel extends JPanel {
        private final Image img;
        public BackgroundPanel(Image img) { this.img = img; }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        }
    }

    private static class TalepItem {
        int talepID;
        String binaAdres;
        String talepEden;
        Timestamp talepTarihi;
        String durum;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Talepler(1));
    }
}

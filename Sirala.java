package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Sirala extends JFrame {


    private static final String DB_URL  = "jdbc:sqlserver://localhost:1433;databaseName=DGBBS;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "250525";


    private final int kullaniciID;
    private final int binaID;


    private BackgroundPanel bgPanel;
    private JPanel listPanel;
    private JScrollPane scrollPane;
    private JButton btnFiltre;
    private JButton btnGeri;


    private boolean filtreKurum = true;
    private boolean filtreGonullu = true;
    private String filtreSehir = null;
    private String fiyatSirasi = null;


    private final List<Item> allItems = new ArrayList<>();


    private static final String[] ILLER = {
            "Tümü",
            "Adana","Adıyaman","Afyonkarahisar","Ağrı","Aksaray","Amasya","Ankara","Antalya","Ardahan","Artvin",
            "Aydın","Balıkesir","Bartın","Batman","Bayburt","Bilecik","Bingöl","Bitlis","Bolu","Burdur","Bursa",
            "Çanakkale","Çankırı","Çorum","Denizli","Diyarbakır","Düzce","Edirne","Elazığ","Erzincan","Erzurum",
            "Eskişehir","Gaziantep","Giresun","Gümüşhane","Hakkari","Hatay","Iğdır","Isparta","İstanbul","İzmir",
            "Kahramanmaraş","Karabük","Karaman","Kars","Kastamonu","Kayseri","Kırıkkale","Kırklareli","Kırşehir",
            "Kilis","Kocaeli","Konya","Kütahya","Malatya","Manisa","Mardin","Mersin","Muğla","Muş","Nevşehir",
            "Niğde","Ordu","Osmaniye","Rize","Sakarya","Samsun","Siirt","Sinop","Sivas","Şanlıurfa","Şırnak",
            "Tekirdağ","Tokat","Trabzon","Tunceli","Uşak","Van","Yalova","Yozgat","Zonguldak"
    };


    public Sirala(int kullaniciID, int binaID) {
        this.kullaniciID = kullaniciID;
        this.binaID = binaID;

        // Arka plan
        Image bgImage = new ImageIcon(getClass().getResource("/img/walp.jpg")).getImage();
        bgPanel = new BackgroundPanel(bgImage);
        setContentPane(bgPanel);

        setTitle("StructCheck - Test Talebi Sıralama");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bgPanel.setLayout(null);


        Font titleFont = new Font("Segoe UI Black", Font.BOLD, 28);
        Font buttonFont = new Font("Segoe UI Black", Font.PLAIN, 18);


        JLabel lblBaslik = new JLabel("Test Talebi İçin Kurum / Gönüllü Seçimi", SwingConstants.LEFT);
        lblBaslik.setBounds(80, 80, 800, 40);
        lblBaslik.setFont(titleFont);
        lblBaslik.setForeground(Color.WHITE);
        bgPanel.add(lblBaslik);


        btnGeri = createRoundedButton("<- Anasayfa");
        btnGeri.setBounds(80, 20, 180, 45);
        btnGeri.addActionListener(e -> {
            Kanasayfa ana = new Kanasayfa(kullaniciID);
            ana.setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnGeri);


        btnFiltre = createRoundedButton("Filtrele");
        btnFiltre.setBounds(1200, 130, 180, 45);
        btnFiltre.addActionListener(e -> showFiltrePopup());
        bgPanel.add(btnFiltre);


        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBounds(80, 170, 1950, 600);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);


        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new RoundedScrollBarUI());

        bgPanel.add(scrollPane);


        loadItemsFromDB();
        applyFiltersAndRebuild();

        setVisible(true);
    }


    private void loadItemsFromDB() {
        allItems.clear();

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {


            String sqlKurum =
                    "SELECT k.KurumID, k.KurumAdi, k.Il, k.Ilce, k.MetrekareFiyati, " +
                            "       ku.Ad, ku.Soyad, ku.Sehir " +
                            "FROM Kurum k " +
                            "JOIN Kullanici ku ON ku.KullaniciID = k.KullaniciID";
            try (PreparedStatement ps = con.prepareStatement(sqlKurum);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Item item = new Item();
                    item.tip = "KURUM";
                    item.id = rs.getInt("KurumID");
                    item.adSoyad = rs.getString("KurumAdi");

                    String il = rs.getString("Il");
                    String kullaniciSehir = rs.getString("Sehir");
                    item.sehir = (il != null && !il.isEmpty()) ? il : kullaniciSehir;

                    item.ilce = rs.getString("Ilce");
                    item.fiyat = rs.getDouble("MetrekareFiyati");
                    if (rs.wasNull()) item.fiyat = null;

                    item.universite = null;
                    item.uzmanlik = null;
                    item.sinif = null;

                    allItems.add(item);
                }
            }


            String sqlGonullu =
                    "SELECT g.GonulluID, g.Universite, g.Uzmanlik, g.Sinif, " +
                            "       ku.Ad, ku.Soyad, ku.Sehir " +
                            "FROM Gonullu g " +
                            "JOIN Kullanici ku ON ku.KullaniciID = g.KullaniciID";
            try (PreparedStatement ps = con.prepareStatement(sqlGonullu);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Item item = new Item();
                    item.tip = "GONULLU";
                    item.id = rs.getInt("GonulluID");
                    String ad = rs.getString("Ad");
                    String soyad = rs.getString("Soyad");
                    item.adSoyad = (ad == null ? "" : ad) + " " + (soyad == null ? "" : soyad);

                    item.sehir = rs.getString("Sehir");
                    item.ilce = null;

                    item.universite = rs.getString("Universite");
                    item.uzmanlik = rs.getString("Uzmanlik");
                    item.sinif = rs.getString("Sinif");

                    item.fiyat = null; // Gönüllü için fiyat yok

                    allItems.add(item);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Veriler yüklenirken hata oluştu:\n" + ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void applyFiltersAndRebuild() {
        listPanel.removeAll();

        List<Item> kurumList = new ArrayList<>();
        List<Item> gonulluList = new ArrayList<>();


        for (Item item : allItems) {


            if (item.tip.equals("KURUM") && !filtreKurum) continue;
            if (item.tip.equals("GONULLU") && !filtreGonullu) continue;


            if (filtreSehir != null && !"Tümü".equalsIgnoreCase(filtreSehir)) {
                String itemSehir = item.sehir == null ? "" : item.sehir;
                if (!itemSehir.equalsIgnoreCase(filtreSehir)) {
                    continue;
                }
            }

            if (item.tip.equals("KURUM")) kurumList.add(item);
            else gonulluList.add(item);
        }


        if (fiyatSirasi != null) {
            Comparator<Item> cmp = Comparator.comparingDouble(i -> i.fiyat != null ? i.fiyat : 0.0);
            if ("DESC".equals(fiyatSirasi)) {
                cmp = cmp.reversed();
            }
            kurumList.sort(cmp);
        }


        List<Item> finalList = new ArrayList<>();
        finalList.addAll(kurumList);
        finalList.addAll(gonulluList);


        int cardWidth = 1900;
        int cardHeight = 150;

        for (Item item : finalList) {
            JPanel card = createItemCard(item, cardWidth, cardHeight);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(15));
        }

        listPanel.revalidate();
        listPanel.repaint();
    }


    private JPanel createItemCard(Item item, int width, int height) {

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 30;
                int shadow = 8;

                // Gölge
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(shadow, shadow,
                        getWidth() - shadow * 2, getHeight() - shadow * 2, arc, arc);

                // Kart
                g2.setColor(new Color(60, 60, 60, 235));
                g2.fillRoundRect(0, 0,
                        getWidth() - shadow * 2, getHeight() - shadow * 2, arc, arc);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(width, height));
        card.setMaximumSize(new Dimension(width, height));

        Font titleFont = new Font("Segoe UI Black", Font.BOLD, 18);
        Font infoFont = new Font("Segoe UI", Font.PLAIN, 15);


        JLabel lblAd = new JLabel(item.adSoyad);
        lblAd.setFont(titleFont);
        lblAd.setForeground(Color.WHITE);
        lblAd.setBounds(30, 20, 600, 25);
        card.add(lblAd);


        StringBuilder sb = new StringBuilder();


        sb.append("Tür: ").append(item.tip.equals("KURUM") ? "Kurum" : "Gönüllü");


        if (item.sehir != null && !item.sehir.isEmpty()) {
            sb.append("   |   Şehir: ").append(item.sehir);
        }
        if (item.ilce != null && !item.ilce.isEmpty()) {
            sb.append("   |   İlçe: ").append(item.ilce);
        }


        if (item.tip.equals("KURUM") && item.fiyat != null) {
            sb.append("   |   m² Fiyatı: ").append(item.fiyat).append(" TL");
        }


        if (item.tip.equals("GONULLU")) {
            if (item.universite != null && !item.universite.isEmpty()) {
                sb.append("   |   Üniversite: ").append(item.universite);
            }
            if (item.uzmanlik != null && !item.uzmanlik.isEmpty()) {
                sb.append("   |   Uzmanlık: ").append(item.uzmanlik);
            }
            if (item.sinif != null && !item.sinif.isEmpty()) {
                sb.append("   |   Sınıf: ").append(item.sinif);
            }
        }

        JLabel lblInfo = new JLabel(sb.toString());
        lblInfo.setFont(infoFont);
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setBounds(30, 55, width - 200, 25);
        card.add(lblInfo);


        JButton btnTalep = createRoundedButton("Test Talep Et");
        btnTalep.setBounds(715, 85, 200, 45);
        btnTalep.addActionListener(e -> handleTestTalep(item));
        card.add(btnTalep);

        return card;
    }


    private void handleTestTalep(Item item) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            String sql =
                    "INSERT INTO TestTalebi (BinaID, TalepEdenKullaniciID, TalepTarihi, Durum, AtananTip, AtananID) " +
                            "VALUES (?, ?, GETDATE(), ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, binaID);
                ps.setInt(2, kullaniciID);
                ps.setString(3, "Beklemede");
                ps.setString(4, item.tip);
                ps.setInt(5, item.id);

                ps.executeUpdate();

                PreparedStatement logPs = con.prepareStatement(
                        "INSERT INTO LogKaydi (KullaniciID, IslemTuru, TabloAdi, KayitID, Aciklama) " +
                                "VALUES (?, 'INSERT', 'TestTalebi', ?, 'Test talebi oluşturuldu')"
                );
                logPs.setInt(1, kullaniciID);
                logPs.setInt(2, binaID);
                logPs.executeUpdate();

            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Test talebi kaydedilirken hata oluştu:\n" + ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        showTalepPopup();
    }


    private void showTalepPopup() {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        int dialogWidth = 460;
        int dialogHeight = 230;
        dialog.setSize(dialogWidth, dialogHeight);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) (screen.width * 0.60);
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
                int shadow = 6;

                g2.setColor(new Color(0,0,0,120));
                g2.fillRoundRect(shadow, shadow,
                        getWidth() - shadow, getHeight() - shadow, arc, arc);

                g2.setColor(new Color(60,60,60,235));
                g2.fillRoundRect(0, 0,
                        getWidth() - shadow, getHeight() - shadow, arc, arc);
            }
        };
        card.setOpaque(false);
        card.setBounds(0, 0, dialogWidth, dialogHeight);


        JButton btnClose = createCircleButton("X");
        btnClose.setBounds(dialogWidth - 55, 15, 35, 35);
        btnClose.addActionListener(e -> dialog.dispose());
        card.add(btnClose);

        JLabel lbl = new JLabel(
                "<html><div style='text-align:center;'>Test talebiniz alındı.<br>Sonuçlarım kısmını takip edebilirsiniz.</div></html>",
                SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        lbl.setBounds(20, 60, dialogWidth - 40, 80);
        card.add(lbl);

        root.add(card);
        dialog.setContentPane(root);


        Timer t = new Timer(3000, e -> dialog.dispose());
        t.setRepeats(false);
        t.start();

        dialog.setVisible(true);
    }


    private void showFiltrePopup() {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        int dialogWidth = 500;
        int dialogHeight = 380;
        dialog.setSize(dialogWidth, dialogHeight);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) (screen.width * 0.62);
        int y = screen.height / 4;
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
                g2.fillRoundRect(shadow, shadow,
                        getWidth() - shadow, getHeight() - shadow, arc, arc);

                g2.setColor(new Color(60, 60, 60, 235));
                g2.fillRoundRect(0, 0,
                        getWidth() - shadow, getHeight() - shadow, arc, arc);
            }
        };
        card.setOpaque(false);
        card.setBounds(0, 0, dialogWidth, dialogHeight);

        Font titleFont = new Font("Segoe UI Black", Font.BOLD, 20);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);

        JLabel lblTitle = new JLabel("Filtrele", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(titleFont);
        lblTitle.setBounds(0, 20, dialogWidth, 30);
        card.add(lblTitle);


        JButton btnClose = createCircleButton("X");
        btnClose.setBounds(dialogWidth - 55, 15, 35, 35);
        btnClose.addActionListener(e -> dialog.dispose());
        card.add(btnClose);


        JLabel lblTur = new JLabel("Tür:");
        lblTur.setForeground(Color.WHITE);
        lblTur.setFont(labelFont);
        lblTur.setBounds(40, 70, 100, 25);
        card.add(lblTur);

        JCheckBox cbKurum = new JCheckBox("Kurum");
        cbKurum.setOpaque(false);
        cbKurum.setForeground(Color.WHITE);
        cbKurum.setFont(labelFont);
        cbKurum.setBounds(150, 70, 100, 25);
        cbKurum.setSelected(filtreKurum);
        card.add(cbKurum);

        JCheckBox cbGonullu = new JCheckBox("Gönüllü");
        cbGonullu.setOpaque(false);
        cbGonullu.setForeground(Color.WHITE);
        cbGonullu.setFont(labelFont);
        cbGonullu.setBounds(270, 70, 120, 25);
        cbGonullu.setSelected(filtreGonullu);
        card.add(cbGonullu);


        JLabel lblSehir = new JLabel("Şehir:");
        lblSehir.setForeground(Color.WHITE);
        lblSehir.setFont(labelFont);
        lblSehir.setBounds(40, 120, 100, 25);
        card.add(lblSehir);

        JComboBox<String> cbSehir = new JComboBox<>(ILLER);
        cbSehir.setBounds(150, 120, 250, 35);
        cbSehir.setFont(labelFont);
        cbSehir.setFocusable(false);
        card.add(cbSehir);


        if (filtreSehir == null || filtreSehir.isEmpty()) {
            cbSehir.setSelectedIndex(0);
        } else {
            for (int i = 0; i < ILLER.length; i++) {
                if (ILLER[i].equalsIgnoreCase(filtreSehir)) {
                    cbSehir.setSelectedIndex(i);
                    break;
                }
            }
        }


        JLabel lblFiyat = new JLabel("Fiyat Sırası (Kurum):");
        lblFiyat.setForeground(Color.WHITE);
        lblFiyat.setFont(labelFont);
        lblFiyat.setBounds(40, 170, 200, 25);
        card.add(lblFiyat);

        JRadioButton rbArtan  = new JRadioButton("Artan");
        JRadioButton rbAzalan = new JRadioButton("Azalan");
        rbArtan.setOpaque(false);
        rbAzalan.setOpaque(false);
        rbArtan.setForeground(Color.WHITE);
        rbAzalan.setForeground(Color.WHITE);
        rbArtan.setFont(labelFont);
        rbAzalan.setFont(labelFont);
        rbArtan.setBounds(250, 170, 80, 25);
        rbAzalan.setBounds(340, 170, 90, 25);

        ButtonGroup grpFiyat = new ButtonGroup();
        grpFiyat.add(rbArtan);
        grpFiyat.add(rbAzalan);

        if ("ASC".equals(fiyatSirasi)) rbArtan.setSelected(true);
        else if ("DESC".equals(fiyatSirasi)) rbAzalan.setSelected(true);

        card.add(rbArtan);
        card.add(rbAzalan);


        JButton btnApply = createRoundedButton("Filtrele");
        btnApply.setBounds((dialogWidth - 220) / 2, 240, 220, 50);
        btnApply.addActionListener(e -> {
            // Tür
            filtreKurum = cbKurum.isSelected();
            filtreGonullu = cbGonullu.isSelected();
            if (!filtreKurum && !filtreGonullu) {
                // Hiçbiri seçilmezse ikisini de aç
                filtreKurum = true;
                filtreGonullu = true;
            }


            String secilenSehir = (String) cbSehir.getSelectedItem();
            if (secilenSehir == null || "Tümü".equalsIgnoreCase(secilenSehir)) {
                filtreSehir = null;
            } else {
                filtreSehir = secilenSehir;
            }


            if (rbArtan.isSelected()) {
                fiyatSirasi = "ASC";
            } else if (rbAzalan.isSelected()) {
                fiyatSirasi = "DESC";
            } else {
                fiyatSirasi = null;
            }

            applyFiltersAndRebuild();
            dialog.dispose();
        });
        card.add(btnApply);

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


    private JButton createCircleButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int d = Math.min(getWidth(), getHeight());
                g2.setColor(new Color(0,0,0,150));
                g2.fillOval(0, 0, d, d);

                g2.setColor(new Color(200,200,200));
                g2.fillOval(2, 2, d-4, d-4);

                super.paintComponent(g2);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(null);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Black", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        return btn;
    }


    private static class RoundedScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // Görünmez track
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (!scrollbar.isEnabled() || thumbBounds.width > thumbBounds.height) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 15;
            g2.setColor(new Color(0, 0, 0, 130));
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y,
                    thumbBounds.width, thumbBounds.height, arc , arc);
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


    private static class Item {
        int id;              // KurumID veya GonulluID
        String tip;          // "KURUM" veya "GONULLU"
        String adSoyad;
        String sehir;
        String ilce;
        Double fiyat;        // sadece kurum
        String universite;   // gönüllü
        String uzmanlik;     // gönüllü
        String sinif;        // gönüllü
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Sirala(1, 1));
    }
}
package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Kkayit extends JFrame {

    private JTextField txtAd, txtSoyad, txtTC, txtTelefon, txtEposta, txtSehir;
    private JPasswordField txtSifre;
    private JRadioButton rbBireysel, rbKurum, rbGonullu;

    private static final String DB_URL  = "jdbc:sqlserver://localhost:1433;databaseName=DGBBS;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "250525";

    public Kkayit() {

        Image backgroundImage = new ImageIcon(getClass().getResource("/img/walp.jpg")).getImage();
        BackgroundPanel bgPanel = new BackgroundPanel(backgroundImage);
        setContentPane(bgPanel);

        setTitle("StructCheck - Kayıt Ol");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bgPanel.setLayout(null);

        Font modernFont = new Font("Segoe UI Black", Font.PLAIN, 18);

        JPanel panel = new RoundedPanel(50, new Color(60, 60, 60, 235));
        panel.setBounds(170, 18, 550, 770);
        panel.setLayout(null);

        JLabel lblBaslik = new JLabel("KULLANICI KAYIT", SwingConstants.CENTER);
        lblBaslik.setBounds(0, 12, 550, 30);
        lblBaslik.setFont(new Font("Segoe UI Black", Font.BOLD, 26));
        lblBaslik.setForeground(Color.WHITE);
        panel.add(lblBaslik);

        int x = 70, w = 400, h = 45, gap = 80, y = 80;

        txtAd = addField(panel, "Ad:", x, y, w, h, modernFont);   y += gap;
        txtSoyad = addField(panel, "Soyad:", x, y, w, h, modernFont); y += gap;
        txtTC = addField(panel, "TC:", x, y, w, h, modernFont); y += gap;
        txtTelefon = addField(panel, "Telefon:", x, y, w, h, modernFont); y += gap;
        txtEposta = addField(panel, "E-posta:", x, y, w, h, modernFont); y += gap;
        txtSehir = addField(panel, "Şehir:", x, y, w, h, modernFont); y += gap;

        JLabel lblSifre = new JLabel("Şifre:");
        lblSifre.setForeground(Color.WHITE);
        lblSifre.setFont(modernFont);
        lblSifre.setBounds(x, y-30, 200, 30);
        panel.add(lblSifre);

        txtSifre = createRoundedPasswordField();
        txtSifre.setBounds(x, y+5, w, h);
        panel.add(txtSifre);

        y += gap;

        JLabel lblRol = new JLabel("Rol Seçimi:");
        lblRol.setForeground(Color.WHITE);
        lblRol.setFont(modernFont);
        lblRol.setBounds(x, y-25, 200, 30);
        panel.add(lblRol);

        rbBireysel = new JRadioButton("Bireysel");
        rbKurum = new JRadioButton("Kurum");
        rbGonullu = new JRadioButton("Gönüllü");

        JRadioButton[] arr = { rbBireysel, rbKurum, rbGonullu };
        ButtonGroup grp = new ButtonGroup();
        int xx = x;
        for (JRadioButton rb : arr) {
            rb.setOpaque(false);
            rb.setForeground(Color.WHITE);
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            rb.setBounds(xx, y, 120, 30);
            grp.add(rb);
            panel.add(rb);
            xx += 120;
        }

        JButton btnKayitOl = createButton("Kayıt Ol");
        btnKayitOl.setBounds(x, y + 40, w, 55);
        panel.add(btnKayitOl);

        bgPanel.add(panel);

        btnKayitOl.addActionListener(e -> handleKayitOl());
    }

    private JTextField addField(JPanel panel, String label, int x, int y, int w, int h, Font font){
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(font);
        lbl.setBounds(x, y-30, 200, 20);
        panel.add(lbl);

        JTextField field = createRoundedField();
        field.setBounds(x, y+5, w, h);
        panel.add(field);
        return field;
    }

    private void handleKayitOl() {

        String ad      = txtAd.getText().trim();
        String soyad   = txtSoyad.getText().trim();
        String tc      = txtTC.getText().trim();
        String telefon = txtTelefon.getText().trim();
        String eposta  = txtEposta.getText().trim();
        String sehir   = txtSehir.getText().trim();
        String sifre   = new String(txtSifre.getPassword()).trim();

        if (ad.isEmpty() || soyad.isEmpty() || tc.isEmpty() || telefon.isEmpty()
                || eposta.isEmpty() || sehir.isEmpty() || sifre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
            return;
        }

        String rol = rbBireysel.isSelected() ? "Bireysel" :
                rbKurum.isSelected()    ? "Kurum" :
                        rbGonullu.isSelected()  ? "Gönüllü" : null;

        if (rol == null) {
            JOptionPane.showMessageDialog(this, "Rol seçiniz.");
            return;
        }

        int kullaniciID = -1;

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            String sql = """
                INSERT INTO Kullanici (Ad, Soyad, TC, Telefon, Eposta, Sifre, Rol, Sehir)
                OUTPUT INSERTED.KullaniciID
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, ad);
            pst.setString(2, soyad);
            pst.setString(3, tc);
            pst.setString(4, telefon);
            pst.setString(5, eposta);
            pst.setString(6, sifre);
            pst.setString(7, rol);
            pst.setString(8, sehir);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) kullaniciID = rs.getInt(1);

            PreparedStatement logPs = con.prepareStatement(
                    "INSERT INTO LogKaydi (KullaniciID, IslemTuru, TabloAdi, KayitID, Aciklama) " +
                            "VALUES (?, 'INSERT', 'Kullanici', ?, 'Yeni kullanıcı kaydı oluşturuldu')"
            );
            logPs.setInt(1, kullaniciID);
            logPs.setInt(2, kullaniciID);
            logPs.executeUpdate();


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            return;
        }

        if (rol.equals("Bireysel")) {
            showPopupAndGoHome(kullaniciID);
        } else if (rol.equals("Kurum")) {
            new Fkayit(kullaniciID).setVisible(true);
            dispose();
        } else {
            new Gkayit(kullaniciID).setVisible(true);
            dispose();
        }
    }

    private void showPopupAndGoHome(int kullaniciID) {
        class SuccessPopup extends BasePopup {

            public SuccessPopup(JFrame owner) {
                super(owner, 420, 220);

                JButton btnClose = createCircleCloseButton();
                btnClose.setBounds(dialogWidth - 55, 15, 35, 35);
                btnClose.addActionListener(e -> dispose());
                card.add(btnClose);

                JLabel lbl = new JLabel("Kayıt Başarılı", SwingConstants.CENTER);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
                lbl.setBounds(0, 55, dialogWidth, 30);
                card.add(lbl);

                JButton btn = createButton("Ana Sayfa");
                btn.setBounds(100, 120, 220, 50);
                btn.addActionListener(e -> {
                    dispose();
                    new Kanasayfa(kullaniciID).setVisible(true);
                    Kkayit.this.dispose();
                });
                card.add(btn);
            }
        }

        new SuccessPopup(this).setVisible(true);
    }



    private JButton createButton(String text){
        JButton btn = new JButton(text){
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,80));
                g2.fillRoundRect(0,5,getWidth(),getHeight()-5,25,25);
                g2.setColor(new Color(60,60,60));
                g2.fillRoundRect(0,0,getWidth(),getHeight()-5,25,25);
                super.paintComponent(g);
            }
        };
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Black", Font.PLAIN, 18));
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTextField createRoundedField(){
        JTextField txt = new JTextField(){
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,40));
                g2.fillRoundRect(2,4,getWidth()-4,getHeight()-6,30,30);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0,0,getWidth()-6,getHeight()-6,30,30);
                super.paintComponent(g2);
            }
        };
        txt.setOpaque(false);
        txt.setBorder(BorderFactory.createEmptyBorder(10,15,10,10));
        return txt;
    }

    private JPasswordField createRoundedPasswordField(){
        JPasswordField txt = new JPasswordField(){
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,40));
                g2.fillRoundRect(2,4,getWidth()-4,getHeight()-6,30,30);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0,0,getWidth()-6,getHeight()-6,30,30);
                super.paintComponent(g2);
            }
        };
        txt.setOpaque(false);
        txt.setBorder(BorderFactory.createEmptyBorder(10,15,10,10));
        return txt;
    }

    class BackgroundPanel extends JPanel{
        private Image img;
        public BackgroundPanel(Image img){ this.img = img; }
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(img,0,0,getWidth(),getHeight(),this);
        }
    }

    class RoundedPanel extends JPanel{
        private int r;
        private Color c;
        RoundedPanel(int r, Color c){ this.r=r; this.c=c; setOpaque(false);}
        @Override
        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0,0,18,100));
            g2.fillRoundRect(8,8,getWidth()-16,getHeight()-16,r,r);
            g2.setColor(c);
            g2.fillRoundRect(0,0,getWidth()-16,getHeight()-16,r,r);
        }
    }
}
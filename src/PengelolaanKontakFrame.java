/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
/**
 *
 * @author Fetra
 */
public class PengelolaanKontakFrame extends javax.swing.JFrame {

    /**
     * Creates new form PengelolaanKontakFrame
     */
    public PengelolaanKontakFrame() {
        initComponents();
        loadContactsToTable();
        setButtonActions();
    }
    
    private void setButtonActions() {
        btnTambah.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                tambahKontak();
            }
        });

        btnUbah.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ubahKontak();
            }
        });

        btnHapus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                hapusKontak();
            }
        });

        jButton4.addActionListener(new ActionListener() { // Search button
            public void actionPerformed(ActionEvent evt) {
                cariKontak();
            }
        });

        btnMuat.addActionListener(new ActionListener() { // Refresh button
            public void actionPerformed(ActionEvent evt) {
                loadContactsToTable();
            }
        });
        
        btnSimpan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exportContactsToCSV();
            }
        });

        btnMuat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                importContactsFromCSV();
            }
        });
    }

    private void tambahKontak() {
        String nama = txtNama.getText();
        String noTelepon = txtTelpon.getText();
        String kategori = cbKategori.getSelectedItem().toString();

        if (!isValidPhoneNumber(noTelepon)) {
            JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka dan harus memiliki panjang antara 10-15 karakter.");
            return;
        }

        KelolaDatabase.tambahKontak(nama, noTelepon, kategori);
        loadContactsToTable();
        clearInputFields();
    }

    private void ubahKontak() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diubah.");
            return;
        }

        int id = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());
        String nama = txtNama.getText();
        String noTelepon = txtTelpon.getText();
        String kategori = cbKategori.getSelectedItem().toString();

        if (!isValidPhoneNumber(noTelepon)) {
            JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka dan harus memiliki panjang antara 10-15 karakter.");
            return;
        }

        KelolaDatabase.perbaruiKontak(id, nama, noTelepon, kategori);
        loadContactsToTable();
        clearInputFields();
    }

    private void hapusKontak() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin dihapus.");
            return;
        }

        int id = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());
        KelolaDatabase.hapusKontak(id);
        loadContactsToTable();
        clearInputFields();
    }

    private void cariKontak() {
        String keyword = jTextField3.getText();
        List<Map<String, String>> kontakList = KelolaDatabase.cariKontak(keyword);
        displayContactsInTable(kontakList);
    }

    private void loadContactsToTable() {
        List<Map<String, String>> kontakList = KelolaDatabase.ambilKontak();
        displayContactsInTable(kontakList);
    }

    private void displayContactsInTable(List<Map<String, String>> kontakList) {
        String[] columns = {"ID", "Nama", "No Telepon", "Kategori"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (Map<String, String> kontak : kontakList) {
            model.addRow(new Object[]{
                kontak.get("id"),
                kontak.get("nama"),
                kontak.get("no_telepon"),
                kontak.get("kategori")
            });
        }

        jTable1.setModel(model);
    }
    
    private void exportContactsToCSV() {
        try (FileWriter fileWriter = new FileWriter("kontak.csv")) {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            int rowCount = model.getRowCount();
            int columnCount = model.getColumnCount();

            for (int i = 0; i < columnCount; i++) {
                fileWriter.write(model.getColumnName(i) + (i == columnCount - 1 ? "\n" : ","));
            }

            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    fileWriter.write(model.getValueAt(row, col).toString() + (col == columnCount - 1 ? "\n" : ","));
                }
            }

            JOptionPane.showMessageDialog(this, "Daftar kontak berhasil diekspor ke kontak.csv");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengekspor kontak: " + e.getMessage());
        }
    }
    
    private void importContactsFromCSV() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("kontak.csv"))) {
            String line = bufferedReader.readLine(); // Skip header
            List<Map<String, String>> kontakList = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String id = values[0];
                    String nama = values[1];
                    String noTelepon = values[2];
                    String kategori = values[3];

                    KelolaDatabase.tambahKontak(nama, noTelepon, kategori); // Simpan ke database
                }
            }

            loadContactsToTable(); // Refresh table after import
            JOptionPane.showMessageDialog(this, "Daftar kontak berhasil dimuat dari kontak.csv");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat kontak: " + e.getMessage());
        }
    }



    private boolean isValidPhoneNumber(String noTelepon) {
        return noTelepon.matches("\\d{10,15}");
    }

    private void clearInputFields() {
        txtNama.setText("");
        txtTelpon.setText("");
        cbKategori.setSelectedIndex(0);
    }
    
    private void populateFieldsFromTable(int selectedRow) {
        // Get data from selected row
        String nama = jTable1.getValueAt(selectedRow, 1).toString();
        String noTelepon = jTable1.getValueAt(selectedRow, 2).toString();
        String kategori = jTable1.getValueAt(selectedRow, 3).toString();

        // Set data to input fields
        txtNama.setText(nama);
        txtTelpon.setText(noTelepon);
        cbKategori.setSelectedItem(kategori);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtTelpon = new javax.swing.JTextField();
        cbKategori = new javax.swing.JComboBox<>();
        btnTambah = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        btnMuat = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Aplikasi Pengelolaan Kontak");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Aplikasi Pengelolaan Kontak");
        jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Nama");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(jLabel2, gridBagConstraints);

        jLabel3.setText("No Telepon");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Kategori");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(txtNama, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(txtTelpon, gridBagConstraints);

        cbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Teman", "Keluarga", "Kerjaan" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(cbKategori, gridBagConstraints);

        btnTambah.setText("Tambah Kontak");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(btnTambah, gridBagConstraints);

        btnUbah.setText("Ubah Kontak");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(btnUbah, gridBagConstraints);

        btnHapus.setText("Hapus Kontak");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(btnHapus, gridBagConstraints);

        getContentPane().add(jPanel2, java.awt.BorderLayout.WEST);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel5.setText("Masukkan Nama atau No Telepon");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel3.add(jLabel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 15);
        jPanel3.add(jTextField3, gridBagConstraints);

        jButton4.setText("Cari");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 15);
        jPanel3.add(jButton4, gridBagConstraints);

        btnMuat.setText("Muat");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 15);
        jPanel3.add(btnMuat, gridBagConstraints);

        btnSimpan.setText("Simpan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 15);
        jPanel3.add(btnSimpan, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 300));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setPreferredSize(new java.awt.Dimension(60, 80));
        jTable1.setShowGrid(true);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel3.add(jScrollPane1, gridBagConstraints);

        getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) {
                    populateFieldsFromTable(selectedRow);
                }
    }//GEN-LAST:event_jTable1MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaanKontakFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnMuat;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cbKategori;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtTelpon;
    // End of variables declaration//GEN-END:variables
}

# Tugas 3: Aplikasi Pengelolaan Kontak

### Pembuat
- **Nama**: Ferdhyan Dwi Rangga Saputra
- **NPM**: 2210010171

---

## 1. Deskripsi Program
Aplikasi ini memungkinkan pengguna untuk:
- Mengelola kontak dengan menambah, mengedit, menghapus, dan menampilkan daftar kontak.
- Menyimpan daftar kontak ke dalam file CSV dan memuat data kontak dari file CSV.
- Mencari kontak berdasarkan nama atau nomor telepon.
- Memastikan nomor telepon yang dimasukkan hanya berupa angka dengan panjang yang sesuai.

## 2. Komponen GUI
- **JFrame**: Window utama aplikasi.
- **JPanel**: Panel untuk menampung komponen.
- **JLabel**: Label untuk menampilkan informasi input.
- **JTextField**: Input untuk nama, nomor telepon, kategori, dan pencarian.
- **JButton**: Tombol untuk menambah, mengedit, menghapus, mencari, mengekspor, dan mengimpor kontak.
- **JComboBox**: Dropdown untuk memilih kategori kontak.
- **JTable**: Tabel untuk menampilkan daftar kontak.

## 3. Logika Program
- Menggunakan database SQLite untuk menyimpan data kontak secara lokal.
- Menampilkan daftar kontak di **JTable**.
- Menyimpan dan memuat daftar kontak dari file CSV.
- Validasi nomor telepon agar hanya berupa angka dan memiliki panjang yang sesuai.

## 4. Events
Menggunakan **ActionListener** untuk menangani interaksi pengguna:

### A. Tombol Tambah Kontak
Menambahkan kontak baru berdasarkan input dari pengguna.

```java
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
```

### B. Tombol Cari Kontak
Mencari kontak berdasarkan nama atau nomor telepon yang dimasukkan dan menampilkan hasil di **JTable**.

```java
private void cariKontak() {
        String keyword = jTextField3.getText();
        List<Map<String, String>> kontakList = KelolaDatabase.cariKontak(keyword);
        displayContactsInTable(kontakList);
    }
```

## 5. Variasi
Aplikasi ini memiliki variasi tambahan berikut:

### A. Pencarian Kontak
Pengguna dapat mencari kontak berdasarkan nama atau nomor telepon. Hasil pencarian akan ditampilkan di **JTable**.

```java
public static List<Map<String, String>> cariKontak(String keyword) {
    List<Map<String, String>> kontakList = new ArrayList<>();
    String sql = "SELECT * FROM kontak WHERE nama LIKE ? OR no_telepon LIKE ?";

        try (Connection conn = koneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Gunakan '%' untuk pencarian mirip (LIKE)
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, String> kontak = new HashMap<>();
                kontak.put("id", String.valueOf(rs.getInt("id")));
                kontak.put("nama", rs.getString("nama"));
                kontak.put("no_telepon", rs.getString("no_telepon"));
                kontak.put("kategori", rs.getString("kategori"));
                kontakList.add(kontak);
            }
        } catch (SQLException e) {
            System.out.println("Error saat mencari kontak: " + e.getMessage());
        }
        return kontakList;
```

### B. Validasi Input Nomor Telepon
Nomor telepon divalidasi agar hanya berisi angka dengan panjang 10 hingga 15 digit.

```java
private boolean isValidPhoneNumber(String noTelepon) {
        return noTelepon.matches("\\d{10,15}");
    }
```

### C. Ekspor Data ke CSV
Aplikasi dapat mengekspor data kontak ke dalam file CSV.

```java
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
```

### D. Impor Data dari CSV
Aplikasi dapat mengimpor data kontak dari file CSV.

```java
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
```

## 6. Tampilan Saat di Jalankan



## 7. Indikator Penilaian

| No  | Komponen          | Persentase |
| :-: | ------------------ | :--------: |
|  1  | Komponen GUI      |     15%    |
|  2  | Logika Program    |     20%    |
|  3  | Events            |     10%    |
|  4  | Kesesuaian UI     |     15%    |
|  5  | Memenuhi Variasi  |     40%    |
|     | **TOTAL**         |  **100%**  |

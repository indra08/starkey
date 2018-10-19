package id.starkey.pelanggan.Kunci.PilihKunci.Motor;

/**
 * Created by Dani on 3/17/2018.
 */

public class ListItemKunciMotor {
    private String id;
    private String nama_kunci;
    private String merk;
    private String harga_kunci;
    private String biaya_layanan;
    private String harga;
    private String gambar;

    public ListItemKunciMotor(String id, String nama_kunci, String merk, String harga_kunci, String biaya_layanan, String harga, String gambar) {
        this.id = id;
        this.nama_kunci = nama_kunci;
        this.merk = merk;
        this.harga_kunci = harga_kunci;
        this.biaya_layanan = biaya_layanan;
        this.harga = harga;
        this.gambar = gambar;
    }

    public String getId() {
        return id;
    }

    public String getNama_kunci() {
        return nama_kunci;
    }

    public String getMerk() {
        return merk;
    }

    public String getHarga_kunci() {
        return harga_kunci;
    }

    public String getBiaya_layanan() {
        return biaya_layanan;
    }

    public String getHarga() {
        return harga;
    }

    public String getGambar() {
        return gambar;
    }
}

package id.starkey.pelanggan.History.DalamProses;

/**
 * Created by Dani on 4/2/2018.
 */

public class ListItemDalamProses {

    private String id;
    private String id_layanan;
    private String id_biaya_layanan;
    private String id_layanan_kunci;
    private String nama_layanan_kunci;
    private String id_kunci;
    private String nama_kunci;
    private String biaya;
    private String tanggal;
    private String status_code;
    private String status;
    private String is_rated;

    public ListItemDalamProses(String id, String id_layanan, String id_biaya_layanan, String id_layanan_kunci,
                               String nama_layanan_kunci, String id_kunci, String nama_kunci, String biaya, String tanggal,
                               String status_code, String status, String is_rated) {
        this.id = id;
        this.id_layanan = id_layanan;
        this.id_biaya_layanan = id_biaya_layanan;
        this.id_layanan_kunci = id_layanan_kunci;
        this.nama_layanan_kunci = nama_layanan_kunci;
        this.id_kunci = id_kunci;
        this.nama_kunci = nama_kunci;
        this.biaya = biaya;
        this.tanggal = tanggal;
        this.status_code = status_code;
        this.status = status;
        this.is_rated = is_rated;
    }

    public String getId() {
        return id;
    }

    public String getId_layanan() {
        return id_layanan;
    }

    public String getId_biaya_layanan() {
        return id_biaya_layanan;
    }

    public String getId_layanan_kunci() {
        return id_layanan_kunci;
    }

    public String getNama_layanan_kunci() {
        return nama_layanan_kunci;
    }

    public String getId_kunci() {
        return id_kunci;
    }

    public String getNama_kunci() {
        return nama_kunci;
    }

    public String getBiaya() {
        return biaya;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getStatus_code() {
        return status_code;
    }

    public String getStatus() {
        return status;
    }

    public String getIs_rated() {
        return is_rated;
    }
}

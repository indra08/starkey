package id.starkey.pelanggan.Utilities;

public class LastOrder {

    private String idLayanan, idKunci, alamat, qty, keterangan, total, gambar, namaStemp, jenisStamp, ukuranStemp, waktuAwal;
    private Double latitude, longitude;
    private int totalEstimasi;

    public void createKunciOrder(String idLayanan
            ,String idKunci
            ,Double lati
            ,Double longi
            ,String alamat
            ,String qty
            ,String keterangan
            ,String total
            ,int estimasi
            ,String gambar){

        this.idLayanan = idLayanan;
        this.idKunci = idKunci;
        this.latitude =  lati;
        this.longitude = longi;
        this.alamat = alamat;
        this.qty = qty;
        this.keterangan = keterangan;
        this.total = total;
        this.totalEstimasi = estimasi;
        this.gambar = gambar;
    }

    public void createStampelOrder(String namaStempel
            ,String ukuran
            ,Double lati
            ,Double longi
            ,String alamat
            ,String qty
            ,String keterangan
            ,String waktuAwal
            ,String estimasi){

        this.namaStemp = namaStempel;
        this.ukuranStemp = ukuran;
        this.latitude = lati;
        this.longitude = longi;
        this.alamat = alamat;
        this.qty = qty;
        this.keterangan = keterangan;
        this.waktuAwal = waktuAwal;
        this.total = estimasi;
    }

    public String getIdLayanan() {
        return idLayanan;
    }

    public void setIdLayanan(String idLayanan) {
        this.idLayanan = idLayanan;
    }

    public String getIdKunci() {
        return idKunci;
    }

    public void setIdKunci(String idKunci) {
        this.idKunci = idKunci;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getNamaStemp() {
        return namaStemp;
    }

    public void setNamaStemp(String namaStemp) {
        this.namaStemp = namaStemp;
    }

    public String getJenisStamp() {
        return jenisStamp;
    }

    public void setJenisStamp(String jenisStamp) {
        this.jenisStamp = jenisStamp;
    }

    public String getUkuranStemp() {
        return ukuranStemp;
    }

    public void setUkuranStemp(String ukuranStemp) {
        this.ukuranStemp = ukuranStemp;
    }

    public String getWaktuAwal() {
        return waktuAwal;
    }

    public void setWaktuAwal(String waktuAwal) {
        this.waktuAwal = waktuAwal;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getTotalEstimasi() {
        return totalEstimasi;
    }

    public void setTotalEstimasi(int totalEstimasi) {
        this.totalEstimasi = totalEstimasi;
    }
}

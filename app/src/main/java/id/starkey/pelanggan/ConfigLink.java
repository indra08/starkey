package id.starkey.pelanggan;

/**
 * Created by Dani on 3/11/2018.
 */

public class ConfigLink {

    private static String base_url = "https://api.starkey.id";

    //REST API
    //REGISTRATION
    public static final String sendCode = "https://api.starkey.id/api/send_code";
    public static final String verifyCode = "https://api.starkey.id/api/verify_code";
    public static final String registrationDetail = "https://api.starkey.id/api/registration_detail";

    //LOGIN
    public static final String login = "https://api.starkey.id/api/user/login";

    //get foto profil
    public static final String getFotoProfil = "https://api.starkey.id/api/user/profile_photo";

    //FEATURE
    public static final String jenisLayanan = "https://api.starkey.id/api/layanan_kunci";
    //public static final String mobil_kunci = "http://103.28.52.123/api/kunci?category=mobil&merk=toyota&sort=asc&keyword=";


    public static final String combo_mobil = "https://api.starkey.id/api/merk_kunci?category=mobil";
    public static final String combo_motor = "https://api.starkey.id/api/merk_kunci?category=motor";
    public static final String combo_lain = "https://api.starkey.id/api/merk_kunci?category=lain";
    public static final String combo_pintu = "https://api.starkey.id/api/merk_kunci?category=pintu";
    public static final String combo_almari = "https://api.starkey.id/api/merk_kunci?category=almari";
    public static final String estimasi_harga = "https://api.starkey.id/api/estimasi_harga"; //id 47 dan 31 yg ada harganya

    //upload photo
    public static final String upload_foto = "https://api.starkey.id/api/user/profile_photo/update";

    //get mitra kunci
//    public static final String getMitraKunci = "https://api.starkey.id/api/get_mitra_kunci";
    //get mitra kunci new
    public static final String getMitraKunciNew = "https://api.starkey.id/api/v1.1/get_mitra_kunci";

    //location update user
    public static final String update_location_user = "https://api.starkey.id/api/user/status/update";

    //for sharepreferences
    public static final String loginFromRegister = "loginfromregister";
    public static final String loginPref = "login";
    public static final String idLayananPref = "idlayananpref";

    //for pref firebasetoken in here
    public static final String firebasePref = "firebase";

    //for sharepreferences image
    public static final String imagePref = "image";

    public static final String countPrefChangeLog = "changelog";

    //status trx proses
    public static final String status_transaksi_proses = "https://api.starkey.id/api/transaction/user?type_history=proses";

    //status trx selesai
    public static final String status_transaksi_selesai = "https://api.starkey.id/api/transaction/user?type_history=selesai";

    //user cancel trx kunci
    public static final String user_cancel_transaction = "https://api.starkey.id/api/user_cancel";

    //user cancel trx stempel
    public static final String user_cancel_transaction_stempel = "https://api.starkey.id/api/stempel/user_cancel";

    //ratting mitra
    public static final String ratting_mitra = "https://api.starkey.id/api/user_feedback_transaction";

    //jenis stempel
    public static final String jenis_stempel = "https://api.starkey.id/api/stempel";

    //ukuran stempel
    public static final String ukuran_stempel = "https://api.starkey.id/api/stempel/size";

    //get mitra stempel
    public static final String getMitraStempel = "https://api.starkey.id/api/stempel/get_mitra";

    //update password
    public static final String update_password = "https://api.starkey.id/api/user/password/update";

    //password reset code
    public static final String password_reset_code = "https://api.starkey.id/api/user/password_reset_code";

    //verify code reset password
    public static final String verify_code_reset_password = "https://api.starkey.id/api/user/verify_code_reset_password";

    //reset code pass
    public static final String reset_pass = "https://api.starkey.id/api/user/password/reset";

    //trx outstanding user
    public static final String transaksi_checker = "https://api.starkey.id/api/user/transaction_checker";

    //maintenance status
    public static final String maintenance_status = base_url + "/api/v1/maintenance_status";

    //changelog info
    public static final String changeLogInfo = base_url + "/api/v1/app_changelog?";

    // API V1.2
    public static final String saveCustomerService = base_url + "/api/v1.2/customer_service";
    public static final String getVersion = base_url + "/api/v1.2/update_versi";
    public static final String savePembatalanOrder = base_url + "/api/v1.2/pembatalan_order";

}

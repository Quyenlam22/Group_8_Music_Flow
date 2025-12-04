package com.vn.btl.utils;

public class LanguageManager {

    public static final String LANG_EN = "en";
    public static final String LANG_VI = "vi";

    // Lấy text theo key và ngôn ngữ
    public static String getText(String key, String lang) {
        if (LANG_VI.equals(lang)) {
            switch (key) {
                case "app_name": return "Music Flow";
                case "label_home": return "Trang chủ";
                case "label_songs": return "Bài hát";
                case "label_settings": return "Cài đặt";
                case "label_playlist": return "Bài hát của tôi";
                case "settings_dark_theme": return "Giao diện";
                case "settings_language": return "Ngôn ngữ";
                case "settings_account": return "Tài khoản";
                case "settings_logout": return "Đăng xuất";
                case "btn_save": return "Lưu";
                case "label_new_albums": return "Album Mới";
                case "btn_cancel": return "Huỷ bỏ";
                case "btn_all": return "Tất cả >"; // Vietnamese
                case "rb_male": return "Nam";
                case "rb_female": return "Nữ";
                case "rb_other": return "Khác";
                case "label_popular": return "Phổ biến";
                case "tab_all_songs": return "Tất cả";
                case "tab_playlists": return "Danh sách";
                case "tab_albums": return "Bộ sưu tập";
                case "tab_artists": return "Nghệ sĩ";
                case "nav_home": return "Trang chủ";
                case "nav_playlist": return "Danh sách phát";
                case "nav_songs": return "Bài hát";
                case "nav_settings": return "Cài đặt";
                case "label_trending_search": return "Xu hướng tìm kiếm";
                case "playlist_title_default": return "Bài hát của tôi";
                case "label_tracks_count": return "24 bài";
                case "btn_play_all": return "Phát tất cả";
                case "label_artists_in_playlist": return "Nghệ sĩ trong playlist";
                case "btn_see_all": return "Xem thêm";



                //account

                case "acc_username": return "Tên đăng nhập";
                case "acc_name": return "Họ và tên";
                case "acc_email": return "Email";
                case "acc_gender": return "Giới tính";
                case "acc_phone": return "Số điện thoại";
                case "acc_password": return "Mật khẩu";
                case "acc_dob": return "Ngày sinh";

                case "login_hint": return "Nhập Email hoặc Tên đăng nhập";
                case "password_hint": return "Mật khẩu";
                case "login_button": return "ĐĂNG NHẬP";
                case "forgot_password": return "Quên mật khẩu?";
                case "or_text": return "HOẶC";
                case "no_account": return "Chưa có tài khoản?";
                case "register": return "Đăng ký";
                case "register_title": return "ĐĂNG KÝ";
                case "confirm_password": return "Xác nhận mật khẩu";
                case "btn_register": return "ĐĂNG KÝ";
                case "already_account": return "Đã có tài khoản?";
                case "login": return "Đăng nhập";
                case "search_artist_hint": return "Tìm nghệ sĩ...";
                case "btn_done": return "XONG";





                default: return key;
            }
        } else { // mặc định English
            switch (key) {
                case "app_name": return "Music Flow";
                case "label_home": return "Home";
                case "label_songs": return "Songs";
                case "label_settings": return "Setting";
                case "label_playlist": return "My Playlist";
                case "settings_dark_theme": return "Dark theme";
                case "settings_language": return "Language";
                case "settings_account": return "Account";
                case "settings_logout": return "Logout";
                case "btn_save": return "Save";
                case "label_trending_search": return "Trending search";
                case "rb_male": return "Male";
                case "rb_female": return "Female";
                case "rb_other": return "Other";

                case "nav_home": return "Home";
                case "nav_playlist": return "My Playlist";
                case "nav_songs": return "Songs";
                case "nav_settings": return "Setting";
                case "btn_cancel": return "Cancel";
                case "label_new_albums": return "New Albums";
                case "tab_all_songs": return "All songs";
                case "tab_playlists": return "Playlists";
                case "tab_albums": return "Albums";
                case "tab_artists": return "Artists";
                 // Tiếng Việt
                case "label_popular": return "Popular";
                case "playlist_title_default": return "My Playlist";
                case "label_tracks_count": return "tracks";
                case "btn_play_all": return "Play All";
                case "label_artists_in_playlist": return "Artists in Playlist";
                case "btn_see_all": return "See All";


                case "acc_username": return "Username";
                case "acc_name": return "Full Name";
                case "acc_email": return "Email";
                case "acc_gender": return "Gender";
                case "acc_phone": return "Phone";
                case "acc_password": return "Password";
                case "acc_dob": return "Date of Birth";

                case "login_hint": return "Enter Email or Username";
                case "password_hint": return "Password";
                case "login_button": return "LOGIN";
                case "forgot_password": return "Forgot Password ?";
                case "or_text": return "OR";
                case "no_account": return "Don’t have an account?";
                case "register": return "Register";
                case "register_title": return "REGISTER"; // English
                case "confirm_password": return "Confirm Password";
                case "btn_register": return "REGISTER";
                case "already_account": return "Already have an account?";
                case "login": return "Login";
                case "search_artist_hint": return "Search artist...";
                case "btn_done": return "DONE";





                case "btn_all": return "All >"; // English
                default: return key;
            }
        }
    }
}

# Báo cáo kiểm thử — Quản lý Ký túc xá Sinh viên (QL_KyTucXa)

## Môi trường kiểm thử
- JDK: OpenJDK 17.0.6 (`javac 17.0.6`) trên Windows 10 / Ubuntu 22.04.
- Ngày kiểm thử: 12/05/2023.
- Giao diện: Console (`dorm.Main`) và Java Swing (`dorm.DormApp`).

## Lệnh biên dịch và kiểm thử
```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java") tests/TestDorm.java
java -cp out dorm.TestDorm
java -cp out dorm.Main --demo
```

## Kết quả test tự động: 30 PASS, 0 FAIL
```
PASS: Seed >=3 toa
PASS: Seed >=12 phong
PASS: Seed >=15 SV
PASS: Login admin ok
PASS: Login staff ok, khong phai admin
PASS: Login sai mat khau
PASS: Login trong
PASS: Staff xoa SV bi chan (Chi ADMIN moi duoc xoa du lieu)
PASS: SV001 van con sau khi staff xoa that bai
PASS: Trung ma SV bi chan (Trung ma SV: SV001)
PASS: SV rong bi chan (Ma SV rong)
PASS: A101 day sau dang ky thu 4
PASS: Dang ky vao phong day bi chan (Phong A101 da day (4 giuong))
PASS: Nu vao phong Nam bi chan (Phong danh cho Nam, SV gioi tinh Nu khong phu hop)
PASS: Chuyen phong thanh cong
PASS: A101 khong con day sau chuyen
PASS: Bill seed tinh dung
PASS: Bill moi tinh dung
PASS: Chi so moi < cu bi chan (Chi so dien moi < cu)
PASS: Trung ma bill bi chan (Trung ma hoa don)
PASS: Tien thang = tien phong + chia dien nuoc (> gia phong)
PASS: Tim theo ten
PASS: Tim theo ma
PASS: Tim khong thay -> rong
PASS: Thong ke tong phong dung
PASS: Thong ke giuong
PASS: Luu/doc file: so SV khop
PASS: Luu/doc file: SV100 o A201
PASS: Admin xoa SV duoc
PASS: Staff xoa phong bi chan (Chi ADMIN moi duoc xoa du lieu)
Tong: 30 PASS, 0 FAIL
```

## Kiểm thử Console & Demo
- `--demo`: in đúng 3 tòa, 12 phòng với sức chứa (tổng 60 giường, 13 giường đã ở), 15 SV, thống kê `emptyRooms=6, fullRooms=0, partialRooms=6, studentsWithoutRoom=2`, đăng nhập admin = true.
- Console tương tác: đăng nhập `admin/admin123` thành công, chọn mục 4 in đúng danh sách phòng + thống kê, chọn 0 lưu và thoát.

## Phạm vi bao phủ
Chức năng chính, nhập sai (mật khẩu sai, chỉ số mới < cũ), dữ liệu trống (mã SV rỗng, đăng nhập rỗng), trùng dữ liệu (mã SV, mã hóa đơn), phân quyền xóa, phòng đầy, sai giới tính phòng, lưu/đọc file.

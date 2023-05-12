# Quản lý Ký túc xá Sinh viên

## 1. Tên đồ án
Hệ thống Quản lý Ký túc xá Sinh viên (QL_KyTucXa — Nhóm 1).

## 2. Môn học tương ứng
Công nghệ Java — Lập trình hướng đối tượng với Java, Java Swing, kiến trúc phân lớp Repository/Service.

## 3. Giới thiệu đề tài
Phần mềm quản lý sinh viên nội trú, tòa nhà, phòng và giường, hợp đồng nội trú, hóa đơn điện nước và thống kê phòng trống/đầy cho ban quản lý ký túc xá trường đại học. Chạy hoàn toàn bằng Java thuần, **không cần thư viện ngoài và không cần cài DB server**.

## 4. Mục tiêu thực hiện
- Áp dụng OOP Java: Class/Object, Encapsulation, Inheritance/Interface qua lớp Repository, Polymorphism.
- Đăng nhập và phân quyền ADMIN/STAFF thực tế (STAFF bị chặn khi xóa).
- Kiểm soát sức chứa giường: không cho đăng ký vào phòng đã đầy, không cho ở khác giới tính tòa/phòng.
- Tính tiền chính xác: tiền phòng + điện/nước theo chỉ số và đơn giá, chia đều theo số người trong phòng.
- Lưu dữ liệu thật bằng file (xem mục 7 và `docs/DB.md` về cách chuyển sang JDBC).

## 5. Công nghệ sử dụng
- Java 17+ (chỉ dùng JDK chuẩn: `java.time`, `java.nio.file`, Collections, Stream, Swing).
- Persistence: file văn bản dạng `|` trong `data/` qua lớp `DataStore` (Repository).
- GUI: Java Swing (`DormApp`). Console: `Main`.
- Kiểm thử: `tests/TestDorm.java` chạy bằng `main` + assert thủ công, không cần JUnit.

> **Lưu trữ dữ liệu:** Dự án lưu trữ dữ liệu trực tiếp trên file văn bản định dạng `|` để dễ dàng chạy ngay trên mọi máy có JDK mà không cần cài đặt DB server. Xem `docs/DB.md` để biết hướng dẫn chuyển đổi lớp `DataStore` sang `JdbcDataStore` (SQLite/MySQL) khi mở rộng.

## 6. Danh sách chức năng
1. Đăng nhập, phân quyền ADMIN/STAFF; STAFF xóa bị chặn (`SecurityException`), ADMIN xóa được.
2. Quản lý sinh viên nội trú: thêm, sửa thông tin cơ bản, xóa (ADMIN), tra cứu theo mã/tên/lớp.
3. Quản lý tòa nhà (≥3 tòa mẫu) và phòng (12 phòng mẫu), số giường = sức chứa từng phòng.
4. Đăng ký phòng: kiểm tra phòng tồn tại, còn giường, đúng giới tính; tự tạo hợp đồng.
5. Chuyển phòng: kiểm tra phòng mới, kết thúc hợp đồng cũ, tạo hợp đồng mới.
6. Quản lý hợp đồng nội trú: ngày bắt đầu/kết thúc, tiền cọc, giá tháng, trạng thái ACTIVE/ENDED, tổng tiền phòng theo số tháng.
7. Hóa đơn điện nước: chỉ số cũ/mới × đơn giá cho điện và nước, tổng bill, trạng thái đã trả.
8. Tính tiền tháng của 1 SV = tiền phòng + (điện+nước của phòng ÷ số người trong phòng).
9. Thống kê: tổng phòng, phòng trống, phòng đầy, phòng còn chỗ, tổng giường, giường đã ở, SV chưa có phòng.
10. Lưu/đọc dữ liệu từ file, tự seed dữ liệu mẫu nếu thư mục data trống.

## 7. Kiến trúc và cấu trúc thư mục
```
QL_KyTucXa/
├── src/main/java/dorm/
│   ├── User.java        # tai khoan + Role ADMIN/STAFF
│   ├── Student.java     # sinh vien noi tru
│   ├── Building.java    # toa nha
│   ├── Room.java        # phong, so giuong/suc chua, gia thang
│   ├── Contract.java    # hop dong noi tru
│   ├── UtilityBill.java # hoa don dien/nuoc
│   ├── DataStore.java   # Repository: luu/doc file, seed mau
│   ├── DormService.java # nghiep vu: dang nhap, dang ky/chuyen phong, bill, thong ke
│   ├── Main.java        # giao dien console + che do --demo
│   └── DormApp.java     # giao dien Java Swing
├── data/                # users/buildings/rooms/students/contracts/bills (.txt)
├── tests/TestDorm.java  # 30 kiem thu tu dong
├── docs/                # DB.md, TEST_REPORT.md
├── assets/              # (de trong - screenshot bo sung sau khi chay GUI)
├── README.md  .gitignore  LICENSE  CONTRIBUTING.md
├── run.sh / run.bat     # bien dich + chay nhanh
└── run-tests.sh
```
Kiến trúc 3 lớp: **GUI/Console → DormService (nghiệp vụ) → DataStore (Repository)**. Thay lớp Repository là thay cách lưu trữ mà không sửa nghiệp vụ (xem `docs/DB.md`).

## 8. Hình ảnh giao diện (screenshots)
Giao diện Java Swing gồm 4 tab: *Sinh viên*, *Phòng & Tòa*, *Hóa đơn điện nước*, *Thống kê*. Để chụp ảnh giao diện: chạy GUI theo mục 10 và lưu ảnh vào thư mục `assets/`.

## 9. Hướng dẫn cài đặt môi trường
- Cài JDK 17 trở lên (Oracle JDK, Temurin hoặc OpenJDK). Kiểm tra: `javac -version` và `java -version`.
- Không cần Maven/Gradle, không cần thư viện ngoài, không cần MySQL/SQLite server.

## 10. Hướng dẫn chạy chương trình
```bash
# Bien dich
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java") tests/TestDorm.java

# Chay kiem thu
java -cp out dorm.TestDorm

# Chay che do demo (khong can nhap lieu)
java -cp out dorm.Main --demo

# Chay console (tu thu muc du an, de doc data/)
java -cp out dorm.Main

# Chay giao dien Swing (can man hinh)
java -cp out dorm.DormApp
```
Hoặc dùng `./run.sh` (Linux/macOS) / `run.bat` (Windows), `./run-tests.sh` để chạy test.

## 11. Tài khoản demo
| Tài khoản | Mật khẩu | Quyền |
|---|---|---|
| `admin` | `admin123` | ADMIN — toàn quyền, được xóa |
| `staff` | `staff123` | STAFF — dùng hằng ngày, **không được xóa** |

Đây **chỉ là tài khoản demo** phục vụ học tập/chấm bài. Khi triển khai thật phải đổi mật khẩu và băm mật khẩu (BCrypt), không dùng mật khẩu mặc định.

Dữ liệu mẫu: 3 tòa (A/B/C), 12 phòng, 60 giường, 15 sinh viên, 2 hợp đồng, 2 hóa đơn.

## 12. Hướng phát triển trong tương lai
- Thay `DataStore` bằng JDBC SQLite rồi MySQL theo `docs/DB.md`; băm mật khẩu BCrypt.
- Thêm quản lý khách thăm, vi phạm nội quy, đặt phòng online cho sinh viên.
- Xuất báo cáo Excel/PDF, biểu đồ tỉ lệ lấp đầy theo tòa/tháng.
- Phân quyền chi tiết hơn (kế toán chỉ xem hóa đơn), nhật ký thao tác (audit log).

## Giấy phép
MIT — xem `LICENSE`.

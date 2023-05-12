# Cơ sở dữ liệu & cách chuyển sang JDBC

## Hiện trạng lưu trữ
- Dự án hiện dùng **file persistence** qua `dorm.DataStore`: mỗi bảng là 1 file `.txt` trong `data/`, các trường ngăn cách bằng `|`.
- Không yêu cầu cài đặt DB server (MySQL/SQLite) để chạy.
- Lý do chọn file cho đồ án: chạy được trên mọi máy chỉ có JDK, dễ kiểm thử, dễ xem dữ liệu mẫu.

## Kiến trúc Repository
`DormService` không tự đọc/ghi file; mọi truy cập đi qua `DataStore` với các danh sách
`users, buildings, rooms, students, contracts, bills` và 2 thao tác `load()` / `save()`.
Muốn đổi cách lưu trữ chỉ cần thay lớp này.

## Sơ đồ bảng tương ứng (khi chuyển sang SQL)
```sql
CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT, full_name TEXT, role TEXT);
CREATE TABLE buildings(id TEXT PRIMARY KEY, name TEXT, floors INT, gender TEXT);
CREATE TABLE rooms(id TEXT PRIMARY KEY, building_id TEXT REFERENCES buildings(id),
                   room_number TEXT, capacity INT, price_per_month BIGINT, gender TEXT);
CREATE TABLE students(id TEXT PRIMARY KEY, full_name TEXT, gender TEXT, class_name TEXT,
                      phone TEXT, hometown TEXT, room_id TEXT REFERENCES rooms(id));
CREATE TABLE contracts(id TEXT PRIMARY KEY, student_id TEXT, room_id TEXT,
                       start_date DATE, end_date DATE, deposit BIGINT, monthly_price BIGINT, status TEXT);
CREATE TABLE utility_bills(id TEXT PRIMARY KEY, room_id TEXT, month TEXT,
  elec_old REAL, elec_new REAL, elec_price BIGINT, water_old REAL, water_new REAL, water_price BIGINT, paid INT);
```

## Các bước chuyển sang JDBC SQLite (thực hiện khi cần)
1. Tải driver `sqlite-jdbc` (org.xerial) và thêm vào classpath — đây là thư viện ngoài duy nhất cần thêm.
2. Tạo class mới `dorm.JdbcDataStore` có cùng các trường danh sách và cùng chữ ký `load()`/`save()` như `DataStore`, bên trong dùng `DriverManager.getConnection("jdbc:sqlite:dorm.db")`, `PreparedStatement` cho mọi truy vấn (chống SQL injection).
3. Trong `load()`: `SELECT` từng bảng, map mỗi `ResultSet` row thành object tương ứng (`User.fromLine` được thay bằng constructor từ các cột).
4. Trong `save()`: thực thi trong 1 transaction (`setAutoCommit(false)` … `commit()`), dùng `INSERT OR REPLACE`.
5. Sửa 1 dòng khởi tạo trong `Main`/`DormApp`/`TestDorm`: `new DataStore(...)` → `new JdbcDataStore(...)`. `DormService` và GUI không đổi.
6. Muốn dùng MySQL: đổi URL thành `jdbc:mysql://localhost:3306/dorm`, dùng driver MySQL Connector/J và tạo database/schema tương ứng ở trên.

## Lưu ý bảo mật
Bản demo lưu mật khẩu dạng rõ trong file để sinh viên dễ đọc dữ liệu mẫu. Khi dùng JDBC/thật, phải băm mật khẩu (BCrypt/Argon2) và không commit file CSDL chứa tài khoản thật lên GitHub.

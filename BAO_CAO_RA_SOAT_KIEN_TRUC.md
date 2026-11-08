# BÁO CÁO RÀ SOÁT KIẾN TRÚC TOÀN DIỆN DỰ ÁN UNICLUB
**Vai trò:** Tech Lead  
**Căn cứ đánh giá:** Tiêu chuẩn quy định tại [uniclub-coder](file:///c:/Workspace/Uniclub_DevOps/.gemini/skills/uniclub-coder/SKILL.md)  
**Phạm vi rà soát:** [uniclub-be](file:///c:/Workspace/Uniclub_DevOps/uniclub-be) & [uniclub-fe](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe)  
**Ngày báo cáo:** 02/11/2026  

---

## 1. Bảng Checklist Đánh Giá Hiện Trạng Toàn Diện

| Phân hệ / Hạng mục | Tiêu chuẩn uniclub-coder | Tình trạng hiện tại | Đánh giá | File / Vị trí vi phạm |
| :--- | :--- | :--- | :---: | :--- |
| **Backend - Security** | Spring Security + JJWT, Role-based Access Control (RBAC) | Tắt toàn bộ bảo mật bằng `.anyRequest().permitAll()`, không có Filter kiểm tra JWT token. Có endpoint `/api/auth/generate-hash` mở công khai cho mọi người dùng. | ❌ **Nghiêm trọng** | [SecurityConfig.java:L27-L29](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/config/SecurityConfig.java#L27-L29)<br>[AuthController.java:L40-L47](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/controller/AuthController.java#L40-L47) |
| **Backend - Phân tầng (Layering)** | Controller chỉ điều hướng, không chứa business logic, xử lý lỗi qua ExceptionHandler tập trung | Controller trực tiếp inject `CloudinaryService`, tự validate file, tự khởi tạo `BCryptPasswordEncoder`, bắt try-catch thủ công trả raw Map. | ❌ **Nghiêm trọng** | [VariantController.java:L88-L125](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/controller/VariantController.java#L88-L125)<br>[AuthController.java:L43-L45](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/controller/AuthController.java#L43-L45) |
| **Backend - Database & ORM** | JPA/Hibernate, không N+1 Query, không hardcode business constants | N+1 Query diện rộng khi lấy danh sách đơn hàng (`enrichOrderResponseWithPaymentMethod`). Hardcode role ID (2 = Buyer) và parse chuỗi DB error message. | ❌ **Nghiêm trọng** | [OrderServiceImpl.java:L53-L60](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/service/impl/OrderServiceImpl.java#L53-L60)<br>[UserServiceImpl.java:L199-L206](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/service/impl/UserServiceImpl.java#L199-L206) |
| **Frontend - Data Fetching** | Gọi API bằng Axios tập trung, có interceptor quản lý token/lỗi | Web app dùng `fetch()` native phân tán khắp các page; `mock-data.js` vẫn được import để thao tác dữ liệu thay vì gọi API. | ❌ **Nghiêm trọng** | [AllOrdersPage.jsx:L6](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/pages/AllOrdersPage.jsx#L6)<br>[LoginPage.jsx:L31](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/pages/LoginPage.jsx#L31)<br>[api.js:L7](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/lib/api.js#L7) |
| **Frontend - Cấu trúc Monorepo** | Tách biệt 2 app trong monorepo pnpm, dùng Tailwind CSS v4 | Khai báo sai dependencies (`vite` nằm trong `dependencies` thay vì `devDependencies`); xung đột version thư viện (`tailwind-merge` v2 vs v3); dùng package thừa/lỗi thời của Tailwind v3. | ⚠️ **Cảnh báo** | [web/package.json:L12-L22](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/package.json#L12-L22)<br>[admin/package.json:L18-L21](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/admin/package.json#L18-L21) |
| **DevOps - Nginx Reverse Proxy** | Nginx Alpine làm reverse proxy, nén Gzip, định tuyến `/api/` | Thiếu `client_max_body_size` (gây lỗi 413 khi upload file > 1MB), không có cấu hình cache cho static assets của Vite, thiếu keepalive upstream. | ⚠️ **Cảnh báo** | [nginx.conf:L6-L45](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/nginx.conf#L6-L45) |
| **DevOps - Docker Containers** | Multi-stage build, tối ưu layer caching | BE Dockerfile chạy quyền root, không config JVM RAM percentage; không dùng Spring Boot Layered Jar; compose hardcode toàn bộ secret vào file repo. | ⚠️ **Cảnh báo** | [uniclub-be/Dockerfile:L20-L36](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/Dockerfile#L20-L36)<br>[docker-compose.yml:L7-L64](file:///c:/Workspace/Uniclub_DevOps/docker-compose.yml#L7-L64) |

---

## 2. Rà Soát Dependencies & File Cấu Hình

### 2.1. Backend ([pom.xml](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/pom.xml))
* **Dynamic Version `RELEASE` không an toàn:**
  - Vị trí: [pom.xml:L102-L106](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/pom.xml#L102-L106) (`org.jetbrains:annotations:RELEASE`).
  - *Vấn đề:* Sử dụng từ khóa `RELEASE` trong Maven đã bị deprecated, gây ra non-reproducible build (mỗi lần build có thể kéo một bản khác nhau, tiềm ẩn rủi ro bảo mật). Cần khóa version cụ thể.
* **Thư viện Cloudinary lỗi thời:**
  - Vị trí: [pom.xml:L60-L63](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/pom.xml#L60-L63) (`com.cloudinary:cloudinary-http44:1.36.0`).
  - *Vấn đề:* Artifact `cloudinary-http44` gắn liền với Apache HttpClient 4.4 cũ. Nên nâng cấp lên phiên bản đồng bộ với Spring Boot 3.x (dùng `cloudinary-core` hoặc `cloudinary-http5`).
* **Hardcode version Testcontainers:**
  - Vị trí: [pom.xml:L86-L96](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/pom.xml#L86-L96) (`testcontainers:junit-jupiter:1.20.1`).
  - *Vấn đề:* Spring Boot 3.5.x đã quản lý phiên bản Testcontainers thông qua dependency management BOM, việc hardcode version dễ gây xung đột transitives.

### 2.2. Frontend ([web/package.json](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/package.json) & [admin/package.json](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/admin/package.json))
* **Khai báo sai nhóm phụ thuộc (Dev vs Runtime):**
  - Trong [web/package.json:L12](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/package.json#L12) & [web/package.json:L21](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/package.json#L21): `@vitejs/plugin-react` và `vite` đang bị đặt trong `dependencies` thay vì `devDependencies`.
* **Dùng tag `"latest"` nguy hiểm:**
  - Trong [admin/package.json:L18-L19](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/admin/package.json#L18-L19): `"react-router-dom": "latest"`, `"recharts": "latest"`.
  - *Hậu quả:* Build CI/CD không có tính tất định (non-deterministic). Bất kỳ bản cập nhật breaking nào từ tác giả thư viện sẽ làm sập quy trình build production.
* **Dependencies thừa do chuyển dịch sang Tailwind CSS v4:**
  - `autoprefixer`: Tailwind v4 tích hợp sẵn Lightning CSS, không cần `autoprefixer` trong `devDependencies`.
  - `"tailwindcss-animate": "^1.0.7"` trong [admin/package.json:L21](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/admin/package.json#L21): Đây là plugin dành riêng cho Tailwind v3, không tương thích với cơ chế `@theme` của Tailwind v4.
* **Xung đột Major Version giữa các app trong monorepo:**
  - `tailwind-merge`: Admin dùng `^2.5.5`, Web dùng `^3.3.1`.
  - `lucide-react`: Admin dùng `^0.454.0`, Web dùng `^0.545.0`.

---

## 3. Các Điểm Vi Phạm Kiến Trúc

### 3.1. Phía Frontend
1. **Anti-pattern "Client-side Data Warehouse":**
   - **Vị trí:** [Dashboard.jsx:L38-L65](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/admin/src/pages/Dashboard.jsx#L38-L65)
   - **Thực trạng:** Để hiển thị thống kê, component gọi `api.list("variants")`, `api.list("orders")`, `api.list("users")` tải **toàn bộ dữ liệu của hệ thống** vào RAM trình duyệt, sau đó dùng `filter`, `reduce` trong JS để tính doanh thu theo tháng, sản phẩm bán chạy, tồn kho thấp.
   - **Hậu quả:** Khi hệ thống có 20.000 đơn hàng, việc tải hàng chục MB JSON sẽ gây nghẽn mạng và làm đơ tab trình duyệt của Admin. Phải chuyển logic này thành một endpoint tổng hợp tại Backend (`GET /api/dashboard/stats`).
2. **Giả lập Database trên React Component thay vì gọi API:**
   - **Vị trí:** [AllOrdersPage.jsx:L6-L26](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/pages/AllOrdersPage.jsx#L6-L26)
   - **Thực trạng:** Trang danh sách đơn hàng không gọi Backend API mà import trực tiếp `orders`, `order_variants`, `payment_methods` từ [mock-data.js](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/lib/mock-data.js) và tự thực hiện relational join (`filter`, `find`) trong `useMemo`.
3. **Tải toàn bộ dữ liệu biến thể của cửa hàng cho một trang chi tiết:**
   - **Vị trí:** [ProductDetailPage.jsx:L51-L56](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/pages/ProductDetailPage.jsx#L51-L56)
   - **Thực trạng:** Khi xem 1 sản phẩm, component gọi `GET /api/variants` (lấy tất cả biến thể của mọi sản phẩm trong DB) rồi mới `filter(v => v.productId === id)`.

### 3.2. Phía Backend
1. **Bảo mật bị vô hiệu hóa hoàn toàn:**
   - **Vị trí:** [SecurityConfig.java:L27-L29](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/config/SecurityConfig.java#L27-L29)
   - **Thực trạng:** Cấu hình `.anyRequest().permitAll()` cho phép bất kỳ ai gửi request đến mọi API quản trị (xóa user, duyệt nhập hàng GRN, thay đổi trạng thái đơn) mà không cần Token hay Role.
2. **Rò rỉ Business Logic vào Controller:**
   - **Vị trí:** [VariantController.java:L88-L125](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/controller/VariantController.java#L88-L125)
   - **Thực trạng:** Controller tự inject `CloudinaryService`, tự validate MIME type, kiểm tra dung lượng file, upload lên Cloudinary và cập nhật URL. Toàn bộ chuỗi thao tác này phải nằm trong Service layer có quản lý transaction.
3. **Vi phạm chuẩn Interface - Implementation:**
   - [VNPayService.java](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/service/VNPayService.java) và [CloudinaryService.java](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/service/CloudinaryService.java) được khai báo trực tiếp dạng concrete class trong `service/` thay vì tuân theo chuẩn `interface` và `service/impl/` như các service khác.

---

## 4. Điểm Nghẽn Hiệu Năng Trong Docker & Nginx

### 4.1. Cấu hình [nginx.conf](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/nginx.conf)
* **Giới hạn Payload mặc định (1MB) chặn upload ảnh:**
  Nginx mặc định `client_max_body_size 1m;`. Trong khi đó, Backend cho phép file tới 5MB ([UploadController.java:L35](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/controller/UploadController.java#L35)). Mọi file upload từ 1MB - 5MB sẽ bị Nginx trả ngay `413 Request Entity Too Large`.
* **Thiếu Cache Headers cho Static Assets:**
  Vite sinh ra các bundle có hash (VD: `assets/index-D7x8a.js`), nhưng Nginx không cấu hình `expires` hay `Cache-Control: public, max-age=31536000, immutable`. Trình duyệt phải liên tục gửi request 304 re-validate, làm chậm tốc độ tải trang.
* **Thiếu Upstream Connection Keepalive:**
  Khối `location /api/` dùng `proxy_pass http://backend:8080/api/;` trực tiếp mà không qua `upstream` block có `keepalive 32;`. Dưới tải cao, việc liên tục mở và đóng TCP socket giữa Nginx và Spring Boot gây cạn port (ephemeral port exhaustion) và tăng latency.

### 4.2. Cấu hình [uniclub-be/Dockerfile](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/Dockerfile) & [docker-compose.yml](file:///c:/Workspace/Uniclub_DevOps/docker-compose.yml)
* **Thiếu Spring Boot Layered Jar:**
  Dockerfile copy nguyên khối `app.jar` ([uniclub-be/Dockerfile:L26](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/Dockerfile#L26)). Mỗi lần thay đổi 1 dòng code, Docker phải build lại layer ~80MB thay vì chỉ 1-2MB application code.
* **JVM không có cgroup limits:**
  Lệnh `ENTRYPOINT ["java", "-jar", "app.jar"]` không chỉ định `-XX:+UseContainerSupport` và `-XX:MaxRAMPercentage=75.0`. Trong container Docker, JVM có thể nhận diện sai bộ nhớ của host và bị kernel OOM-killer hạ gục.
* **Hardcode Secret trong Git:**
  [docker-compose.yml:L7-L64](file:///c:/Workspace/Uniclub_DevOps/docker-compose.yml#L7-L64) lưu trực tiếp MySQL password, JWT secret, VNPay Hash, Cloudinary API Key. Cần tách hoàn toàn ra `.env`.

---

## 5. Top 3 File Có "Code Smell" Nặng Nhất Cần Refactor Ngay

### 🥇 Top 1: [uniclub-fe/admin/src/pages/Dashboard.jsx](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/admin/src/pages/Dashboard.jsx)
* **Lý do:** Vi phạm kiến trúc Client-Server cơ bản. Kéo toàn bộ bảng DB về phía browser để gom nhóm và tính toán số liệu kế toán/doanh thu.
* **Đề xuất Refactor:** Loại bỏ logic tính toán client-side; chuyển sang gọi endpoint tổng hợp từ backend:

```diff
--- a/uniclub-fe/admin/src/pages/Dashboard.jsx
+++ b/uniclub-fe/admin/src/pages/Dashboard.jsx
@@ -37,30 +37,12 @@ export default function Dashboard() {
-        // Fetch data from API
-        const [variants, orders, users] = await Promise.all([
-          api.list("variants"),
-          api.list("orders"),
-          api.list("users")
-        ])
-
-        // Calculate basic stats
-        const lowStockCount = variants.filter((v) => v.quantity < 5).length
-        const totalRevenue = orders.filter((o) => o.status === "DELIVERED").reduce((sum, o) => sum + (o.total || 0), 0)
-
-        setStats({
-          revenue: totalRevenue,
-          orders: orders.length,
-          activeUsers: users.length,
-          lowStock: lowStockCount,
-        })
+        // Fetch pre-aggregated statistics from dedicated backend endpoint
+        const dashboardStats = await api.getDashboardStats()
+        setStats(dashboardStats.summary)
+        setRevenueData(dashboardStats.monthlyRevenue)
+        setOrderStatusData(dashboardStats.orderStatusDistribution)
+        setTopProducts(dashboardStats.topSellingProducts)
```

---

### 🥈 Top 2: [uniclub-be/src/main/java/com/uniclub/service/impl/OrderServiceImpl.java](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/service/impl/OrderServiceImpl.java)
* **Lý do:** Gây thảm họa **N+1 Query** trong [OrderServiceImpl.java:L53-L60](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/service/impl/OrderServiceImpl.java#L53-L60). Với 1000 đơn hàng, hàm `getAllOrders()` sẽ thực thi 1001 câu truy vấn SQL (`paymentRepository.findByOrderId`). Cùng với đó là vòng lặp cập nhật DB đơn lẻ trong `cancelOrder` ([OrderServiceImpl.java:L250-L254](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/service/impl/OrderServiceImpl.java#L250-L254)) và hardcode IP `127.0.0.1`.
* **Đề xuất Refactor:** Thay thế bằng `@EntityGraph` hoặc JPQL JOIN FETCH, và gom cập nhật số lượng biến thể thành batch:

```diff
--- a/uniclub-be/src/main/java/com/uniclub/service/impl/OrderServiceImpl.java
+++ b/uniclub-be/src/main/java/com/uniclub/service/impl/OrderServiceImpl.java
@@ -53,8 +53,8 @@ public class OrderServiceImpl implements OrderService {
     @Override
     public List<OrderResponse> getAllOrders() {
-        return orderRepository.findAll().stream()
+        // Sử dụng query đã JOIN FETCH cả Order, OrderVariants và Payment
+        return orderRepository.findAllWithPaymentsAndVariants().stream()
                 .map(order -> {
                     OrderResponse response = OrderResponse.fromEntity(order);
-                    enrichOrderResponseWithPaymentMethod(response, order);
                     return response;
                 })
```

---

### 🥉 Top 3: [uniclub-fe/web/src/pages/AllOrdersPage.jsx](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/pages/AllOrdersPage.jsx)
* **Lý do:** Tách rời hoàn toàn khỏi Backend. Sử dụng [mock-data.js](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/lib/mock-data.js) và tự join bảng in-memory trong component ([AllOrdersPage.jsx:L14-L26](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/pages/AllOrdersPage.jsx#L14-L26)). Khi người dùng đặt hàng mới từ backend, trang này không bao giờ phản ánh dữ liệu thực tế.
* **Đề xuất Refactor:** Kết nối component với Axios API Service:

```diff
--- a/uniclub-fe/web/src/pages/AllOrdersPage.jsx
+++ b/uniclub-fe/web/src/pages/AllOrdersPage.jsx
@@ -1,11 +1,10 @@
-import React, { useState, useMemo } from "react"
+import React, { useState, useEffect } from "react"
 import { Link } from "react-router-dom"
 import { PageLayout } from "../components/PageLayout"
 import { Button } from "@/components/ui/button"
 import { Price } from "@/components/Price"
-import { orders, order_variants, payment_methods, billing_details } from "@/lib/mock-data"
+import { getOrdersByCurrentUser } from "@/lib/api"
 import { Package, ChevronRight, Filter, Search, Calendar, Download } from "lucide-react"
 
 export default function AllOrdersPage() {
@@ -13,15 +12,14 @@ export default function AllOrdersPage() {
   const [statusFilter, setStatusFilter] = useState("ALL")
   const [searchQuery, setSearchQuery] = useState("")
   const [sortBy, setSortBy] = useState("date-desc")
+  const [orders, setOrders] = useState([])
+  const [loading, setLoading] = useState(true)
 
-  const allOrders = useMemo(() => {
-    return orders.map((order) => {
-      const items = order_variants.filter((ov) => ov.id_order === order.id)
-      const payment = payment_methods.find((pm) => pm.id === order.id_payment)
-      const billing = billing_details.find((b) => b.id_order === order.id)
-      return {
-        ...order,
-        itemCount: items.reduce((sum, item) => sum + item.quantity, 0),
-        payment,
-        billing,
-      }
-    })
-  }, [])
+  useEffect(() => {
+    getOrdersByCurrentUser()
+      .then(setOrders)
+      .catch(console.error)
+      .finally(() => setLoading(false))
+  }, [])
```

---

## 6. Kế Hoạch Hành Động Khuyến Nghị (Action Plan)

1. **Sprint 1 (Khẩn cấp - Hotfix):**
   - Đóng các lỗ hổng bảo mật: Kích hoạt `JwtAuthenticationFilter` trong [SecurityConfig.java](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/config/SecurityConfig.java), xóa endpoint `/api/auth/generate-hash`.
   - Sửa cấu hình [nginx.conf](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/nginx.conf): Bổ sung `client_max_body_size 10M;` và cấu hình `Cache-Control` cho assets.
   - Chuyển toàn bộ secret trong [docker-compose.yml](file:///c:/Workspace/Uniclub_DevOps/docker-compose.yml) sang file `.env`.
2. **Sprint 2 (Khắc phục hiệu năng & Kiến trúc Frontend):**
   - Refactor [Dashboard.jsx](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/admin/src/pages/Dashboard.jsx): Viết API thống kê phía backend, xóa logic tải toàn bộ DB về frontend.
   - Xóa bỏ hoàn toàn `mock-data.js` trong `uniclub-fe/web`, chuyển [AllOrdersPage.jsx](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/pages/AllOrdersPage.jsx) và [ProfilePage.jsx](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/pages/ProfilePage.jsx) sang dùng Axios API.
   - Khắc phục [ProductDetailPage.jsx](file:///c:/Workspace/Uniclub_DevOps/uniclub-fe/web/src/pages/ProductDetailPage.jsx) để chỉ gọi biến thể theo `productId`.
3. **Sprint 3 (Tối ưu Backend & DevOps):**
   - Tối ưu N+1 Query trong [OrderServiceImpl.java](file:///c:/Workspace/Uniclub_DevOps/uniclub-be/src/main/java/com/uniclub/service/impl/OrderServiceImpl.java) bằng `@EntityGraph`.
   - Chuẩn hóa Dockerfile Backend sang Spring Boot Layered Jar và chỉ định tham số JVM Container RAM.

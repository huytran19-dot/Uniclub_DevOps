# ==========================================
# Stage 1: Build Workspace (Admin & Web)
# ==========================================
FROM node:20-alpine AS builder

WORKDIR /app

# Bật pnpm phiên bản xác định
RUN corepack enable && corepack prepare pnpm@9.0.0 --activate

# BƯỚC TỐI ƯU CACHE: Chỉ copy các file quản lý thư viện vào trước
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./
COPY admin/package.json ./admin/
COPY web/package.json ./web/

# Cài đặt toàn bộ thư viện cho cả workspace (sẽ dùng lại cache nếu package.json không đổi)
RUN pnpm install --frozen-lockfile

# Copy toàn bộ mã nguồn vào sau khi đã cài thư viện xong
COPY . .

# Cấu hình API base URL dùng chung cho Admin & Web khi build production qua Nginx Proxy
ARG VITE_API_URL=/api
ENV VITE_API_URL=$VITE_API_URL

# Build Admin và Web
RUN pnpm --filter ./admin run build
RUN pnpm --filter ./web run build

# ==========================================
# Stage 2: Runtime with Nginx
# ==========================================
FROM nginx:alpine

# Xóa cấu hình mặc định
RUN rm -rf /usr/share/nginx/html/*

# Copy thư mục build từ stage builder
COPY --from=builder /app/admin/dist /usr/share/nginx/html/admin
COPY --from=builder /app/web/dist /usr/share/nginx/html/web

# Copy cấu hình Nginx của bạn
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Mở port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://127.0.0.1/ || exit 1

# Khởi động nginx
CMD ["nginx", "-g", "daemon off;"]
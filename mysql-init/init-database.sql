CREATE DATABASE IF NOT EXISTS `uniclub`;
USE `uniclub`;

-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Máy chủ: mysql:3306
-- Thời gian đã tạo: Th10 24, 2025 lúc 01:18 PM
-- Phiên bản máy phục vụ: 8.0.44
-- Phiên bản PHP: 8.3.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `uniclub`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `billing_detail`
--

CREATE TABLE `billing_detail` (
  `id` int NOT NULL,
  `id_order` int DEFAULT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `province` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `district` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ward` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `brand`
--

CREATE TABLE `brand` (
  `id` int NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `brand`
--

INSERT INTO `brand` (`id`, `name`, `status`, `created_at`, `updated_at`) VALUES
(1, 'Nike', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(2, 'Adidas', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(3, 'Uniqlo', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(4, 'Zara', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(5, 'H&M', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(6, 'Bernini', 1, '2025-10-23 21:08:03', '2025-10-23 21:08:03');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `cart`
--

CREATE TABLE `cart` (
  `id` int NOT NULL,
  `id_user` int NOT NULL,
  `total_price` int DEFAULT '0',
  `shipping_fee` int DEFAULT '0',
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `cart_item`
--

CREATE TABLE `cart_item` (
  `id` int NOT NULL,
  `id_cart` int NOT NULL,
  `id_sku` int NOT NULL,
  `quantity` int DEFAULT '1',
  `unit_price` int NOT NULL,
  `subtotal` int GENERATED ALWAYS AS ((`quantity` * `unit_price`)) STORED,
  `added_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `category`
--

CREATE TABLE `category` (
  `id` int NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `category`
--

INSERT INTO `category` (`id`, `name`, `status`, `created_at`, `updated_at`) VALUES
(1, 'Áo thun', 1, '2025-10-23 13:03:06', '2025-10-23 13:03:06'),
(2, 'Quần jean', 1, '2025-10-23 13:03:06', '2025-10-23 13:03:06'),
(3, 'Áo sơ mi', 1, '2025-10-23 13:03:06', '2025-10-23 13:03:06'),
(4, 'Váy', 1, '2025-10-23 13:03:06', '2025-10-23 13:03:06'),
(5, 'Áo khoác', 1, '2025-10-23 13:03:06', '2025-10-23 13:03:06'),
(6, 'Áo Polo', 1, '2025-10-23 21:05:06', '2025-10-23 21:05:06');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `color`
--

CREATE TABLE `color` (
  `id` int NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `hex_code` varchar(7) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `color`
--

INSERT INTO `color` (`id`, `name`, `hex_code`, `status`, `created_at`, `updated_at`) VALUES
(1, 'Đỏ', '#FF0000', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(2, 'Xanh dương', '#0000FF', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(3, 'Xanh lá', '#00FF00', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(4, 'Vàng', '#FFFF00', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(5, 'Đen', '#000000', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(6, 'Trắng', '#FFFFFF', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(10, 'Xám', '#ababab', 1, '2025-10-23 20:45:27', '2025-10-23 20:45:41'),
(11, 'Tím', '#ddc1f0', 1, '2025-10-24 20:00:15', '2025-10-24 20:00:15');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `comment`
--

CREATE TABLE `comment` (
  `id` int NOT NULL,
  `id_user` int NOT NULL,
  `parent_id` int DEFAULT NULL,
  `content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `grn_detail`
--

CREATE TABLE `grn_detail` (
  `id` int NOT NULL,
  `id_grn` int NOT NULL,
  `id_sku` int NOT NULL,
  `quantity` int NOT NULL,
  `unit_cost` int NOT NULL,
  `subtotal` int GENERATED ALWAYS AS ((`quantity` * `unit_cost`)) STORED
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `grn_header`
--

CREATE TABLE `grn_header` (
  `id` int NOT NULL,
  `id_supplier` int NOT NULL,
  `total_cost` int DEFAULT '0',
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `received_date` date DEFAULT (curdate()),
  `status` enum('PENDING','COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `payment`
--

CREATE TABLE `payment` (
  `id` int NOT NULL,
  `id_order` int DEFAULT NULL,
  `payment_method` enum('COD','VNPay') COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_no` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vnpay_bank_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vnpay_response_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `amount` int DEFAULT NULL,
  `payment_status` enum('FAILED','PENDING','SUCCESS','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `paid_at` timestamp NULL DEFAULT NULL,
  `payment_expires_at` datetime NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `product`
--

CREATE TABLE `product` (
  `id` int NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `information` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_brand` int DEFAULT NULL,
  `id_category` int DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `product`
--

INSERT INTO `product` (`id`, `name`, `description`, `information`, `id_brand`, `id_category`, `status`, `created_at`, `updated_at`) VALUES
(1, 'Áo Polo cọc tay', '', '', 6, 6, 1, '2025-10-23 21:34:48', '2025-10-23 21:34:48'),
(2, 'Áo sơ mi cọc tay', '', '', 1, 3, 1, '2025-10-24 19:43:42', '2025-10-24 19:43:42'),
(3, 'Áo sơ mi dài tay', '', '', 2, 3, 1, '2025-10-24 20:01:16', '2025-10-24 20:01:16');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `review`
--

CREATE TABLE `review` (
  `id` int NOT NULL,
  `id_product` int DEFAULT NULL,
  `id_user` int DEFAULT NULL,
  `star` double NOT NULL,
  `content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `images` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `role`
--

CREATE TABLE `role` (
  `id` int NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `role`
--

INSERT INTO `role` (`id`, `name`, `description`, `status`, `created_at`, `updated_at`) VALUES
(1, 'SysAdmin', 'Quản trị viên - Có toàn quyền điều khiển hệ thống', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(2, 'Buyer', 'Người mua - Có thể xem và mua sản phẩm', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `size`
--

CREATE TABLE `size` (
  `id` int NOT NULL,
  `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `size`
--

INSERT INTO `size` (`id`, `name`, `status`, `created_at`, `updated_at`) VALUES
(1, 'XS', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(2, 'S', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(3, 'M', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(4, 'L', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(5, 'XL', 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(6, 'XXL', 1, '2025-10-23 13:03:05', '2025-10-23 20:46:03');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `supplier`
--

CREATE TABLE `supplier` (
  `id` int NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_person` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `supplier`
--

INSERT INTO `supplier` (`id`, `name`, `contact_person`, `phone`, `email`, `address`, `status`, `created_at`, `updated_at`) VALUES
(1, 'Nhà cung cấp A', 'Nguyễn Văn A', '0123456789', 'supplierA@email.com', '123 Đường ABC, Quận 1, TP.HCM', 1, '2025-10-23 13:03:06', '2025-10-24 15:12:47'),
(2, 'Nhà cung cấp B', 'Trần Thị B', '0987654321', 'supplierB@email.com', '456 Đường XYZ, Quận 2, TP.HCM', 1, '2025-10-23 13:03:06', '2025-10-23 13:03:06'),
(3, 'Nhà cung cấp C', 'Đỗ Anh C', '0906666233', 'atrbod@gmail.com', '22 An Dương Vương P10 Q5 TP.HCM', 1, '2025-10-24 15:58:13', '2025-10-24 15:58:13');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `user`
--

CREATE TABLE `user` (
  `id` int NOT NULL,
  `email` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `province_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `province_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `district_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `district_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ward_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ward_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_role` int DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `user`
--

INSERT INTO `user` (`id`, `email`, `password`, `full_name`, `id_role`, `status`, `created_at`, `updated_at`) VALUES
(1, 'admin@uniclub.com', '$2a$10$uGCf6BtBmivlVSWjgvATS.NyYxA9/m0thuhErzAz7wt6ECezQphkS', 'System Administrator', 1, 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05'),
(2, 'buyer@uniclub.com', '$2a$10$uGCf6BtBmivlVSWjgvATS.NyYxA9/m0thuhErzAz7wt6ECezQphkS', 'Buyer User', 2, 1, '2025-10-23 13:03:05', '2025-10-23 13:03:05');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `variant`
--

CREATE TABLE `variant` (
  `sku` int NOT NULL,
  `id_product` int NOT NULL,
  `id_size` int DEFAULT NULL,
  `id_color` int DEFAULT NULL,
  `images` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantity` int DEFAULT '0',
  `price` int DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `variant`
--

INSERT INTO `variant` (`sku`, `id_product`, `id_size`, `id_color`, `images`, `quantity`, `price`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, 5, 5, 'https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/jkc53v3xoluixh6lfnr2.jpg', 0, 108000, 1, '2025-10-23 22:52:26', '2025-10-24 10:45:08'),
(2, 1, 4, 1, 'https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/rbgfmd6b0bkffvjglp2w.jpg', 0, 108000, 1, '2025-10-24 19:39:06', '2025-10-24 19:39:06'),
(3, 1, 5, 6, 'https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/bzzbmcscdmioh1plz8se.jpg', 0, 108000, 1, '2025-10-24 19:42:33', '2025-10-24 19:42:33'),
(4, 2, 5, 6, 'https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/pqfdnkwfwqvwvqglgxpr.jpg', 0, 135000, 1, '2025-10-24 19:45:10', '2025-10-24 19:45:41'),
(5, 2, 4, 10, 'https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/hsszyoner92irei0z60r.jpg', 0, 127000, 1, '2025-10-24 19:46:24', '2025-10-24 19:46:24'),
(6, 2, 5, 11, 'https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/ijegkhf1kfzzzwhw2cfg.jpg', 0, 140000, 1, '2025-10-24 20:00:40', '2025-10-24 20:00:40'),
(7, 3, 5, 6, 'https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/kevpfl2cpg8as0qzjoc4.jpg', 0, 150000, 1, '2025-10-24 20:02:09', '2025-10-24 20:02:09'),
(8, 3, 5, 5, 'https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/shxd4gpykyx9b2gsmhth.jpg', 0, 140000, 1, '2025-10-24 20:02:46', '2025-10-24 20:02:46'),
(9, 3, 4, 2, 'https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev/uniclub/variants/ki43fdaaxkae7nvj8cce.jpg', 0, 160000, 1, '2025-10-24 20:03:21', '2025-10-24 20:03:21');


--
-- Cấu trúc bảng cho bảng `orders`
--

CREATE TABLE `orders` (
  `id` int NOT NULL,
  `total` int DEFAULT NULL,
  `shipping_fee` int NOT NULL DEFAULT 0,
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `recipient_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `recipient_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `shipping_address` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_user` int DEFAULT NULL,
  `status` enum('PENDING','CONFIRMED','SHIPPING','DELIVERED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `payment_expires_at` datetime NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `order_variant`
--

CREATE TABLE `order_variant` (
  `id_order` int NOT NULL,
  `id_sku` int NOT NULL,
  `quantity` int DEFAULT NULL,
  `price` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

INSERT INTO `orders` (`id`, `total`, `note`, `id_user`, `status`, `created_at`, `updated_at`) VALUES
(1, 108000, 'Note 1', 1, 'PENDING', '2025-10-23 13:03:05', '2025-10-23 13:03:05');

INSERT INTO `order_variant` (`id_order`, `id_sku`, `quantity`, `price`) VALUES
(1, 1, 1, 108000);
--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `billing_detail`
--
ALTER TABLE `billing_detail`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_billing_order` (`id_order`);

--
-- Chỉ mục cho bảng `brand`
--
ALTER TABLE `brand`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_cart_user` (`id_user`),
  ADD KEY `FK_cart_user` (`id_user`);

--
-- Chỉ mục cho bảng `cart_item`
--
ALTER TABLE `cart_item`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_cartitem_cart` (`id_cart`),
  ADD KEY `FK_cartitem_variant` (`id_sku`);

--
-- Chỉ mục cho bảng `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `color`
--
ALTER TABLE `color`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `comment`
--
ALTER TABLE `comment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_comment_user` (`id_user`),
  ADD KEY `FK_comment_parent` (`parent_id`);

--
-- Chỉ mục cho bảng `grn_detail`
--
ALTER TABLE `grn_detail`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_grn_detail_grn` (`id_grn`),
  ADD KEY `FK_grn_detail_variant` (`id_sku`);

--
-- Chỉ mục cho bảng `grn_header`
--
ALTER TABLE `grn_header`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_grn_supplier` (`id_supplier`);

--
-- Chỉ mục cho bảng `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_order_user` (`id_user`);

--
-- Chỉ mục cho bảng `order_variant`
--
ALTER TABLE `order_variant`
  ADD PRIMARY KEY (`id_order`,`id_sku`),
  ADD KEY `FK_ov_variant` (`id_sku`);

--
-- Chỉ mục cho bảng `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_payment_order` (`id_order`);

--
-- Chỉ mục cho bảng `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_product_brand` (`id_brand`),
  ADD KEY `FK_product_category` (`id_category`);

--
-- Chỉ mục cho bảng `review`
--
ALTER TABLE `review`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_review_product` (`id_product`),
  ADD KEY `FK_review_user` (`id_user`);

--
-- Chỉ mục cho bảng `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Chỉ mục cho bảng `size`
--
ALTER TABLE `size`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `FK_user_role` (`id_role`);

--
-- Chỉ mục cho bảng `variant`
--
ALTER TABLE `variant`
  ADD PRIMARY KEY (`sku`),
  ADD KEY `FK_variant_product` (`id_product`),
  ADD KEY `FK_variant_size` (`id_size`),
  ADD KEY `FK_variant_color` (`id_color`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `billing_detail`
--
ALTER TABLE `billing_detail`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `brand`
--
ALTER TABLE `brand`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `cart`
--
ALTER TABLE `cart`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `cart_item`
--
ALTER TABLE `cart_item`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `category`
--
ALTER TABLE `category`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `color`
--
ALTER TABLE `color`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT cho bảng `comment`
--
ALTER TABLE `comment`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `grn_detail`
--
ALTER TABLE `grn_detail`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `grn_header`
--
ALTER TABLE `grn_header`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `payment`
--
ALTER TABLE `payment`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `product`
--
ALTER TABLE `product`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `review`
--
ALTER TABLE `review`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `role`
--
ALTER TABLE `role`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `size`
--
ALTER TABLE `size`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `supplier`
--
ALTER TABLE `supplier`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `user`
--
ALTER TABLE `user`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `variant`
--
ALTER TABLE `variant`
  MODIFY `sku` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Ràng buộc đối với các bảng kết xuất
--

--
-- Ràng buộc cho bảng `billing_detail`
--
ALTER TABLE `billing_detail`
  ADD CONSTRAINT `FK_billing_order` FOREIGN KEY (`id_order`) REFERENCES `orders` (`id`);

--
-- Ràng buộc cho bảng `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `FK_cart_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`);

--
-- Ràng buộc cho bảng `cart_item`
--
ALTER TABLE `cart_item`
  ADD CONSTRAINT `FK_cartitem_cart` FOREIGN KEY (`id_cart`) REFERENCES `cart` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_cartitem_variant` FOREIGN KEY (`id_sku`) REFERENCES `variant` (`sku`);

--
-- Ràng buộc cho bảng `comment`
--
ALTER TABLE `comment`
  ADD CONSTRAINT `FK_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`),
  ADD CONSTRAINT `FK_comment_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`);

--
-- Ràng buộc cho bảng `grn_detail`
--
ALTER TABLE `grn_detail`
  ADD CONSTRAINT `FK_grn_detail_grn` FOREIGN KEY (`id_grn`) REFERENCES `grn_header` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_grn_detail_variant` FOREIGN KEY (`id_sku`) REFERENCES `variant` (`sku`);

--
-- Ràng buộc cho bảng `grn_header`
--
ALTER TABLE `grn_header`
  ADD CONSTRAINT `FK_grn_supplier` FOREIGN KEY (`id_supplier`) REFERENCES `supplier` (`id`);

--
-- Ràng buộc cho bảng `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `FK_order_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`);

--
-- Ràng buộc cho bảng `order_variant`
--
ALTER TABLE `order_variant`
  ADD CONSTRAINT `FK_ov_order` FOREIGN KEY (`id_order`) REFERENCES `orders` (`id`),
  ADD CONSTRAINT `FK_ov_variant` FOREIGN KEY (`id_sku`) REFERENCES `variant` (`sku`);

--
-- Ràng buộc cho bảng `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `FK_payment_order` FOREIGN KEY (`id_order`) REFERENCES `orders` (`id`);

--
-- Ràng buộc cho bảng `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `FK_product_brand` FOREIGN KEY (`id_brand`) REFERENCES `brand` (`id`),
  ADD CONSTRAINT `FK_product_category` FOREIGN KEY (`id_category`) REFERENCES `category` (`id`);

--
-- Ràng buộc cho bảng `review`
--
ALTER TABLE `review`
  ADD CONSTRAINT `FK_review_product` FOREIGN KEY (`id_product`) REFERENCES `product` (`id`),
  ADD CONSTRAINT `FK_review_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`);

--
-- Ràng buộc cho bảng `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `FK_user_role` FOREIGN KEY (`id_role`) REFERENCES `role` (`id`);

--
-- Ràng buộc cho bảng `variant`
--
ALTER TABLE `variant`
  ADD CONSTRAINT `FK_variant_color` FOREIGN KEY (`id_color`) REFERENCES `color` (`id`),
  ADD CONSTRAINT `FK_variant_product` FOREIGN KEY (`id_product`) REFERENCES `product` (`id`),
  ADD CONSTRAINT `FK_variant_size` FOREIGN KEY (`id_size`) REFERENCES `size` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

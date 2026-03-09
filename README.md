# 🐚 蛤蠣訂單系統後端 (Clam Order Backend)

一個基於 Clean Architecture 設計的 Spring Boot 後端系統，用於管理蛤蠣訂單、計算價格、处理庫存。

## 📋 目錄

- [專案概述](#專案概述)
- [技術棧](#技術棧)
- [專案結構](#專案結構)
- [API 文件](#api-文件)
- [業務規則](#業務規則)
- [開始使用](#開始使用)
- [開發指南](#開發指南)
- [部署](#部署)

---

## 專案概述

本系統是為「海的女兒」蛤蠣專賣店設計的訂單管理系統，主要功能包括：

- 📦 商品管理（支援限量商品）
- 🧮 智慧價格計算（運費、優惠）
- 📝 訂單管理與狀態追蹤
- 📊 訂單匯出（CSV）
- 🔒 庫存管理（避免超賣）

---

## 技術棧

| 類別 | 技術 |
|------|------|
| 框架 | Spring Boot 3.3.0 |
| 語言 | Java 17 |
| 資料庫 | H2 (開發) / PostgreSQL (生產) |
| ORM | Spring Data JPA |
| 安全 | Spring Security |
| API 文件 | SpringDoc OpenAPI |
| 建置 | Gradle |
| 測試 | JUnit 5 |

---

## 專案結構

採用 **Clean Architecture**（清晰架構）設計，將系統分為四個同心圓層：

```
src/main/java/com/project/clamorderbackend/
│
├── domain/                    # 🔴 核心層（最內層）
│   ├── # 實體
 entity/               │   │   ├── Product.java       # 商品實體
│   │   ├── Order.java         # 訂單實體
│   │   └── OrderItem.java     # 訂單項目實體
│   │
│   ├── valueobject/           # 值物件
│   │   ├── PriceCalculation.java    # 價格計算結果
│   │   ├── DeliveryZone.java        # 配送區域
│   │   └── DiscountPolicy.java      # 折扣政策
│   │
│   ├── service/              # 領域服務
│   │   ├── OrderCalculationService.java  # 訂單計算服務
│   │   └── ProductService.java          # 商品服務
│   │
│   └── repository/           # 倉庫介面
│       ├── ProductRepository.java
│       └── OrderRepository.java
│
├── application/              # 🟡 應用層
│   ├── dto/                 # 資料傳輸物件
│   │   ├── ProductResponse.java
│   │   ├── OrderCalculateRequest.java
│   │   ├── OrderCalculateResponse.java
│   │   ├── OrderSubmitRequest.java
│   │   ├── OrderSubmitResponse.java
│   │   └── OrderExportResponse.java
│   │
│   ├── usecase/             # 使用案例
│   │   ├── ProductUseCase.java
│   │   └── OrderUseCase.java
│   │
│   └── mapper/              # DTO 映射器
│       └── OrderMapper.java
│
├── infrastructure/          # 🟢 基礎設施層
│   ├── config/              # 配置
│   │   ├── JpaConfig.java
│   │   ├── DataInitializer.java    # 初始資料
│   │   └── SecurityConfig.java
│   └── security/
│
└── presentation/            # 🔵 表現層（最外層）
    ├── controller/          # REST 控制器
    │   ├── ProductController.java
    │   ├── OrderController.java
    │   └── AdminController.java
    │
    └── advice/             # 全域異常處理
        └── GlobalExceptionHandler.java
```

### 各層職責

| 層 | 職責 |
|----|------|
| **Domain** | 核心業務邏輯，與框架無關 |
| **Application** | 協調領域物件，實現用例 |
| **Infrastructure** | 技術實現（資料庫、安全） |
| **Presentation** | 處理 HTTP 請求/響應 |

---

## API 文件

### 1. 取得商品列表

```http
GET /api/v1/products
```

**Response:**
```json
[
  {
    "id": "p1",
    "name": "大",
    "pricePerCatty": 170,
    "description": "約40顆/斤",
    "isLimited": false
  },
  {
    "id": "p2",
    "name": "特大",
    "pricePerCatty": 200,
    "description": "約30顆/斤",
    "isLimited": false
  },
  {
    "id": "p3",
    "name": "冬季限定款",
    "pricePerCatty": 220,
    "description": "約23顆/斤",
    "isLimited": true,
    "stockRemaining": 50
  }
]
```

---

### 2. 計算訂單金額

```http
POST /api/v1/order/calculate
```

**Request:**
```json
{
  "items": [
    { "productId": "p1", "qty": 10 },
    { "productId": "p2", "qty": 5 }
  ],
  "deliveryMethod": "TAICHUNG_DELIVERY",
  "district": "南區"
}
```

**Response:**
```json
{
  "totalWeight": 15,
  "subtotal": 2700,
  "bulkDiscount": -75,
  "pickupDiscount": 0,
  "shippingFee": 0,
  "finalAmount": 2625,
  "isValid": true,
  "message": "免運優惠已套用 / 滿10斤每斤-$5"
}
```

---

### 3. 提交訂單

```http
POST /api/v1/order/submit
```

**Request:**
```json
{
  "customerName": "王小明",
  "phone": "0912345678",
  "deliveryMethod": "TAICHUNG_DELIVERY",
  "district": "南區",
  "address": "台中市南區復興路一段100號",
  "items": [
    { "productId": "p1", "qty": 10 }
  ],
  "isManagementOfficeCollect": true,
  "paymentLastFive": "12345"
}
```

**Response:**
```json
{
  "orderId": "ORD-20260309-001",
  "status": "PENDING_PAYMENT",
  "totalPay": "1700",
  "message": "訂單已收到，請於指定時間前完成付款"
}
```

---

### 4. 管理員 - 匯出訂單

```http
GET /api/v1/admin/orders/export?groupBy=district
```

**Response:** CSV 檔案下載

---

## 業務規則

### 最低訂購量

| 區域 | 最低斤數 |
|------|----------|
| 台中南區 | 3 斤 |
| 其他區域 | 5 斤 |

### 量販優惠

| 訂購量 | 折扣 |
|--------|------|
| 10 斤以上 | 每斤 -$5 |
| 20 斤以上 | 每斤 -$10 |

### 配送方式

| 方式 | 說明 |
|------|------|
| `PICKUP` | 自取（無運費，每斤額外 -$10） |
| `TAICHUNG_DELIVERY` | 台中市配送 |
| `HOME_DELIVERY` | 宅配送貨 |

### 運費規則

| 區域 | 免運門檻 | 運費 |
|------|----------|------|
| 南區 | 3 斤 | 超過免運 |
| 台中免運區 | 5 斤 | 超過免運 |
| 外縣市 | 15 斤 | $250 |

### 訂單狀態

| 狀態 | 說明 |
|------|------|
| `PENDING_PAYMENT` | 待核帳 |
| `PAID` | 已付款 |
| `READY_TO_SHIP` | 待出貨 |
| `COMPLETED` | 已完結 |
| `CANCELLED` | 已取消 |

---

## 開始使用

### 前置需求

- Java 17+
- Gradle 9.x

### 本地開發

```bash
# 克隆專案
git clone <repository-url>
cd clam-order-backend

# 編譯專案
./gradlew compileJava

# 執行測試
./gradlew test

# 啟動開發伺服器
./gradlew bootRun
```

伺服器啟動後：
- API 文件：http://localhost:8080/swagger-ui/index.html
- H2 Console：http://localhost:8080/h2-console

### 使用 Docker 執行（可選）

```bash
# 建置 JAR
./gradlew build -x test

# 執行
java -jar build/libs/clam-order-backend-0.0.1-SNAPSHOT.jar
```

---

## 開發指南

### 新增商品

修改 `DataInitializer.java` 來新增初始商品：

```java
Product.builder()
    .publicId("p4")
    .name("特大極品")
    .pricePerCatty(250)
    .description("約25顆/斤")
    .isLimited(true)
    .stockRemaining(30)
    .isActive(true)
    .build()
```

### 新增折扣規則

修改 `DiscountPolicy.java`：

```java
// 新增新的折扣常數
public static final BigDecimal NEW_DISCOUNT = BigDecimal.valueOf(15);

// 在對應方法中新增邏輯
public static BigDecimal calculateBulkDiscount(Integer totalWeight) {
    // ...
}
```

### 新增 API Endpoint

1. 在 `application/dto/` 新增 Request/Response DTO
2. 在 `application/usecase/` 新增 Use Case
3. 在 `presentation/controller/` 新增 Controller

---

## 資料庫

### 開發環境（H2）

自動建立資料表，無需額外設定。

### 生產環境（PostgreSQL）

修改 `application.yaml`：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/clamdb
    username: your_username
    password: your_password
  jpa:
    hibernate:
      ddl-auto: validate
```

### 實體關係

```
Product (1) ──────< (N) OrderItem
                            │
                            └─> (1) Order
```

---

## 測試

```bash
# 執行所有測試
./gradlew test

# 執行特定測試類別
./gradlew test --tests DiscountPolicyTest

# 查看測試報告
open build/reports/tests/test/index.html
```

### 測試覆蓋

- ✅ 量販折扣計算
- ✅ 自取折扣計算
- ✅ 運費計算
- ✅ 配送區域判斷
- ✅ 完整價格計算流程

---

## 部署

### Render / Railway 等平台

1. 推送至 GitHub
2. 連結 GitHub 專案
3. 設定環境變數：
   ```
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=postgres://...
   ```

### Render (render.yaml)

```yaml
services:
  - type: web
    name: clam-order-backend
    env: java
    buildCommand: ./gradlew build -x test
    startCommand: java -jar build/libs/clam-order-backend-0.0.1-SNAPSHOT.jar
```

---

## TODO

- [ ] 加入 JWT 認證
- [ ] LINE Bot 整合
- [ ] 電子郵件通知
- [ ]  Payment 串接（綠界/藍新）
- [ ] Docker Compose 部署配置

---

## 授權

MIT License

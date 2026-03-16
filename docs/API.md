# API Documentation - 蛤蠣訂單系統

Base URL: `http://localhost:8080/api/v1`

---

## 商品 API

### 取得商品列表

```http
GET /products
```

**Response:**
```json
[
  {
    "id": "p1",
    "name": "大",
    "pricePerCatty": 170,
    "description": "約40顆/斤",
    "isLimited": false,
    "stockRemaining": null
  },
  {
    "id": "p2",
    "name": "特大",
    "pricePerCatty": 200,
    "description": "約30顆/斤",
    "isLimited": false,
    "stockRemaining": null
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

## 訂單 API

### 計算訂單金額

```http
POST /order/calculate
```

**Request:**
```json
{
  "items": [
    { "productId": "p1", "qty": 10 },
    { "productId": "p2", "qty": 5 }
  ],
  "deliveryMethod": "taichung_delivery",
  "district": "北區"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|------|------|------|------|
| items | Array | 是 | 商品列表 |
| items[].productId | String | 是 | 商品 ID (p1, p2, p3) |
| items[].qty | Integer | 是 | 數量 (最少 1) |
| deliveryMethod | String | 是 | 配送方式 |
| district | String | 否 | 配送地區 |
| address | String | 否 | 詳細地址 |

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

### 提交訂單

```http
POST /order/submit
```

**Request:**
```json
{
  "customerName": "王小明",
  "phone": "0912345678",
  "deliveryMethod": "taichung_delivery",
  "district": "北區",
  "address": "台中市北區進化路100號",
  "items": [
    { "productId": "p1", "qty": 10 }
  ],
  "isManagementOfficeCollect": true,
  "paymentLastFive": "12345",
  "notes": "備註"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|------|------|------|------|
| customerName | String | 是 | 姓名 |
| phone | String | 是 | 電話 (10-11位數字) |
| deliveryMethod | String | 是 | 配送方式 |
| district | String | 否 | 配送地區 |
| address | String | 否 | 詳細地址 |
| items | Array | 是 | 商品列表 |
| items[].productId | String | 是 | 商品 ID |
| items[].qty | Integer | 是 | 數量 |
| isManagementOfficeCollect | Boolean | 否 | 是否為管理室代收 |
| paymentLastFive | String | 否 | 帳號後五碼 (5位數字) |
| notes | String | 否 | 備註 |

**Response:**
```json
{
  "orderId": "ORD-20260315-001",
  "status": "PENDING_PAYMENT",
  "totalPay": "2625",
  "message": "訂單已收到，請於指定時間前完成付款"
}
```

---

## 管理員 API

### 取得後台統計數據

```http
GET /admin/dashboard/stats
```

**Response:**
```json
{
  "todayOrdersCount": 24,
  "totalWeight": 158,
  "pendingOrdersCount": 8
}
```

---

### 取得訂單列表 (分頁)

```http
GET /admin/orders?page=1&size=20&status=pending_payment&startDate=2024-03-01&endDate=2024-03-15
```

**Query Parameters:**
| 參數 | 類型 | 預設值 | 說明 |
|------|------|--------|------|
| page | Integer | 1 | 頁碼 |
| size | Integer | 20 | 每頁筆數 |
| status | String | - | 訂單狀態篩選 |
| startDate | String | - | 開始日期 (YYYY-MM-DD) |
| endDate | String | - | 結束日期 (YYYY-MM-DD) |

**Response:**
```json
{
  "totalElements": 150,
  "totalPages": 8,
  "currentPage": 1,
  "pageSize": 20,
  "content": [
    {
      "orderId": "ORD-20240315-001",
      "customerName": "王小明",
      "phone": "0912345678",
      "deliveryMethod": "TAICHUNG_DELIVERY",
      "address": "...",
      "district": "北區",
      "totalWeight": 10,
      "items": "大 x 10斤",
      "isManagementOfficeCollect": true,
      "paymentLastFive": "12345",
      "status": "PENDING_PAYMENT",
      "statusChinese": "待核帳",
      "finalAmount": 1700,
      "notes": "...",
      "createdAt": "2024-03-15T10:30:00"
    }
  ]
}
```

---

### 匯出訂單 (CSV)

```http
GET /admin/orders/export?groupBy=district&startDate=2024-03-01&endDate=2024-03-15
```

**Query Parameters:**
| 參數 | 類型 | 說明 |
|------|------|------|
| groupBy | String | 分組依據 (e.g., "district") |
| startDate | String | 開始日期 (YYYY-MM-DD) |
| endDate | String | 結束日期 (YYYY-MM-DD) |

**Response:** CSV 檔案下載

---

## 參考資料

### 配送方式 (deliveryMethod)

| 值 | 說明 |
|----|------|
| `PICKUP` | 自取 |
| `TAICHUNG_DELIVERY` | 台中配送 |
| `HOME_DELIVERY` | 宅配送貨 |

支援格式: `PICKUP`, `pickup`, `TAICHUNG_DELIVERY`, `taichung_delivery`

### 配送地區 (district)

| 地區 | 最低訂購量 | 運費 |
|------|-----------|------|
| 南區 | 3 斤 | 3斤以上免運，否則 $250 |
| 西區/北區/西屯/北屯/南屯/東區/中區/大里/烏日/太平 | 5 斤 | 5斤以上免運，否則 $250 |
| 其他縣市 | 5 斤 | 15斤以上免運，否則 $250 |

### 優惠規則

| 條件 | 折扣 |
|------|------|
| 滿 10 斤 | 每斤 -$5 |
| 滿 20 斤 | 每斤 -$10 |
| 自取 | 每斤額外 -$10 |

### 訂單狀態

| 狀態 (英文) | 狀態 (中文) | 說明 |
|-------------|-------------|------|
| `PENDING_PAYMENT` | 待核帳 | 待核帳 |
| `PAID` | 已付款 | 已付款 |
| `READY_TO_SHIP` | 待出貨 | 待出貨 |
| `COMPLETED` | 已完結 | 已完結 |
| `CANCELLED` | 已取消 | 已取消 |

---

## Swagger UI

線上 API 文件: http://localhost:8080/swagger-ui/index.html

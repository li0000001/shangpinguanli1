# 快速开始指南

## 项目概述

**商品保质期管家 (Expiry Tracker)** 是一个现代化的安卓应用，帮助用户管理商品保质期并自动创建日历提醒。

## 主要特性

✅ **灵活的日期输入**
- 方式1: 生产日期 + 保质期天数 → 自动计算到期日
- 方式2: 直接输入保质日期

✅ **智能提醒系统**
- 自定义提前提醒天数
- 自动添加到系统日历
- 删除商品时同步删除日历事件

✅ **智能状态标识**
- 🔴 已过期
- 🟠 今天到期  
- 🟡 即将过期 (3天内)
- 🟢 新鲜

✅ **现代化UI**
- Material Design 3
- Jetpack Compose
- 流畅动画

## 技术栈

| 技术 | 版本/框架 |
|------|----------|
| 语言 | Kotlin 1.9.10 |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM |
| 数据库 | Room 2.6.0 |
| 异步 | Kotlin Coroutines |
| 导航 | Navigation Compose |
| 权限 | Accompanist Permissions |

## 项目结构

```
ExpiryTracker/
├── app/
│   ├── src/main/
│   │   ├── java/com/expirytracker/
│   │   │   ├── data/
│   │   │   │   ├── database/      # Room 数据库
│   │   │   │   └── repository/    # 数据仓库
│   │   │   ├── ui/
│   │   │   │   ├── components/    # 可复用组件
│   │   │   │   ├── screens/       # 页面
│   │   │   │   └── theme/         # 主题配置
│   │   │   ├── utils/             # 工具类
│   │   │   ├── viewmodel/         # ViewModel
│   │   │   ├── ExpiryTrackerApp.kt
│   │   │   └── MainActivity.kt
│   │   ├── res/                   # 资源文件
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 开始使用

### 1. 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.2

### 2. 克隆项目
```bash
git clone <repository-url>
cd ExpiryTracker
```

### 3. 构建项目
```bash
./gradlew build
```

### 4. 运行应用
- 在 Android Studio 中打开项目
- 连接 Android 设备或启动模拟器 (API 24+)
- 点击 Run 按钮

或使用命令行：
```bash
./gradlew installDebug
```

## 权限

应用需要以下权限：
- `READ_CALENDAR` - 读取日历
- `WRITE_CALENDAR` - 写入日历事件

首次运行时会自动请求权限。

## 使用流程

1. **启动应用** → 授予日历权限
2. **添加商品** → 点击 + 按钮
3. **输入信息**:
   - 商品名称（必填）
   - 选择日期输入方式
   - 设置提醒天数
4. **保存** → 自动创建日历提醒
5. **查看列表** → 按到期日期排序
6. **删除商品** → 同步删除日历事件

## 核心文件说明

| 文件 | 说明 |
|------|------|
| `ProductEntity.kt` | 商品数据模型 |
| `ProductDao.kt` | 数据库访问接口 |
| `AppDatabase.kt` | Room 数据库配置 |
| `ProductRepository.kt` | 数据仓库，处理数据和日历逻辑 |
| `ProductViewModel.kt` | 视图模型，管理 UI 状态 |
| `ProductListScreen.kt` | 商品列表页面 |
| `AddProductScreen.kt` | 添加商品页面 |
| `CalendarHelper.kt` | 日历操作工具 |
| `DateUtils.kt` | 日期计算工具 |

## 开发说明

### MVVM 架构

```
View (Compose UI)
    ↕
ViewModel (ProductViewModel)
    ↕
Repository (ProductRepository)
    ↕
Data Source (Room + Calendar)
```

### 数据流

1. **添加商品**:
   ```
   UI → ViewModel → Repository → Room 数据库
                              → Calendar API
   ```

2. **删除商品**:
   ```
   UI → ViewModel → Repository → Room 数据库
                              → Calendar API (删除事件)
   ```

3. **显示列表**:
   ```
   Room 数据库 → Repository (Flow) → ViewModel (StateFlow) → UI
   ```

## 自定义配置

### 修改默认提醒天数
编辑 `AddProductScreen.kt`:
```kotlin
var reminderDaysBefore by remember { mutableStateOf("3") }
```

### 修改颜色主题
编辑 `ui/theme/Color.kt`:
```kotlin
val Primary = Color(0xFF6750A4)  // 修改主色调
```

### 修改过期状态阈值
编辑 `DateUtils.kt`:
```kotlin
return when {
    daysUntil < 0 -> ExpiryStatus.EXPIRED
    daysUntil == 0 -> ExpiryStatus.EXPIRING_TODAY
    daysUntil <= 3 -> ExpiryStatus.EXPIRING_SOON  // 修改这里
    daysUntil <= 7 -> ExpiryStatus.WARNING
    else -> ExpiryStatus.FRESH
}
```

## 故障排除

### 权限被拒绝
- 进入系统设置 → 应用 → 商品保质期管家 → 权限
- 手动开启日历权限

### 日历事件未创建
- 检查设备是否有日历账户
- 确认日历权限已授予
- 查看 Logcat 日志了解错误信息

### 构建失败
```bash
# 清理项目
./gradlew clean

# 重新构建
./gradlew build --refresh-dependencies
```

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License

---

**享受使用商品保质期管家！** 📦⏰

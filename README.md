# 商品保质期管家 (Expiry Tracker)

一个时尚、实用的安卓应用，帮助用户管理商品的保质期，自动提醒即将过期的商品。

## 功能特点

### 📦 核心功能
- **灵活的日期输入方式**
  - 方式一：输入生产日期 + 保质期天数，自动计算到期日
  - 方式二：直接输入保质日期
  
- **智能提醒系统**
  - 自定义提前提醒天数
  - 自动添加到手机系统日历
  - 删除商品时同步删除日历事件

- **直观的商品管理**
  - 商品列表按到期日期自动排序
  - 彩色状态标识（已过期、今天到期、即将过期、注意、新鲜）
  - 快速查看剩余天数

- **时尚的用户界面**
  - 采用 Material Design 3 设计规范
  - 流畅的动画效果
  - 简洁直观的操作体验

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构模式**: MVVM (Model-View-ViewModel)
- **数据库**: Room
- **异步处理**: Kotlin Coroutines
- **权限管理**: Accompanist Permissions
- **日历集成**: Android Calendar Provider API

## 项目结构

```
com.expirytracker/
├── data/
│   ├── database/
│   │   ├── AppDatabase.kt
│   │   ├── ProductDao.kt
│   │   └── ProductEntity.kt
│   └── repository/
│       └── ProductRepository.kt
├── ui/
│   ├── components/
│   │   ├── DatePickerField.kt
│   │   └── ProductCard.kt
│   ├── screens/
│   │   ├── AddProductScreen.kt
│   │   └── ProductListScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── utils/
│   ├── CalendarHelper.kt
│   └── DateUtils.kt
├── viewmodel/
│   └── ProductViewModel.kt
├── ExpiryTrackerApp.kt
└── MainActivity.kt
```

## 安装要求

- Android 7.0 (API 24) 或更高版本
- 日历读写权限

## 构建项目

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd ExpiryTracker

# 构建项目
./gradlew build

# 安装到设备
./gradlew installDebug
```

## 权限说明

应用需要以下权限：
- `READ_CALENDAR`: 读取日历以检查现有事件
- `WRITE_CALENDAR`: 创建和删除保质期提醒事件

## 使用方法

1. **添加商品**
   - 点击右下角的 "+" 按钮
   - 输入商品名称（必填）
   - 选择输入方式：
     - 生产日期 + 保质期天数
     - 或直接输入保质日期
   - 设置提前提醒天数（默认3天）
   - 点击"保存商品"

2. **查看商品列表**
   - 商品按到期日期排序
   - 不同颜色表示不同状态：
     - 🔴 红色：已过期
     - 🟠 橙色：今天到期
     - 🟡 黄色：即将过期（3天内）
     - 🟢 绿色：新鲜

3. **删除商品**
   - 在商品卡片上点击删除按钮
   - 确认删除后，日历事件也会同步删除

## 开发团队

使用 Kotlin + Jetpack Compose 开发，遵循 Android 最佳实践。

## 许可证

MIT License

# 商品保质期管家 - 项目信息

## 项目完成状态 ✅

该项目已完成所有核心功能的开发，包括：

### ✅ 已实现功能

1. **数据层 (Data Layer)**
   - ✅ Room 数据库配置 (AppDatabase.kt)
   - ✅ 商品实体定义 (ProductEntity.kt)
   - ✅ 数据访问对象 (ProductDao.kt)
   - ✅ 仓库模式实现 (ProductRepository.kt)

2. **UI层 (UI Layer)**
   - ✅ Jetpack Compose 主题系统 (Color.kt, Theme.kt, Type.kt)
   - ✅ 商品列表屏幕 (ProductListScreen.kt)
   - ✅ 添加商品屏幕 (AddProductScreen.kt)
   - ✅ 商品卡片组件 (ProductCard.kt)
   - ✅ 日期选择组件 (DatePickerField.kt)

3. **业务逻辑层 (Business Logic)**
   - ✅ ProductViewModel (MVVM架构)
   - ✅ 日历集成工具 (CalendarHelper.kt)
   - ✅ 日期工具类 (DateUtils.kt)

4. **核心功能**
   - ✅ 两种日期输入方式（生产日期+保质期 或 直接输入保质日期）
   - ✅ 自动计算到期日
   - ✅ 自定义提醒天数
   - ✅ 日历权限请求
   - ✅ 自动添加到系统日历
   - ✅ 删除商品时同步删除日历事件
   - ✅ 商品按到期日排序
   - ✅ 彩色状态标识

5. **用户体验**
   - ✅ Material Design 3 时尚界面
   - ✅ 简体中文界面
   - ✅ 应用图标
   - ✅ 权限处理
   - ✅ 错误提示

## 技术栈

- **语言**: Kotlin 1.9.10
- **最低 SDK**: Android 7.0 (API 24)
- **目标 SDK**: Android 14 (API 34)
- **UI**: Jetpack Compose + Material 3
- **架构**: MVVM
- **数据库**: Room 2.6.0
- **异步**: Kotlin Coroutines
- **构建工具**: Gradle 8.2

## 文件统计

- Kotlin 文件: 18 个
- XML 文件: 9 个
- 总代码行数: 约 1500+ 行

## 构建和运行

```bash
# 构建项目
./gradlew build

# 安装到设备
./gradlew installDebug

# 运行应用
./gradlew run
```

## 权限要求

- READ_CALENDAR
- WRITE_CALENDAR

## 包结构

```
com.expirytracker
├── data
│   ├── database (Room 数据库)
│   └── repository (数据仓库)
├── ui
│   ├── components (可复用组件)
│   ├── screens (屏幕)
│   └── theme (主题)
├── utils (工具类)
├── viewmodel (视图模型)
├── ExpiryTrackerApp.kt (Application 类)
└── MainActivity.kt (主 Activity)
```

## 特色功能

1. **智能过期提醒**: 根据距离过期的天数，用不同颜色标识
   - 🔴 已过期
   - 🟠 今天到期
   - 🟡 即将过期 (3天内)
   - 🟢 新鲜

2. **灵活的日期输入**: 支持两种方式输入
   - 生产日期 + 保质期天数
   - 直接输入保质日期

3. **日历集成**: 自动管理系统日历事件
   - 添加商品时自动创建提醒
   - 删除商品时自动删除提醒

4. **现代化界面**: 使用 Material Design 3
   - 流畅的动画
   - 直观的交互
   - 美观的视觉设计

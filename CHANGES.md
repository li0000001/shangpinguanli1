# 代码变更说明

## 修复的问题

1. ✅ 应用程序内设置的提前提醒天数参数现在可以在日历功能中正确生效
2. ✅ 应用程序内配置的提醒方式设置现在可以在日历功能中正常执行

## 变更的文件

### 1. `app/src/main/java/com/expirytracker/utils/CalendarHelper.kt`

**关键变更**：

- **参数重命名**：`reminderMinutesBefore: Int` → `reminderDaysBefore: Int`
  - 更符合业务语义（天数而非分钟）
  - 与ProductEntity的字段保持一致

- **事件日期修正**：
  ```kotlin
  // 修复前：事件创建在过期日期
  put(CalendarContract.Events.DTSTART, expiryDate)
  
  // 修复后：事件创建在提醒日期
  val reminderDate = expiryDate - (reminderDaysBefore * 24 * 60 * 60 * 1000L)
  put(CalendarContract.Events.DTSTART, reminderDate)
  ```

- **事件标题优化**：
  ```kotlin
  // 修复前
  "⏰ $title 即将过期"
  
  // 修复后
  "⏰ $title 将在${daysText}过期"  // 如：将在3天后过期
  ```

- **提醒处理改进**：
  - `addReminderToEvent()` 现在返回 `Boolean` 表示成功/失败
  - 添加了完整的错误处理和日志记录
  - 提醒时间固定为事件前60分钟（确保在事件日当天触发）
  - 参数验证：使用 `uppercase()` 处理大小写问题

- **新增日志功能**：
  ```kotlin
  private const val TAG = "CalendarHelper"
  
  Log.d(TAG, "日历事件创建成功，事件ID: $eventId")
  Log.d(TAG, "添加提醒 - 事件ID: $eventId, 提醒方式: $reminderMethod")
  Log.e(TAG, "无法获取日历ID，请确保已授予日历权限")
  ```

- **增强错误处理**：
  - 区分 `SecurityException`（权限问题）和其他异常
  - 所有错误都有详细的日志记录
  - 删除事件时也增加了日志和错误处理

### 2. `app/src/main/java/com/expirytracker/data/repository/ProductRepository.kt`

**关键变更**：

- **简化参数传递**：
  ```kotlin
  // 修复前：在Repository层转换为分钟
  val reminderMinutes = product.reminderDaysBefore * 24 * 60
  CalendarHelper.addEventToCalendar(..., reminderMinutes, ...)
  
  // 修复后：直接传递天数
  CalendarHelper.addEventToCalendar(..., product.reminderDaysBefore, ...)
  ```

- **影响的方法**：
  - `addProduct()`：添加商品时创建日历事件
  - `updateProduct()`：更新商品时重新创建日历事件

## 技术细节

### 日期计算逻辑

**问题**：原来的实现中，日历事件显示在过期日期，但用户期望在提醒日期看到事件

**解决方案**：
```kotlin
// 计算提醒日期（过期日期前N天）
val reminderDate = expiryDate - (reminderDaysBefore * 24 * 60 * 60 * 1000L)

// 事件在提醒日期创建
put(CalendarContract.Events.DTSTART, reminderDate)

// 提醒在事件开始前1小时触发（确保当天能收到提醒）
put(CalendarContract.Reminders.MINUTES, 60)
```

**效果**：
- 商品过期日期：2024-01-10
- 提前提醒天数：3天
- **日历中的事件日期**：2024-01-07
- **事件标题**："⏰ 牛奶 将在3天后过期"
- **提醒触发时间**：2024-01-07 上午（事件开始前1小时）

### 提醒方式映射

```kotlin
when (reminderMethod.uppercase()) {
    "ALARM" -> CalendarContract.Reminders.METHOD_ALARM        // 值：4（闹钟）
    "NOTIFICATION" -> CalendarContract.Reminders.METHOD_ALERT  // 值：1（通知）
    "ALERT" -> CalendarContract.Reminders.METHOD_ALERT         // 值：1（通知）
    else -> CalendarContract.Reminders.METHOD_ALARM            // 默认：闹钟
}
```

## 测试建议

### 测试用例1：基本功能
1. 添加商品，设置3天提前提醒
2. 打开系统日历应用
3. 验证事件出现在正确日期（过期日期前3天）
4. 验证事件标题包含"将在3天后过期"

### 测试用例2：提醒方式
1. 添加商品A，选择"闹钟提醒"
2. 添加商品B，选择"通知提醒"
3. 检查logcat日志
4. 验证日志显示正确的提醒方式（METHOD_ALARM vs METHOD_ALERT）

### 测试用例3：边界情况
1. 设置提前0天提醒 → 验证事件在过期当天，标题为"今天过期"
2. 设置提前30天提醒 → 验证事件在30天前正确创建
3. 编辑已有商品，更改提醒天数 → 验证旧事件被删除，新事件被创建

### 测试用例4：权限处理
1. 撤销日历权限
2. 尝试添加商品
3. 检查logcat，应显示"没有日历权限"错误
4. 验证应用不崩溃，商品仍保存到数据库

### 测试用例5：更新和删除
1. 添加商品后，检查日历中的事件ID
2. 更新商品（改变提醒天数或方式）
3. 验证日历中旧事件被删除，新事件被创建
4. 删除商品，验证日历事件也被删除

## 日志示例

成功创建事件：
```
D/CalendarHelper: 日历事件创建成功，事件ID: 12345
D/CalendarHelper: 添加提醒 - 事件ID: 12345, 提醒方式: ALARM (方法值: 4), 提前时间: 60分钟
D/CalendarHelper: 提醒添加成功，使用方式: 闹钟
```

权限错误：
```
E/CalendarHelper: 无法获取日历ID，请确保已授予日历权限
```

提醒添加失败：
```
E/CalendarHelper: 添加提醒失败，但事件已创建
E/CalendarHelper: 添加提醒时出错: Permission denied
```

删除事件：
```
D/CalendarHelper: 成功删除日历事件，事件ID: 12345
```

## 兼容性说明

- ✅ **数据库兼容**：ProductEntity结构未变更，现有数据完全兼容
- ✅ **API向后兼容**：虽然CalendarHelper内部实现变更，但所有调用处已同步更新
- ✅ **Android版本兼容**：使用标准Calendar Provider API，支持Android 7.0+

## 注意事项

1. **日志标签**：所有日志使用统一标签"CalendarHelper"，便于过滤
2. **错误恢复**：即使添加提醒失败，事件仍会被创建（用户至少能在日历中看到事件）
3. **提醒时间**：固定为事件前60分钟，确保在事件日当天上午能收到提醒
4. **权限检查**：需要READ_CALENDAR和WRITE_CALENDAR权限（已在AndroidManifest中声明）

## 总结

本次修复从根本上解决了日历同步的两个核心问题：

1. **日期准确性**：通过在正确的日期创建事件，确保用户体验符合预期
2. **功能可靠性**：通过完善的错误处理和日志，提高系统稳定性和可维护性

用户现在可以：
- ✅ 在日历中的正确日期看到提醒事件
- ✅ 选择不同的提醒方式（闹钟/通知）并正常工作
- ✅ 通过日志追踪任何问题（便于调试和用户支持）

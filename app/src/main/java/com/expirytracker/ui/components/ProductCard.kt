package com.expirytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expirytracker.data.database.ProductEntity
import com.expirytracker.ui.theme.*
import com.expirytracker.utils.DateUtils
import com.expirytracker.utils.ExpiryStatus

@Composable
fun ProductCard(
    product: ProductEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = DateUtils.getExpiryStatus(product.expiryDate)
    val daysUntil = DateUtils.getDaysUntilExpiry(product.expiryDate)
    
    val statusColor = when (status) {
        ExpiryStatus.EXPIRED -> ExpiredColor
        ExpiryStatus.EXPIRING_TODAY -> ExpiringTodayColor
        ExpiryStatus.EXPIRING_SOON -> ExpiringSoonColor
        ExpiryStatus.WARNING -> WarningColor
        ExpiryStatus.FRESH -> FreshColor
    }
    
    val statusText = when (status) {
        ExpiryStatus.EXPIRED -> "已过期"
        ExpiryStatus.EXPIRING_TODAY -> "今天到期"
        ExpiryStatus.EXPIRING_SOON -> "即将过期"
        ExpiryStatus.WARNING -> "注意"
        ExpiryStatus.FRESH -> "新鲜"
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(statusColor)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = statusText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = when {
                            daysUntil < 0 -> "已过期 ${-daysUntil} 天"
                            daysUntil == 0 -> "今天到期"
                            else -> "还剩 $daysUntil 天"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = "保质日期: ${DateUtils.formatDate(product.expiryDate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                
                if (product.productionDate != null) {
                    Text(
                        text = "生产日期: ${DateUtils.formatDate(product.productionDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

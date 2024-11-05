package `in`.devh.rail.functions

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import `in`.devh.rail.services.CellUpdateService
import `in`.devh.rail.utils.PermissionHandler
import `in`.devh.rail.utils.PreferenceManager

fun Context.handleServiceState(enabled: Boolean) {
    PreferenceManager.setServiceEnabled(this, enabled)

    if (enabled) {
        if (PermissionHandler.hasRequiredPermissions(this)) {
            // Request battery optimization disable
            PermissionHandler.requestBatteryOptimizationDisable(this)

            // Start the service
            Intent(this, CellUpdateService::class.java).apply {
                action = CellUpdateService.ACTION_START_SERVICE
                ContextCompat.startForegroundService(this@handleServiceState, this)
            }
        }
    } else {
        // Stop the service
        Intent(this, CellUpdateService::class.java).apply {
            action = CellUpdateService.ACTION_STOP_SERVICE
            startService(this)
        }
    }
}
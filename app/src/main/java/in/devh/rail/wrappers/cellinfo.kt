package `in`.devh.rail.wrappers

import android.annotation.SuppressLint
import android.content.Context
import cz.mroczis.netmonster.core.factory.NetMonsterFactory
import cz.mroczis.netmonster.core.model.cell.*
import cz.mroczis.netmonster.core.model.connection.PrimaryConnection
import org.json.JSONObject

data class CellData(
    var mcc: String = "",
    var mnc: String = "",
    var lac: String = "",
    var cid: String = "",
    var gsmSignal: String = "",
    var wcdmaSignal: String = "",
    var lteSignal: String = "",
    var nrSignal: String = "",
    var tdscdmaSignal: String = ""
)

fun CellData.toJson(): String {
    return JSONObject().apply {
        put("mcc", mcc)
        put("mnc", mnc)
        put("lac", lac)
        put("cid", cid)
        put("strengthGSM", gsmSignal)
        put("strengthWCDMA", wcdmaSignal)
        put("strengthLTE", lteSignal)
        put("strengthNR", nrSignal)
        put("strengthTDSCDMA", tdscdmaSignal)
    }.toString()
}

class CellInfoWrapper(private val context: Context) {
    @SuppressLint("MissingPermission")
    fun getCellInfo(): CellData {
        val cellData = CellData()
        val netMonster = NetMonsterFactory.get(context)

        val cells = netMonster.getCells()
        val servingCell = cells.firstOrNull { it.connectionStatus is PrimaryConnection }

        servingCell?.let { cell ->
            when (cell) {
                is CellGsm -> {
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.lac.toString()
                    cellData.cid = cell.cid.toString()
                    cellData.gsmSignal = "${cell.signal.rssi} dBm"
                }
                is CellWcdma -> {
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.lac.toString()
                    cellData.cid = cell.ci.toString()
                    cellData.wcdmaSignal = "${cell.signal.rscp} dBm"
                }
                is CellLte -> {
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.tac.toString()
                    cellData.cid = cell.eci.toString()
                    cellData.lteSignal = "${cell.signal.rsrp} dBm"
                }
                is CellNr -> {
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.tac.toString()
                    cellData.cid = cell.nci.toString()
                    cellData.nrSignal = "${cell.signal.ssRsrp} dBm"
                }
                is CellTdscdma -> {
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.lac.toString()
                    cellData.cid = cell.ci.toString()
                    cellData.tdscdmaSignal = "${cell.signal.rscp} dBm"
                }
            }
        }

        return cellData
    }
}
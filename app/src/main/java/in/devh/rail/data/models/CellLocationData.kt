package `in`.devh.rail.data.models

data class CellLocationData(
    var mcc: String = "",
    var mnc: String = "",
    var lac: String = "",
    var cid: String = "",
    var timestamp: Long = System.currentTimeMillis()
)
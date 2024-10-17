package `in`.devh.rail.data.models

data class CellLocationData(
    var mcc: String = "",
    var mnc: String = "",
    var lac: String = "",
    var cid: String = "",
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
    var timestamp: Long = System.currentTimeMillis()
)
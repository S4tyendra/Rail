package `in`.devh.rail.data.models

data class PassengerStatu(
    val Berth: Int,
    val BookingBerthCode: String,
    val BookingBerthNo: String,
    val BookingCoachId: String,
    val BookingStatus: String,
    val BookingStatusIndex: String,
    val BookingStatusNew: String,
    val Coach: String,
    val CoachPosition: Any,
    val ConfirmTktStatus: String,
    val CurrentBerthCode: Any,
    val CurrentBerthNo: String,
    val CurrentCoachId: String,
    val CurrentStatus: String,
    val CurrentStatusIndex: String,
    val CurrentStatusNew: String,
    val Number: Int,
    val Pnr: Any,
    val Prediction: String,
    val PredictionPercentage: Any,
    val ReferenceId: Any
)
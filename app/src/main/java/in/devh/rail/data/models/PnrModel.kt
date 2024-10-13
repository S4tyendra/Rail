package `in`.devh.rail.data.models

data class PnrModel(
    val Ads: List<Any>,
    val ArrivalTime: String,
    val BoardingPoint: String,
    val BoardingStationName: String,
    val BookedByUser: Boolean,
    val BookedInConfirmtkt: Boolean,
    val BookingDate: String,
    val BookingFare: String,
    val BookingId: String,
    val CacheTime: String,
    val ChartPrepared: Boolean,
    val Class: String,
    val CleanlinessRating: Double,
    val CoachPosition: String,
    val DepartureTime: String,
    val DestinationDoj: String,
    val DestinationName: String,
    val Doj: String,
    val Duration: String,
    val Error: Any,
    val ErrorCode: Int,
    val ExpectedPlatformNo: String,
    val FlightBannerUrl: String,
    val FoodRating: Double,
    val From: String,
    val FromStnActual: Any,
    val GroupingId: Any,
    val HasPantry: Boolean,
    val InformationMessage: Any,
    val OptVikalp: Boolean,
    val PassengerCount: Int,
    val PassengerStatus: List<PassengerStatu>,
    val Pnr: String,
    val PnrAlternativeAdPosition: Int,
    val PunctualityRating: Double,
    val Quota: String,
    val Rating: Double,
    val RatingCount: Int,
    val ReservationUpto: String,
    val ReservationUptoName: String,
    val ShowBlaBlaAd: Boolean,
    val ShowCab: Boolean,
    val SourceDoj: String,
    val SourceName: String,
    val SponsoredButtons: List<Any>,
    val TicketFare: String,
    val To: String,
    val TrainCancelledFlag: Boolean,
    val TrainName: String,
    val TrainNo: String,
    val TrainStatus: String,
    val VikalpData: String,
    val VikalpTransferred: Boolean,
    val VikalpTransferredMessage: String,
    val WebsiteAds: Any,
    val WebsiteEvents: List<String>
)
package `in`.devh.rail.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.devh.rail.ui.theme.RailTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

data class PNRResponse(
    val pnr: String,
    val trainNo: String,
    val trainName: String,
    val doj: String,
    val from: String,
    val to: String,
    val passengerStatus: List<PassengerStatus>
)

data class PassengerStatus(
    val number: Int,
    val currentStatus: String,
    val coach: String,
    val berth: Int
)
class PNRViewModel : ViewModel() {
    private val client = OkHttpClient()

    private val _pnrResponse = MutableStateFlow<PNRResponse?>(null)
    val pnrResponse: StateFlow<PNRResponse?> = _pnrResponse

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchPNRStatus(pnrNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _pnrResponse.value = null
            _logs.value = emptyList()

            addLog("Starting PNR request for $pnrNumber")

            val request = Request.Builder()
                .url("https://api.confirmtkt.com/api/pnr/status/$pnrNumber")
                .header("Host", "api.confirmtkt.com")
                .header("Connection", "Keep-Alive")
                .header("User-Agent", "okhttp/4.9.2")
                .header("Accept", "application/json")
                .build()

            try {
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                addLog("PNR response received")
                addLog("Response code: ${response.code}")

                response.body?.let { responseBody ->
                    val bodyString = withContext(Dispatchers.IO) {
                        responseBody.string()
                    }
                    addLog("Response body length: ${bodyString.length} characters")
                    try {
                        val jsonObject = JSONObject(bodyString)
                        _pnrResponse.value = parsePNRResponse(jsonObject)
                        addLog("PNR response parsed successfully")
                    } catch (e: Exception) {
                        addLog("Failed to parse PNR response: ${e.message}")
                    }
                }
            } catch (e: IOException) {
                addLog("PNR request failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parsePNRResponse(json: JSONObject): PNRResponse {
        val passengerStatus = json.getJSONArray("PassengerStatus").let { array ->
            List(array.length()) { i ->
                val passenger = array.getJSONObject(i)
                PassengerStatus(
                    number = passenger.getInt("Number"),
                    currentStatus = passenger.getString("CurrentStatus"),
                    coach = passenger.getString("Coach"),
                    berth = passenger.getInt("Berth")
                )
            }
        }

        return PNRResponse(
            pnr = json.getString("Pnr"),
            trainNo = json.getString("TrainNo"),
            trainName = json.getString("TrainName"),
            doj = json.getString("Doj"),
            from = json.getString("From"),
            to = json.getString("To"),
            passengerStatus = passengerStatus
        )
    }

    private fun addLog(message: String) {
        _logs.value = _logs.value + message
    }
}
@Composable
fun PNRScreen(viewModel: PNRViewModel = viewModel()) {
    var pnrNumber by remember { mutableStateOf("") }
    val pnrResponse by viewModel.pnrResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "PNR Status Check",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = pnrNumber,
            onValueChange = { pnrNumber = it.filter { char -> char.isDigit() } },
            label = { Text("Enter 10-digit PNR number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)

        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.fetchPNRStatus(pnrNumber) },
            modifier = Modifier.align(Alignment.End),
            enabled = !isLoading && pnrNumber.length == 10 && pnrNumber.all { it.isDigit() }
        ) {
            Text("Check Status")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            pnrResponse?.let { pnr ->
                PNRDetails(pnr)
            }
        }
    }
}

@Composable
fun PNRDetails(pnr: PNRResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "PNR: ${pnr.pnr}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("${pnr.trainNo} - ${pnr.trainName}", style = MaterialTheme.typography.titleMedium)
            Text("Date: ${pnr.doj}")
            Text("From: ${pnr.from}")
            Text("To: ${pnr.to}")

            Spacer(modifier = Modifier.height(16.dp))
            Text("Passenger Status:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            pnr.passengerStatus.forEach { passenger ->
                PassengerStatusItem(passenger)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PassengerStatusItem(passenger: PassengerStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Passenger ${passenger.number}", fontWeight = FontWeight.Bold)
                Text(passenger.currentStatus)
            }
            Text("${passenger.coach} ${passenger.berth}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RailAppPreview() {
    RailTheme(darkTheme = true, dynamicColor = true) {
        PNRScreen()
    }
}

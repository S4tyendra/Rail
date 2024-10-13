package `in`.devh.rail.ui.urlfetcher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException


class URLFetcherViewModel : ViewModel() {
    private val client = OkHttpClient()

    private val _responseText = MutableStateFlow<String?>(null)
    val responseText: StateFlow<String?> = _responseText

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchUrl(url: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _responseText.value = null
            _logs.value = emptyList()

            addLog("Starting request to $url")

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    viewModelScope.launch {
                        addLog("Request failed: ${e.message}")
                        _isLoading.value = false
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    viewModelScope.launch {
                        addLog("Response received")
                        addLog("Response code: ${response.code}")

                        response.body?.string()?.let { body ->
                            _responseText.value = body
                            addLog("Response body length: ${body.length} characters")
                        }

                        _isLoading.value = false
                    }
                }
            })
        }
    }

    private fun addLog(message: String) {
        _logs.value = _logs.value + message
    }
}
@Composable
fun URLFetcherScreen(viewModel: URLFetcherViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    var url by remember { mutableStateOf("") }
    val responseText by viewModel.responseText.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Enter URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.fetchUrl(url) },
            modifier = Modifier.align(Alignment.End),
            enabled = !isLoading
        ) {
            Text("Fetch URL")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        LazyColumn {
            item {
                Text("Logs:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(logs) { log ->
                Text(log)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Response:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                responseText?.let {
                    Text(it)
                }
            }
        }
    }
}

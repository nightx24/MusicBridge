package com.musicbridge.app

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject

data class Track(val title: String, val artist: String, val album: String = "")

class MainActivity : ComponentActivity() {
    private val prefs by lazy { getSharedPreferences("musicbridge", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MusicBridgeScreen(
                        initialTracks = loadTracks(),
                        onSave = { saveTracks(it) },
                        onExportJson = { uri, tracks -> writeText(uri, tracksToJson(tracks)) },
                        onExportCsv = { uri, tracks -> writeText(uri, tracksToCsv(tracks)) },
                        onImportJson = { uri -> readText(uri) }
                    )
                }
            }
        }
    }

    private fun loadTracks(): List<Track> = runCatching {
        jsonToTracks(prefs.getString("tracks", "[]") ?: "[]")
    }.getOrDefault(emptyList())

    private fun saveTracks(tracks: List<Track>) {
        prefs.edit().putString("tracks", tracksToJson(tracks)).apply()
    }

    private fun writeText(uri: Uri, text: String) {
        contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(text) }
            ?: error("Could not open the selected file")
    }

    private fun readText(uri: Uri): String =
        contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            ?: error("Could not read the selected file")
}

@Composable
private fun MusicBridgeScreen(
    initialTracks: List<Track>,
    onSave: (List<Track>) -> Unit,
    onExportJson: (Uri, List<Track>) -> Unit,
    onExportCsv: (Uri, List<Track>) -> Unit,
    onImportJson: (Uri) -> String
) {
    var tracks by remember { mutableStateOf(initialTracks) }
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Your library stays on this device.") }

    val jsonExport = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) runCatching { onExportJson(uri, tracks) }
            .onSuccess { message = "JSON backup exported." }
            .onFailure { message = "Export failed: ${it.message}" }
    }
    val csvExport = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) runCatching { onExportCsv(uri, tracks) }
            .onSuccess { message = "CSV exported." }
            .onFailure { message = "Export failed: ${it.message}" }
    }
    val jsonImport = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) runCatching { onImportJson(uri) }
            .map { jsonToTracks(it) }
            .onSuccess { imported ->
                tracks = imported
                onSave(tracks)
                message = "Imported ${imported.size} tracks."
            }
            .onFailure { message = "Import failed. Choose a valid MusicBridge JSON backup." }
    }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("MusicBridge", style = MaterialTheme.typography.headlineLarge)
        Text("Your music library. Your choice.", style = MaterialTheme.typography.titleMedium)
        Text("Free playlist backup and transfer tools", style = MaterialTheme.typography.bodyMedium)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add a track", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Song title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = artist, onValueChange = { artist = it }, label = { Text("Artist") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Button(
                    onClick = {
                        if (title.isNotBlank() && artist.isNotBlank()) {
                            tracks = tracks + Track(title.trim(), artist.trim())
                            onSave(tracks)
                            title = ""; artist = ""; message = "Track added to your local library."
                        } else message = "Enter both a song title and artist."
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Add to library") }
            }
        }

        Text("Backup & restore", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { jsonExport.launch("musicbridge-backup.json") }, modifier = Modifier.weight(1f)) { Text("Export JSON") }
            OutlinedButton(onClick = { csvExport.launch("musicbridge-playlist.csv") }, modifier = Modifier.weight(1f)) { Text("Export CSV") }
        }
        OutlinedButton(
            onClick = { jsonImport.launch(arrayOf("application/json", "text/*", "application/octet-stream")) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Import JSON backup") }

        Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        Divider()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Local tracks", style = MaterialTheme.typography.titleLarge)
            Text("${tracks.size} songs", style = MaterialTheme.typography.titleMedium)
        }
        if (tracks.isEmpty()) {
            Text("Your library is empty. Add a track or import a MusicBridge JSON backup.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(tracks) { index, track ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(track.title, style = MaterialTheme.typography.titleSmall)
                                Text(track.artist, style = MaterialTheme.typography.bodyMedium)
                                if (track.album.isNotBlank()) Text(track.album, style = MaterialTheme.typography.bodySmall)
                            }
                            OutlinedButton(onClick = {
                                tracks = tracks.filterIndexed { i, _ -> i != index }
                                onSave(tracks)
                                message = "Track removed."
                            }) { Text("Remove") }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text("Streaming-service connections are planned; this initial version does not yet transfer directly to Spotify or YouTube Music.", style = MaterialTheme.typography.bodySmall)
    }
}

private fun tracksToJson(tracks: List<Track>): String {
    val root = JSONObject()
    root.put("format", "musicbridge-playlist")
    root.put("version", 1)
    val songs = JSONArray()
    tracks.forEach { track ->
        songs.put(JSONObject().put("title", track.title).put("artist", track.artist).put("album", track.album))
    }
    root.put("tracks", songs)
    return root.toString(2)
}

private fun jsonToTracks(text: String): List<Track> {
    val root = JSONObject(text)
    require(root.optString("format") == "musicbridge-playlist") { "Not a MusicBridge backup" }
    val songs = root.getJSONArray("tracks")
    return (0 until songs.length()).map { index ->
        val song = songs.getJSONObject(index)
        Track(song.optString("title"), song.optString("artist"), song.optString("album"))
    }
}

private fun tracksToCsv(tracks: List<Track>): String {
    fun escape(value: String) = "\"" + value.replace("\"", "\"\"") + "\""
    return buildString {
        appendLine("title,artist,album")
        tracks.forEach { appendLine("${escape(it.title)},${escape(it.artist)},${escape(it.album)}") }
    }
}

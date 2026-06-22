package engineer.zamora.bufferlessvideoplayer.player

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.LoadEventInfo
import androidx.media3.exoplayer.source.MediaLoadData

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlayerDebugger : Player.Listener, AnalyticsListener {
    private val TAG = "PlayerDebugger"
    private val timeFormatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()
    
    private fun log(message: String) {
        val timestamp = timeFormatter.format(Date())
        val formattedMessage = "[$timestamp] $message"
        Log.d(TAG, message)
        val currentLogs = _logs.value.toMutableList()
        currentLogs.add(0, formattedMessage) // Newest logs at the top
        if (currentLogs.size > 100) {
            currentLogs.removeAt(currentLogs.size - 1)
        }
        _logs.value = currentLogs
    }

    fun startDebugging(exoPlayer: ExoPlayer) {
        log("Debugging started")
        // 1. Monitor Tracks, Codecs, and Resolutions
        exoPlayer.addListener(this)

        // 2. Monitor Segment Downloads
        exoPlayer.addAnalyticsListener(this)
    }

    override fun onTracksChanged(tracks: Tracks) {
        log("Tracks Changed. Groups: ${tracks.groups.size}")
        tracks.groups.forEachIndexed { index, group ->
            val type = when (group.type) {
                C.TRACK_TYPE_AUDIO -> "Audio"
                C.TRACK_TYPE_TEXT -> "Text"
                C.TRACK_TYPE_VIDEO -> "Video"
                else -> "Other (${group.type})"
            }
            for (i in 0 until group.length) {
                val format: Format = group.getTrackFormat(i)
                val isSelected = group.isTrackSelected(i)
                log("  $type track $i: ${format.width}x${format.height}, ${format.bitrate}bps, ${format.sampleMimeType}, selected=$isSelected")
            }
        }
    }

    override fun onEvents(player: Player, events: Player.Events) {
        if (events.contains(Player.EVENT_TIMELINE_CHANGED)) {
            val manifest = player.currentManifest
            if (manifest != null) {
                log("Manifest loaded")
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onLoadStarted(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData
    ) {
        log("Download Started: ${loadEventInfo.uri.lastPathSegment ?: "unknown"}")
    }

    @OptIn(UnstableApi::class)
    override fun onLoadCompleted(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData
    ) {
        log("Download Completed: ${loadEventInfo.uri.lastPathSegment ?: "unknown"} (${loadEventInfo.bytesLoaded} bytes in ${loadEventInfo.loadDurationMs}ms)")
    }
}

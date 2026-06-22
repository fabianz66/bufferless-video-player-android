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

class PlayerDebugger : Player.Listener, AnalyticsListener {
    private val TAG = "PlayerDebugger"

    fun startDebugging(exoPlayer: ExoPlayer) {

        // 1. Monitor Tracks, Codecs, and Resolutions
        exoPlayer.addListener(this)

        // 2. Monitor Segment Downloads
        exoPlayer.addAnalyticsListener(this)
    }

    override fun onTracksChanged(tracks: Tracks) {
        Log.d(TAG, "Tracks Changed. Available Groups: ${tracks.groups.size}")
        tracks.groups.forEachIndexed { index, group ->
            val type = when (group.type) {
                C.TRACK_TYPE_AUDIO -> "Audio"
                C.TRACK_TYPE_TEXT -> "Text"
                C.TRACK_TYPE_VIDEO -> "Video"
                else -> "Other (${group.type})"
            }
            Log.d(TAG, "Group $index ($type):")
            for (i in 0 until group.length) {
                val format: Format = group.getTrackFormat(i)
                val isSelected = group.isTrackSelected(i)
                Log.d(
                    TAG, "  Track $i: ${format.width}x${format.height}, " +
                            "Bitrate: ${format.bitrate}, Codec: ${format.sampleMimeType}, " +
                            "Selected: $isSelected"
                )
            }
        }
    }

    override fun onEvents(player: Player, events: Player.Events) {
        if (events.contains(Player.EVENT_TIMELINE_CHANGED)) {
            val manifest = player.currentManifest
            if (manifest != null) {
                Log.d(TAG, "Manifest loaded: $manifest")
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onLoadStarted(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData
    ) {
        Log.d(TAG, "Download Started: ${loadEventInfo.uri}")
    }

    @OptIn(UnstableApi::class)
    override fun onLoadCompleted(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData
    ) {
        Log.d(
            TAG, "Download Completed: ${loadEventInfo.uri}, " +
                    "Bytes: ${loadEventInfo.bytesLoaded}, " +
                    "Duration: ${loadEventInfo.loadDurationMs}ms"
        )
    }
}
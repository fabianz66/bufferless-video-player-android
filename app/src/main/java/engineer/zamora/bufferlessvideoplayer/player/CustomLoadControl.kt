package engineer.zamora.bufferlessvideoplayer.player

import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.exoplayer.trackselection.ExoTrackSelection
import androidx.media3.exoplayer.upstream.Allocator
import androidx.media3.exoplayer.upstream.DefaultAllocator

@UnstableApi
class CustomLoadControl : LoadControl {
    private val allocator = DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE)

    @Deprecated("Use onPrepared(PlayerId) instead")
    override fun onPrepared() {
        Log.d("CustomLoadControl", "onPrepared")
    }

    @Deprecated("Use onTracksSelected(PlayerId, Timeline, MediaPeriodId, Array<out Renderer>, TrackGroupArray, Array<out ExoTrackSelection?>) instead")
    override fun onTracksSelected(
        renderers: Array<out Renderer>,
        trackGroups: TrackGroupArray,
        trackSelections: Array<out ExoTrackSelection?>
    ) {
        Log.d("CustomLoadControl", "onTracksSelected")
    }

    override fun onStopped() {
        Log.d("CustomLoadControl", "onStopped")
    }

    override fun onReleased() {
        Log.d("CustomLoadControl", "onReleased")
    }

    override fun getAllocator(): Allocator = allocator

    override fun getBackBufferDurationUs(): Long = 0

    override fun retainBackBufferFromKeyframe(): Boolean = false

    override fun shouldContinueLoading(
        playbackPositionUs: Long,
        bufferedDurationUs: Long,
        playbackSpeed: Float
    ): Boolean {
        // Logic to decide if we should keep downloading segments
        val shouldContinue = bufferedDurationUs < 50_000_000 // 50 seconds
        return shouldContinue
    }

    override fun shouldStartPlayback(
        timeline: Timeline,
        periodId: androidx.media3.exoplayer.source.MediaSource.MediaPeriodId,
        bufferedDurationUs: Long,
        playbackSpeed: Float,
        rebuffering: Boolean,
        targetLiveOffsetUs: Long
    ): Boolean {
        // Logic to decide if we have enough buffer to start/resume playback
        return bufferedDurationUs > 2_000_000 // 2 seconds
    }
}
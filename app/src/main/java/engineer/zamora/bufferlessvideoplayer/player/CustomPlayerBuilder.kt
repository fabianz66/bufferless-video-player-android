package engineer.zamora.bufferlessvideoplayer.player

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter

@UnstableApi
class CustomPlayerBuilder(
    private val context: Context,
    private val logger: CustomLogger
) {

    private val bandwidthMeter = DefaultBandwidthMeter.getSingletonInstance(context)

    // 1. Create the ABR Factory
    private val trackSelectionFactory = CustomTrackSelectionFactory(bandwidthMeter, logger)

    // 2. Create the Track Selector using our custom ABR factory
    private val trackSelector = CustomTrackSelector(context, trackSelectionFactory)

    // 3. Configure Default Load Control
    private val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            5_000, // minBufferMs (The minimum duration of media that the player will attempt to ensure is buffered)
            10_000, // maxBufferMs (The maximum duration of media that the player will attempt to buffer)
            500,  // bufferForPlaybackMs (The duration of media that must be buffered for playback to start or resume after a user action)
            500   // bufferForPlaybackAfterRebufferMs (The duration of media that must be buffered for playback to resume after a rebuffer)
        )
        .build()

    // 4. Create the Debugger
    val playerDebugger = PlayerDebugger(logger)

    fun build(): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .setBandwidthMeter(bandwidthMeter)
            .build()
            .apply {
                playerDebugger.startDebugging(this)
            }
    }
}

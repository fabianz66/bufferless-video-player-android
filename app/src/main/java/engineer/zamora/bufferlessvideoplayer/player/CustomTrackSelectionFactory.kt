package engineer.zamora.bufferlessvideoplayer.player

import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.trackselection.ExoTrackSelection
import androidx.media3.exoplayer.upstream.BandwidthMeter

@UnstableApi
class CustomTrackSelectionFactory(
    private val bandwidthMeter: BandwidthMeter,
    private val logger: CustomLogger? = null
) : ExoTrackSelection.Factory {
    override fun createTrackSelections(
        definitions: Array<out ExoTrackSelection.Definition?>,
        bandwidthMeter: BandwidthMeter,
        periodId: MediaSource.MediaPeriodId,
        timeline: Timeline
    ): Array<ExoTrackSelection?> {
        return definitions.map { definition ->
            if (definition != null) {
                CustomTrackSelection(definition.group, definition.tracks, bandwidthMeter, logger)
            } else null
        }.toTypedArray()
    }
}
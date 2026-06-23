package engineer.zamora.bufferlessvideoplayer.player

import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.TrackGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.chunk.MediaChunk
import androidx.media3.exoplayer.source.chunk.MediaChunkIterator
import androidx.media3.exoplayer.trackselection.BaseTrackSelection
import androidx.media3.exoplayer.upstream.BandwidthMeter

/**
 * This class contains the ABR logic.
 */
@UnstableApi
class CustomTrackSelection(
    group: TrackGroup,
    tracks: IntArray,
    private val bandwidthMeter: BandwidthMeter,
    private val logger: CustomLogger? = null
) : BaseTrackSelection(group, tracks, group.type) {

    private var selectedIndex = 0
    private var selectionReason = C.SELECTION_REASON_INITIAL

    override fun updateSelectedTrack(
        playbackPositionUs: Long,
        bufferedDurationUs: Long,
        availableDurationUs: Long,
        queue: List<MediaChunk>,
        mediaChunkIterators: Array<out MediaChunkIterator>
    ) {
        // --- THIS IS THE BRAIN OF THE ABR ---


        // Inputs:
        // Buffer health.
        // Device constraints (available memory).
        // Bandwidth.
        //
        // The algorithm:
        //
        // healthy_buffer = Do I have a healthy buffer (e.g. >70%)?
        // low_buffer = Do I have a low buffer (e.g. <30%)?
        // currentBandwidth = sliding window average.
        // available_memory = device's available memory, don't want to run OOM.
        // low_memory = available_memory less than 40% ?
        //
        // NOTE: Are memory constraints responsibility of the loadControl instead?
        //
        // First check if we need to scale down:
        // Are we low on buffer? Are we low on memory?
        // Pick a new format.
        //
        // Can we scale up?
        // Is our buffer healthy? Is memory healthy?
        // Lets pick the highest available format for the available bw.
        //
        // Should we ever pick a format with higher bitrate than available bw?
        //
        // On the load control side:
        // Does load control consider available memory?
        // Can load control estimate memory per downloaded chunk?

        val currentBandwidth = bandwidthMeter.bitrateEstimate

        // For debugging lets create a list with all formats.
        val formats = mutableListOf<Format>()
        for (i in 0 until length) {
            formats.add(getFormat(i))
        }
        logger?.log("ABR Formats: $formats")

        // Exclude formats with higher bitrate than current bandwidth.
        // Use <= to include formats matching the bandwidth estimate.
        val formatsLowerBps = formats.filter { it.bitrate <= currentBandwidth }
        logger?.log("ABR Formats With BW: $formatsLowerBps")

        // Sort DESC by pixel count and then DESC by bitrate to get best quality.
        val sortedFormats = formatsLowerBps.sortedWith(
            compareByDescending<Format> { it.pixelCount }
                .thenByDescending { it.bitrate }
        )
        logger?.log("ABR Sorted Formats: $sortedFormats")

        // Fallback to the lowest bitrate format if no formats are below bandwidth estimate.
        val selectedFormat = sortedFormats.firstOrNull() ?: formats.minBy { it.bitrate }
        selectedIndex = indexOf(selectedFormat)
        selectionReason = C.SELECTION_REASON_ADAPTIVE

        logger?.log("ABR Selected Format: $selectedFormat")
    }

    override fun getSelectedIndex(): Int = selectedIndex
    override fun getSelectionReason(): Int = selectionReason
    override fun getSelectionData(): Any? = null
}

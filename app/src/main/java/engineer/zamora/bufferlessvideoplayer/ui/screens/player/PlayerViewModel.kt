package engineer.zamora.bufferlessvideoplayer.ui.screens.player

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import engineer.zamora.bufferlessvideoplayer.player.CustomLoadControl
import engineer.zamora.bufferlessvideoplayer.player.CustomTrackSelector
import engineer.zamora.bufferlessvideoplayer.player.PlayerDebugger

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    // Use Custom Components
    @OptIn(UnstableApi::class)
    private val customTrackSelector = CustomTrackSelector(application)

    @OptIn(UnstableApi::class)
    private val customLoadControl = CustomLoadControl()

    // Player debugger
    val playerDebugger = PlayerDebugger()

    // The player instance is created once and kept here
    @OptIn(UnstableApi::class)
    val player = ExoPlayer.Builder(application)
        .setTrackSelector(customTrackSelector)
//        .setLoadControl(customLoadControl)
        .build()
        .apply {
            // Use 'this' to refer to the player instance being built
            playerDebugger.startDebugging(this)
        }

    fun playVideo(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    override fun onCleared() {
        super.onCleared()
        // This is called when the user FINISHED with the screen (not on rotation)
        player.release()
    }
}

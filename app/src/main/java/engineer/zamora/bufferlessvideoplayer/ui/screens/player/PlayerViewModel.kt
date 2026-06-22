package engineer.zamora.bufferlessvideoplayer.ui.screens.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    // The player instance is created once and kept here
    val player = ExoPlayer.Builder(application).build()

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
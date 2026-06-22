package engineer.zamora.bufferlessvideoplayer.ui.screens.player

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import engineer.zamora.bufferlessvideoplayer.player.CustomLogger
import engineer.zamora.bufferlessvideoplayer.player.CustomLoggerImpl
import engineer.zamora.bufferlessvideoplayer.player.CustomPlayerBuilder

@UnstableApi
class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    @OptIn(UnstableApi::class)
    val customLogger: CustomLogger = CustomLoggerImpl("VideoPlayer")

    @OptIn(UnstableApi::class)
    private val playerBuilder = CustomPlayerBuilder(application, customLogger)

    @OptIn(UnstableApi::class)
    val playerDebugger = playerBuilder.playerDebugger

    @OptIn(UnstableApi::class)
    val player = playerBuilder.build()

    fun playVideo(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // This is called when the user FINISHED with the screen (not on rotation)
        player.release()
    }
}

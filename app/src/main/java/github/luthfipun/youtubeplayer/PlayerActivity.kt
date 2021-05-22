package github.luthfipun.youtubeplayer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import github.luthfipun.youtubeplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    companion object {
        private const val DATA = "DATA"
        fun createIntent(context: Context, videoURL: String): Intent {
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(DATA, videoURL)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        intent.getStringExtra(DATA).also {

            val mediaItem = MediaItem.fromUri(it.orEmpty())
            val player = SimpleExoPlayer.Builder(this)
                .build()

            binding.playerView.apply {
                this.player = player
            }

            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }
}
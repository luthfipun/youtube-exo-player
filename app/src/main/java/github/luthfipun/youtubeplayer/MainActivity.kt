package github.luthfipun.youtubeplayer

import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.kotvertolet.youtubejextractor.JExtractorCallback
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import com.github.kotvertolet.youtubejextractor.models.newModels.VideoPlayerConfig
import github.luthfipun.youtubeplayer.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var videoURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch {
            fetchYt()
        }

        binding.btnPlay.setOnClickListener {
            startActivity(
                PlayerActivity.createIntent(this, videoURL.orEmpty())
            )
        }
    }

    private suspend fun fetchYt(){

        withContext(Dispatchers.IO){

            val youtubeJExtractor = YoutubeJExtractor()
            try {
                youtubeJExtractor.extract("X-k73JzPlCk", object : JExtractorCallback {
                    override fun onSuccess(videoData: VideoPlayerConfig?) {
                        val data = videoData?.streamingData

                        data?.adaptiveFormats?.map {
                            Log.e("ENOG", "ADAPTIVE => ${it.url}")
                            Log.e("ENOG", "SIZE => ${it.quality}")

                            if (it.quality.equals("medium")){
                                videoURL = it.url

                                launch(Dispatchers.Main){
                                    binding.btnPlay.isEnabled = true
                                }
                            }
                        }

                        data?.muxedStreams?.map {
                            Log.e("ENOG", "MUXED => ${it.url}")
                        }

                        Log.e("ENOG", "DASH => ${data?.dashManifestUrl}")
                        Log.e("ENOG", "HLS => ${data?.hlsManifestUrl}")
                        Log.e("ENOG", "HLS => ${data?.adaptiveVideoStreams}")
                    }

                    override fun onNetworkException(e: YoutubeRequestException?) {
                        Log.e("ENOG", "ERROR Network => ${e?.localizedMessage}")
                    }

                    override fun onError(exception: Exception?) {
                        Log.e("ENOG", "Error Exception => ${exception?.localizedMessage}")
                    }

                })
            }
            catch (e: ExtractionException){
                Log.e("ENOG", "Error Exception => ${e?.localizedMessage}")
            }
            catch (e: YoutubeRequestException){
                Log.e("ENOG", "Error Exception => ${e?.localizedMessage}")
            }
            catch (e: NetworkOnMainThreadException){
                e.printStackTrace()
                Log.e("ENOG", "Error Exception => ${e.message}")
            }
        }

    }
}
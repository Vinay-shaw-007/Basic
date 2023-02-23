package com.example.basic.ui

import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.navigation.navArgs
import com.example.basic.databinding.ActivityVideoPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.FileDataSource


class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding

    private val argument by navArgs<VideoPlayerActivityArgs>()

    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.argb(128, 0, 0, 0)

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializePlayer(argument.videoUri)
        Log.d("videoUri", argument.videoUri)

    }

    private fun initializePlayer(uri: String) {
        player = ExoPlayer.Builder(applicationContext).build()
        binding.playerView.player = player
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }


    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
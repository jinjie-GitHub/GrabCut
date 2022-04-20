package com.spx.library

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import android.view.View
import java.util.*

class ThumbExoPlayerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    companion object {
        const val CHECK_INTERVAL_MS: Long = 0
        const val TAG = "ThumbExoPlayerView"
    }

    private lateinit var mediaPath: String
    private lateinit var textureView: TextureView
    private var callback: ((Bitmap, Int) -> Boolean)? = null
    private var bitmapIndex = 0
    private val thumbnailMillSecList = ArrayList<Long>()

    //private var exoPlayer: SimpleExoPlayer? = null
    /*private var listener: Player.DefaultEventListener = object : Player.DefaultEventListener() {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Log.d(TAG, "player state $playbackState")
        }
    }*/

    fun setDataSource(source: String, millsecsPerFrame: Int, thubnailCount: Int, callback: (Bitmap, Int) -> Boolean) {
        this.mediaPath = source
        this.callback = callback
        /*exoPlayer = com.spx.library.player.initPlayer(context, mediaPath, this, listener)
        exoPlayer?.volume = 0f
        exoPlayer!!.repeatMode = Player.REPEAT_MODE_OFF
        exoPlayer!!.playWhenReady = false
        val param = PlaybackParameters(20f)
        player.setPlaybackParameters(param)*/

        Thread {
            var duration = getVideoDuration(context, mediaPath)

            var millSec = 0L
            var mMMR = MediaMetadataRetriever()
            mMMR.setDataSource(mediaPath)

            for (i in 0 until thubnailCount) {
                if (millSec > duration) {
                    millSec = duration
                }
                thumbnailMillSecList.add(millSec)
                Log.d(TAG, "getThumbnail()  [$i] time:$millSec")

                val srcFrame = mMMR.getFrameAtTime(millSec) //原图
                val frame = Bitmap.createScaledBitmap(srcFrame, srcFrame.width / 8, srcFrame.height / 8, false)
                post {
                    callback?.invoke(frame, i)
                }
                srcFrame.recycle()
                millSec += millsecsPerFrame.toLong()
            }
            mMMR.release()
        }.start()
    }

    /*private fun startPlayAndCapture() {

        if (thumbnailMillSecList.size == 0) {
            return
        }

        val timeMs = thumbnailMillSecList.get(0)
        //Log.d(TAG, "startPlayAndCapture()  current position:${exoPlayer!!.currentPosition}, want timems:$timeMs")
        if (exoPlayer!!.currentPosition > timeMs) {
            exoPlayer?.playWhenReady = false
            val bitmap = textureView.bitmap
            //Log.d(TAG, "startPlayAndCapture()  bitmap:$bitmap")
            bitmap?.run {
                var fileName = context.externalCacheDir.absolutePath+"thumbnail_" + bitmapIndex
                writeToFile(bitmap, fileName)
                //callback?.invoke(fileName, bitmapIndex++)
                thumbnailMillSecList.removeAt(0)
            }
        }
        exoPlayer?.playWhenReady = true
        postDelayed({ startPlayAndCapture() }, CHECK_INTERVAL_MS)
    }*/
}
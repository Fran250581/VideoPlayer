package com.example.fran.videoplayer

import android.app.Activity
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

class MainActivity : Activity(), MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, SurfaceHolder.Callback, View.OnClickListener {

    private var mediaPlayer: MediaPlayer? = null
    private var surfaceView: SurfaceView? = null
    private var surfaceHolder: SurfaceHolder? = null
    private var editText: EditText? = null
    private var bPlay: ImageButton? = null
    private var bPause: ImageButton? = null
    private var bStop: ImageButton? = null
    private var bLog: ImageButton? = null
    private var logTextView: TextView? = null
    private var pause: Boolean = false
    private var stop: Boolean = false
    private var path: String? = null
    private var savePos = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        surfaceView = findViewById<View>(R.id.surfaceView) as SurfaceView?
        surfaceHolder = surfaceView!!.holder
        surfaceHolder!!.addCallback(this)
        // obsoleto, pero neceario para versiones de Android
        // anteriores a la 3.0
        surfaceHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        editText = findViewById<View>(R.id.path) as EditText?
        editText!!.setText("http://campus.somtic.net/android/video1617.mp4")
        logTextView = findViewById<View>(R.id.Log) as TextView?
        bPlay = findViewById<View>(R.id.play) as ImageButton?
        bPlay!!.setOnClickListener(this)
        bPause = findViewById<View>(R.id.pause) as ImageButton?
        bPause!!.setOnClickListener(this)
        bStop = findViewById<View>(R.id.stop) as ImageButton?
        bStop!!.setOnClickListener(this)
        bLog = findViewById<View>(R.id.logButton) as ImageButton?
        bLog!!.setOnClickListener(this)
        log("")
    }

    private fun log(s: String) {
        logTextView!!.append(s + "\n")
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.play -> if (mediaPlayer != null) {
                if (pause) {
                    mediaPlayer!!.start()
                } else {
                    playVideo()
                }
            }
            R.id.pause -> if (mediaPlayer != null) {
                pause = true
                mediaPlayer!!.pause()
            }

            R.id.stop -> if (mediaPlayer != null) {
                pause = false
                mediaPlayer!!.stop()
                savePos = 0
                stop = true
            }
            R.id.logButton -> if (logTextView!!.visibility == TextView.VISIBLE) {
                logTextView!!.visibility = TextView.INVISIBLE
            } else {
                logTextView!!.visibility = TextView.VISIBLE
            }
        }
    }

    private fun playVideo() {
        try {
            pause = false
            path = editText!!.getText().toString()
            if (mediaPlayer == null)
                mediaPlayer = MediaPlayer()
            if (stop)
                mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(path)
            mediaPlayer!!.setDisplay(surfaceHolder)
            mediaPlayer!!.prepare()
            // mMediaPlayer.prepareAsync(); Para streaming
            mediaPlayer!!.setOnBufferingUpdateListener(this)
            mediaPlayer!!.setOnCompletionListener(this)
            mediaPlayer!!.setOnPreparedListener(this)
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.seekTo(savePos)
            stop = false
        }
        catch (e: Exception) {
            log("ERROR: " + e.message)
        }
    }

    override fun onBufferingUpdate(arg0: MediaPlayer, percent: Int) {
        log("onBufferingUpdate porcentaje:" + percent)
    }

    override fun onCompletion(arg0: MediaPlayer) {
        log("llamada a onCompletion")
    }

    override fun onPrepared(mp: MediaPlayer) {
        log("llamada a onPrepared")
        val mVideoWidth = mediaPlayer!!.getVideoWidth()
        val mVideoHeight = mediaPlayer!!.getVideoHeight()
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            surfaceHolder!!.setFixedSize(mVideoWidth, mVideoHeight)
            mediaPlayer!!.start()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        log("llamada a surfaceCreated")
        playVideo()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        log("llamada a surfaceChanged")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        log("llamada a surfaceDestroyed")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    public override fun onPause() {
        super.onPause()
        if (mediaPlayer != null && !pause) {
            mediaPlayer!!.pause()
        }
    }

    public override fun onResume() {
        super.onResume()
        if (mediaPlayer != null && !pause) {
            mediaPlayer!!.start()
        }
    }

    override fun onSaveInstanceState(estado: Bundle) {
        super.onSaveInstanceState(estado)
        if (mediaPlayer != null) {
            val pos = mediaPlayer!!.getCurrentPosition()
            estado.putString("ruta", path)
            estado.putInt("posicion", pos)
        }
    }

    override fun onRestoreInstanceState(estado: Bundle?) {
        super.onRestoreInstanceState(estado)
        if (estado != null) {
            path = estado.getString("ruta")!!.toString()
            savePos = estado.getInt("posicion")
        }
    }
}

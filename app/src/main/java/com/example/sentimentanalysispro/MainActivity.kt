package com.example.sentimentanalysispro

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var buttonSpeak: Button? = null
    private var editText: EditText? = null

    lateinit var possitive_words : ArrayList<String>
    lateinit var negative_words : ArrayList<String>
    lateinit var for_happy : ArrayList<String>
    lateinit var for_sad : ArrayList<String>
    lateinit var for_neutral : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSpeak = this.button_speak
        editText = this.edittext_input

        buttonSpeak!!.isEnabled = false;
        tts = TextToSpeech(this, this)

        buttonSpeak!!.setOnClickListener { speakOut() }

        possitive_words = getFile("possitive.txt").split("\n") as ArrayList<String>
        negative_words = getFile("negative.txt").split("\n") as ArrayList<String>
        for_happy = getFile("for_happy.txt").split("\n") as ArrayList<String>
        for_sad = getFile("for_sad.txt").split("\n") as ArrayList<String>
        for_neutral = getFile("for_neutral.txt").split("\n") as ArrayList<String>

        Log.d("FILE", negative_words?.joinToString(","))
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                buttonSpeak!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }

    }

    private fun speakOut() {
        val text = editText!!.text.toString()
        var to_speak = ""

        var state = countWords(text)
        if (state > 0) {
            to_speak = "You look happy! I recommend you some ${for_happy[(0..for_happy.size).random()]}"
        }
        else if (state < 0) {
            to_speak = "I feel like you are sad... I recommend you some ${for_sad[(0..for_sad.size).random()]}"
        }
        else {
            to_speak = "You seem to be OK. I recommend you some ${for_neutral[(0..for_neutral.size).random()]}"
        }

        tts!!.speak(to_speak, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    private fun countWords(text : String) : Int {
        var counter = 0

        text.split(" ").forEach {
            if (possitive_words!!.contains((it))) {
                counter++
            }
            else if (negative_words!!.contains(it)) {
                counter--
            }
        }

        return counter
    }

    private fun getFile(assetf: String): String {
        var file = ""
        try {
            val `is` = assets.open(assetf)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            file = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}
package com.example.a7minworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minworkout.databinding.ActivityExerciseBinding
import com.example.a7minworkout.databinding.DialogueCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding :ActivityExerciseBinding? = null

    private var restTimer : CountDownTimer? = null
    private var restProgress = 0
    private var restTimeDuration: Long = 3

    private var exerciseTimer : CountDownTimer?=null
    private var exerciseProgress =0
    private var exerciseTimeDuration: Long = 5

    private var exerciseList : ArrayList<ExerciseModel>?=null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null
    private var player : MediaPlayer? = null

    private var exerciseAdapter : ExerciseStatusAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener{
            customDialogForBackButton()

        }


        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this,this)


        setUpRestView()
        setupExerciseStatusRecyclerview()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
       // super.onBackPressed()
    }
    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)
        val dialogBinding = DialogueCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener{
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun setupExerciseStatusRecyclerview(){
        binding?.rvExerciseStatus?.layoutManager=
            LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter

    }

    private fun setUpRestView(){

        try {
            val soundURI = Uri.parse("android.resource://com.example.a7minworkout/"+R.raw.press_start)
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping=false
            player?.start()


        }catch (e: Exception){
            e.printStackTrace()
        }

        binding?.flRestView?.visibility= View.VISIBLE
        binding?.tvTitle?.visibility=View.VISIBLE
        binding?.upcoming?.visibility=View.VISIBLE
        binding?.upComingExercise?.visibility=View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.upComingExercise?.text = exerciseList!![currentExercisePosition+1].getName()
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        setRestProgressbar()
    }
    private fun setUpExerciseView(){
        binding?.flRestView?.visibility= View.INVISIBLE
        binding?.tvTitle?.visibility=View.INVISIBLE
        binding?.upcoming?.visibility=View.INVISIBLE
        binding?.upComingExercise?.visibility=View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        if(exerciseTimer !=null){
            exerciseTimer?.cancel()
            exerciseProgress=0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text=exerciseList!![currentExercisePosition].getName()
        setUpExerciseProgressBar()
    }

    private fun setRestProgressbar(){
        binding?.progressBar?.progress=restProgress

        restTimer = object : CountDownTimer(restTimeDuration*1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress=restTimeDuration.toInt()-restProgress
                binding?.tvTimer?.text=(restTimeDuration.toInt()-restProgress).toString()
                binding?.progressBar?.max=restTimeDuration.toInt()
            }

            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setUpExerciseView()
            }
        }.start()
    }

    private fun setUpExerciseProgressBar(){
        binding?.progressBarExercise?.progress= exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimeDuration*1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress=exerciseTimeDuration.toInt()-exerciseProgress
                binding?.tvTimerExercise?.text=(exerciseTimeDuration.toInt()-exerciseProgress).toString()
                binding?.progressBarExercise?.max=exerciseTimeDuration.toInt()
            }

            override fun onFinish() {
                if(currentExercisePosition<exerciseList?.size!!-1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setUpRestView()
                }else{
                    finish()
                Toast.makeText(this@ExerciseActivity,"congrats you have completed 7 minutes exercise ",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                startActivity(intent)
                }
            }
        }.start()
    }



    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if(exerciseTimer !=null){
            exerciseTimer?.cancel()
            exerciseProgress=0
        }
        if (tts!=null){
            tts?.stop()
            tts?.shutdown()
        }
        if(player!=null){
            player?.stop()
        }
        binding = null
    }


    override fun onInit(status: Int) {
        if(status==TextToSpeech.SUCCESS){
            val result = tts?.setLanguage(Locale.US)
            if(result==TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","language not supported")
            }
        }else{
            Log.e("TTS","initialization failed")
        }
    }
    private fun speakOut(text: String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
}
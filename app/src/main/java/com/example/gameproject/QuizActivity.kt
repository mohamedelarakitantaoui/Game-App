package com.example.gameproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Contacts.Intents
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gameproject.databinding.ActivityQuizBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class QuizActivity : AppCompatActivity() {

    private lateinit var quizBinding: ActivityQuizBinding

    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference.child("question")

    var question = ""
    var answerA = ""
    var answerB = ""
    var answerC = ""
    var answerD = ""
    var correctAnswer = ""

    var questionCount = 0
    var questionNumber = 1

    var userAnswer = ""
    var userCorrect = 0
    var userWrong = 0

    lateinit var timer: CountDownTimer
    private val totalTime = 15000L
    var timerContinue = false
    var leftTime = totalTime

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val scoreRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(quizBinding.root)
        gameLogic()

        // Listener for the Next Button
        quizBinding.buttonNext.setOnClickListener {
            resetTimer()
            gameLogic()
        }

        // Listener for the Finish Button
        quizBinding.buttonFinish.setOnClickListener {
            sendScore()
        }

        // Listener for Option A
        quizBinding.textViewA.setOnClickListener {
            handleOptionClick("a", quizBinding.textViewA)
        }

        // Listener for Option B
        quizBinding.textViewB.setOnClickListener {
            handleOptionClick("b", quizBinding.textViewB)
        }

        // Listener for Option C
        quizBinding.textViewC.setOnClickListener {
            handleOptionClick("c", quizBinding.textViewC)
        }

        // Listener for Option D
        quizBinding.textViewD.setOnClickListener {
            handleOptionClick("d", quizBinding.textViewD)
        }

        // Removed disableClickOfOption()
    }

    private fun handleOptionClick(selectedOption: String, textView: TextView) {
        pauseTimer()
        userAnswer = selectedOption
        if (correctAnswer.equals(userAnswer, ignoreCase = true)) {
            textView.setBackgroundColor(Color.GREEN)
            userCorrect++
            quizBinding.textViewCorrect.text = userCorrect.toString()
        } else {
            textView.setBackgroundColor(Color.RED)
            userWrong++
            quizBinding.textViewWrong.text = userWrong.toString()
            findAnswer()
        }
        disableClickOfOption()
    }

    private fun gameLogic() {
        restoreOptions()
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                questionCount = snapshot.childrenCount.toInt()
                Log.d("QuizActivity", "Total questions: $questionCount")

                if (questionNumber <= questionCount) {
                    val questionSnapshot = snapshot.child(questionNumber.toString())
                    if (!questionSnapshot.exists()) {
                        Log.e("QuizActivity", "Question $questionNumber does not exist.")
                        Toast.makeText(applicationContext, "Question not found.", Toast.LENGTH_SHORT).show()
                        return
                    }

                    question = questionSnapshot.child("q").getValue(String::class.java) ?: ""
                    answerA = questionSnapshot.child("a").getValue(String::class.java) ?: ""
                    answerB = questionSnapshot.child("b").getValue(String::class.java) ?: ""
                    answerC = questionSnapshot.child("c").getValue(String::class.java) ?: ""
                    answerD = questionSnapshot.child("d").getValue(String::class.java) ?: ""
                    correctAnswer = questionSnapshot.child("answer").getValue(String::class.java) ?: ""

                    quizBinding.textViewQuestion.text = question
                    quizBinding.textViewA.text = answerA
                    quizBinding.textViewB.text = answerB
                    quizBinding.textViewC.text = answerC
                    quizBinding.textViewD.text = answerD

                    quizBinding.progressBarQuiz.visibility = View.INVISIBLE
                    quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                    quizBinding.linearLayoutQuestion.visibility = View.VISIBLE
                    quizBinding.linearLayoutButtons.visibility = View.VISIBLE

                    if (questionNumber == 1) {
                        startTime() // Start timer for the first question
                    } else {
                        resetTimer()
                        startTime() // Start timer for subsequent questions
                    }

                    Log.d("QuizActivity", "Loaded question $questionNumber: $question")
                    questionNumber++ // Increment question number after loading the question
                } else {

                    val dialogMessage = AlertDialog.Builder(this@QuizActivity)
                    dialogMessage.setTitle("Quiz Game")
                    dialogMessage.setMessage("you want to see the result?")
                    dialogMessage.setCancelable(false)
                    dialogMessage.setPositiveButton("See Result"){dialogWindow, position ->

                        sendScore()


                    }
                    dialogMessage.setNeutralButton("Play again"){dialogWindow, position ->
                        val intent = Intent(this@QuizActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialogMessage.create().show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("QuizActivity", "Database error: ${error.message}")
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun findAnswer() {
        when (correctAnswer.lowercase(Locale.ROOT)) {
            "a" -> quizBinding.textViewA.setBackgroundColor(Color.GREEN)
            "b" -> quizBinding.textViewB.setBackgroundColor(Color.GREEN)
            "c" -> quizBinding.textViewC.setBackgroundColor(Color.GREEN)
            "d" -> quizBinding.textViewD.setBackgroundColor(Color.GREEN)
        }
    }

    fun disableClickOfOption() {
        quizBinding.textViewA.isClickable = false
        quizBinding.textViewB.isClickable = false
        quizBinding.textViewC.isClickable = false
        quizBinding.textViewD.isClickable = false
    }

    fun restoreOptions() {
        quizBinding.textViewA.setBackgroundColor(Color.WHITE)
        quizBinding.textViewB.setBackgroundColor(Color.WHITE)
        quizBinding.textViewC.setBackgroundColor(Color.WHITE)
        quizBinding.textViewD.setBackgroundColor(Color.WHITE)

        quizBinding.textViewA.isClickable = true
        quizBinding.textViewB.isClickable = true
        quizBinding.textViewC.isClickable = true
        quizBinding.textViewD.isClickable = true
    }

    private fun startTime() {
        timer = object : CountDownTimer(leftTime, 1000) {
            override fun onTick(millisUntilFinish: Long) {
                leftTime = millisUntilFinish
                updateCountDownText()
            }

            override fun onFinish() {
                resetTimer()
                updateCountDownText()
                quizBinding.textViewQuestion.text = "Sorry, time is up"
                disableClickOfOption()
                // Optionally, auto-load the next question or handle timeout
            }
        }.start()
        timerContinue = true
    }

    fun updateCountDownText() {
        val remainingTime: Int = (leftTime / 1000).toInt()
        quizBinding.textViewTime.text = remainingTime.toString()
    }

    fun pauseTimer() {
        timer.cancel()
        timerContinue = false
    }

    fun resetTimer() {
        pauseTimer()
        leftTime = totalTime
        updateCountDownText()
    }
    fun sendScore(){
        user?.let{
            val userID = it.uid
            scoreRef.child("scores").child(userID).child("correct").setValue(userCorrect)
            scoreRef.child("scores").child(userID).child("wrong").setValue(userWrong).addOnSuccessListener {

                Toast.makeText(applicationContext,"socre sent to database", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@QuizActivity,ResultActivity::class.java)
                startActivity(intent)
                finish()

            }
        }


    }
}

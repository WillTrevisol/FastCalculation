package br.edu.scl.ifsp.sdm.fastcalculation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import br.edu.scl.ifsp.sdm.fastcalculation.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    private lateinit var fragmentGameBinding: FragmentGameBinding
    private lateinit var settings: Settings
    private lateinit var calculationGame: CalculationGame
    private var currentRound: CalculationGame.Round? = null
    private var startRoundTime: Long = 0L
    private var totalGameTime: Long = 0L
    private var hits = 0
    private val roundDeadlineHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            totalGameTime += settings.roundInterval
            play()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            settings = it.getParcelable(Extras.EXTRA_SETTINGS) ?: Settings()
        }
        calculationGame = CalculationGame(settings.rounds)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentGameBinding = FragmentGameBinding.inflate(inflater, container, false)
        val onClickListener = View.OnClickListener {
            val value = (it as Button).text.toString().toInt()
            if (value == currentRound?.answer) {
                totalGameTime += System.currentTimeMillis() - startRoundTime
                hits++
            } else {
                totalGameTime += settings.roundInterval
                hits--
            }
            roundDeadlineHandler.removeMessages(MESSAGE_ROUND_DEADLINE)
            play()
        }

        fragmentGameBinding.apply {
            alternativeOneButton.setOnClickListener(onClickListener)
            alternativeTwoButton.setOnClickListener(onClickListener)
            alternativeThreeButton.setOnClickListener(onClickListener)
        }

        play()

        return fragmentGameBinding.root
    }

    companion object {

        private const val MESSAGE_ROUND_DEADLINE = 0
        @JvmStatic
        fun newInstance(settings: Settings) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Extras.EXTRA_SETTINGS, settings)
                }
            }
    }

    private fun play() {
        currentRound = calculationGame.nextRound()
        if (currentRound != null) {
            fragmentGameBinding.apply {
                "Round: ${currentRound!!.round}/${settings.rounds}".also {
                    roundTextView.text = it
                }

                questionTextView.text = currentRound!!.question
                alternativeOneButton.text = currentRound!!.alt1.toString()
                alternativeTwoButton.text = currentRound!!.alt2.toString()
                alternativeThreeButton.text = currentRound!!.alt3.toString()
            }
            startRoundTime = System.currentTimeMillis()
            roundDeadlineHandler.sendEmptyMessageDelayed(MESSAGE_ROUND_DEADLINE, settings.roundInterval)
        } else {
            with(fragmentGameBinding) {
                roundTextView.text = getString(R.string.points)
                val points = hits * 10f / (totalGameTime / 1000.0f)
                "%.2f".format(points).also {
                    questionTextView.text = it
                }
                alternativeOneButton.visibility = View.GONE
                alternativeTwoButton.visibility = View.GONE
                alternativeThreeButton.visibility = View.GONE
            }
        }
    }
}
package br.edu.scl.ifsp.sdm.fastcalculation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import br.edu.scl.ifsp.sdm.fastcalculation.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private lateinit var fragmentResultBinding: FragmentResultBinding
    private lateinit var points: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            points = it.getString(Extras.EXTRA_POINTS, "")
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentResultBinding = FragmentResultBinding.inflate(layoutInflater, container, false)
        fragmentResultBinding.pointsResultTextView.text = points

        fragmentResultBinding.restartGameButton.setOnClickListener {
            (context as OnPlayGame).onPlayGame()
        }

        return fragmentResultBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(points: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(Extras.EXTRA_POINTS, points)
                }
            }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.restartGameMenuItem).isVisible = false
    }
}
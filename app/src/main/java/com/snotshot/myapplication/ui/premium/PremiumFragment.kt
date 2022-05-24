package com.snotshot.myapplication.ui.premium

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.snotshot.myapplication.databinding.FragmentMusicBinding
import com.snotshot.myapplication.databinding.FragmentPremiumBinding
import com.snotshot.myapplication.ui.music.MusicViewModel

class PremiumFragment : Fragment() {

    private lateinit var premiumViewModel: PremiumViewModel
    private var _binding: FragmentPremiumBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        premiumViewModel =
            ViewModelProvider(this).get(PremiumViewModel::class.java)

        _binding = FragmentPremiumBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
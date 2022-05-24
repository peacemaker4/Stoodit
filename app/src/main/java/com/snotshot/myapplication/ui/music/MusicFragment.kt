package com.snotshot.myapplication.ui.music

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.snotshot.myapplication.NewsActivity
import com.snotshot.myapplication.databinding.FragmentHomeBinding
import com.snotshot.myapplication.databinding.FragmentMusicBinding

class MusicFragment : Fragment() {

    private lateinit var musicViewModel: MusicViewModel
    private var _binding: FragmentMusicBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        musicViewModel =
            ViewModelProvider(this).get(MusicViewModel::class.java)

        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
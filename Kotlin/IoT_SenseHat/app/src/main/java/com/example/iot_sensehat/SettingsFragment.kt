package com.example.iot_sensehat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.iot_sensehat.databinding.FragmentSettingsBinding


class SettingsFragment:Fragment() {
    private lateinit var binding:FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentSettingsBinding.inflate(layoutInflater,container,false)

        binding.saveSettingButton.setOnClickListener {
            it.findNavController()
                .navigate(SettingsFragmentDirections
                    .actionFragmentSettingsToFragmentSensorMenu("${binding.serverAdressValue.text}"))
        }
        return binding.root
    }
}
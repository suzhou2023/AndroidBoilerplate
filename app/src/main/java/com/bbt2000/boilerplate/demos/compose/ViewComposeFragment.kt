package com.bbt2000.boilerplate.demos.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.bbt2000.boilerplate.R


class ViewComposeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_view_compose, container, false)
        rootView.findViewById<ComposeView>(R.id.composeView).setContent {
            Text(text = "hello")
        }
        return rootView
    }
}
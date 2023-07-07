package com.bbt2000.boilerplate.demos.viewbinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bbt2000.boilerplate.databinding.FragmentVbBinding

class ViewBindingFragment : Fragment() {
    lateinit var viewBinding: FragmentVbBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_viewbinding, container, false)
        viewBinding = FragmentVbBinding.inflate(inflater, container, false)
        viewBinding.textView.text = "ViewBinding example"
        return viewBinding.root
    }
}
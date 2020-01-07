package com.tbuonomo.creativeviewpager.transformer

import android.view.View
import androidx.viewpager.widget.ViewPager

class CreativeContentPageTransformer(private val contentWidthPadding: Float) : ViewPager.PageTransformer {

  override fun transformPage(view: View, position: Float) {
    view.translationX = contentWidthPadding
  }
}
package learn.codeacademy.covidtracker

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import kotlinx.android.synthetic.main.marker_view.view.*

class CustomMarker(context: Context, layoutResource: Int):  MarkerView(context, layoutResource) {
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tvPrice.setText(""+e!!.`val`.toInt())
    }

    override fun getXOffset(xpos: Float): Int {
        return -(width/2)
    }

    override fun getYOffset(ypos: Float): Int {
        return (-(height*2.05)).toInt()
    }
}

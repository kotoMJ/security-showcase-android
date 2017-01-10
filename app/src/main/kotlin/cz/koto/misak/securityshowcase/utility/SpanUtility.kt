package cz.koto.misak.securityshowcase.utility

import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.text.style.ReplacementSpan


class SuperAlignedSpan : ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return paint.getSuperPaint().measureText(text.subSequence(start, end).toString()).toInt()
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int,
                      x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {

        val sub = text.subSequence(start, end).toString()
        val p = paint.getSuperPaint()

        canvas.drawText(sub, x, y - (bottom - top) / 3f, p)
    }

}

fun Paint.getSuperPaint(): TextPaint = TextPaint(this).apply {
    textSize = this@getSuperPaint.textSize / 2f
}
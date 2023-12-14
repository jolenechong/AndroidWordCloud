package com.jolenechong.wordcloud

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.jolenechong.wordcloud.utils.DataProcessing

data class Word(
    val word: String,
    val frequency: Int,
    var x: Float,
    var y: Float,
    var width: Int,
    var height: Int,
    val textSize: Float = 50f,
    val color: Int = Color.BLACK
) {}

class WordCloud(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private val words = ArrayList<Word>()
    private val colors = arrayListOf<String>(
        "#045893",
        "#db6100",
        "#108010",
        "#108010",
        "#74499c",
        "#c158a0",
        "#009dae"
    )
    private val baseFont = 30f

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.textSize = 50f
    }

    fun setParagraph(paragraph: String, topN: Int = 10, lemmatize: Boolean = false) {
        val words = DataProcessing(context).cleanText(paragraph, lemmatize)
        setWords(words,  topN)
    }

    fun setWords(words: List<String>, topN: Int = 10) {
        this.words.clear()

        // Calculate word frequencies
        val wordFrequencies = mutableMapOf<String, Int>()
        for (word in words) {
            wordFrequencies[word] = wordFrequencies.getOrDefault(word, 0) + 1
        }

        // Sort word frequencies in descending order
        val sortedFrequencies = wordFrequencies.entries.sortedByDescending { it.value }

        // Take the top N most frequent words
        val topNFrequencies = sortedFrequencies.take(topN)

        // Find the maximum frequency among the top N
        val maxFrequency = topNFrequencies.map { it.value }.maxOrNull() ?: 1

        for ((word, frequency) in topNFrequencies) {
            // Calculate the scale factor based on the frequency
            val scaleFactor = (frequency.toDouble() / maxFrequency).toFloat()

            // Set the text size based on the scale factor
            val textSize = baseFont + scaleFactor * 50f
            paint.textSize = textSize

            val textBounds = Rect()
            paint.getTextBounds(word, 0, word.length, textBounds)

            this.words.add(
                Word(word, frequency, 0f, 0f, textBounds.width(), textBounds.height(), textSize)
            )
        }
        invalidate()
    }

    fun setColors(colors: ArrayList<Int>) {

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate the center of the canvas
        val centerX = width / 2f
        val centerY = height / 2f

        // Sort the words in descending order of frequency
        words.sortByDescending { it.frequency }

        // Place the most frequent word at the center of the canvas
        val mostFrequentWord = words[0]
        mostFrequentWord.x = centerX
        mostFrequentWord.y = centerY

        // Set the radius for the circle
        val radiusX = width / 4f
        val radiusY = height / 4f

        // Place the other words in an oval pattern around the most frequent word
        for (i in 1 until words.size) {
            val word = words[i]

            // Calculate the angle of the word based on its position in the oval
            val angle = (2 * Math.PI * i) / words.size

            // Calculate the x and y coordinates of the word based on its angle and the oval dimensions
            word.x = centerX + radiusX * Math.cos(angle).toFloat()
            word.y = centerY + radiusY * Math.sin(angle).toFloat()

            // Adjust word positions to avoid overlapping
            adjustWordPosition(word, words.subList(0, i))
        }

        centerWordCloud()

        // Draw each word to the canvas at its calculated position
        for (word in words) {
            paint.textSize = word.textSize
            paint.color = getRandomColor()
            Log.w("WordCloud", "word: ${word.word}, textSize: ${word.textSize}")
            canvas.drawText(word.word, word.x, word.y, paint)
        }
    }

    private fun adjustWordPosition(word: Word, otherWords: List<Word>) {
        for (otherWord in otherWords) {
            if (isOverlap(word, otherWord)) {
                // If overlap occurs, adjust the position of the word
                word.x += 10  // Adjust this value based on your requirements
                word.y += 10  // Adjust this value based on your requirements
                adjustWordPosition(word, otherWords)  // Recursive call for further adjustments
                return
            }
        }
    }

    private fun centerWordCloud() {
        // get the highest point, lowest point, rightest point, and leftest point of the current words in the word cloud
        val extremePoints = findExtremePoints(words)

        // calculate the center point of the word cloud
        val centerX = (extremePoints.maxX + extremePoints.minX) / 2f
        val centerY = (extremePoints.maxY + extremePoints.minY) / 2f

        // calculate how much to adjust extreme points so that the word cloud is centered
        val adjustX = width / 2f - centerX
        val adjustY = height / 2f - centerY

        // adjust the position of each word
        for (word in words) {
            word.x += adjustX
            word.y += adjustY
        }
    }

    private fun findExtremePoints(words: List<Word>): Quadrilateral {
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE

        for (word in words) {
            // Find the leftmost and rightmost points
            minX = minOf(minX, word.x)
            maxX = maxOf(maxX, word.x + word.width)

            // Find the highest and lowest points
            minY = minOf(minY, word.y)
            maxY = maxOf(maxY, word.y + word.height)
        }

        return Quadrilateral(minX, minY, maxX, maxY)
    }

    data class Quadrilateral(val minX: Float, val minY: Float, val maxX: Float, val maxY: Float)

    private fun isOverlap(word1: Word, word2: Word): Boolean {
        val rect1 = Rect(word1.x.toInt(), word1.y.toInt(), (word1.x + word1.width).toInt(), (word1.y + word1.height).toInt())
        val rect2 = Rect(word2.x.toInt(), word2.y.toInt(), (word2.x + word2.width).toInt(), (word2.y + word2.height).toInt())
        return Rect.intersects(rect1, rect2)
    }

    private fun getRandomColor(): Int {
        val randomNum = (0 until colors.size).random()
        return Color.parseColor(colors[randomNum])
    }

}
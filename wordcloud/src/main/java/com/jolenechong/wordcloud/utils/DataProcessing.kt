package com.jolenechong.wordcloud.utils

import android.content.Context
import java.util.Properties
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP

class DataProcessing(private val mContext: Context) {

    fun cleanText(text: String, lemm: Boolean = false): List<String> {
        val stopwords = mContext.assets.open("stopwords.txt").bufferedReader().useLines { it.toList() }

        // Remove punctuation, tokenize and remove stopwords
        val noPunct = text.replace(Regex("[^a-zA-Z0-9 ]"), "")
        val words = noPunct.lowercase().trim().split(" ")

        val stopwordsRemoved = words.filterNot { it.lowercase() in stopwords || it.length == 1}

        // only lemmatize if lemm, else return
        if (!lemm) return stopwordsRemoved

        val props = Properties()
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma")
        val pipeline = StanfordCoreNLP(props)
        val sb = StringBuilder()
        for (token in stopwordsRemoved) {
            sb.append(token).append(" ")
        }
        val annotation = pipeline.process(sb.toString().trim())

        val lemmas = annotation.get(CoreAnnotations.SentencesAnnotation::class.java)[0]
            .get(CoreAnnotations.TokensAnnotation::class.java)
            .joinToString(" ") { it.get(CoreAnnotations.LemmaAnnotation::class.java) }

        return lemmas.split(" ")
    }
}
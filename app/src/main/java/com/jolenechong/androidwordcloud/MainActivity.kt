package com.jolenechong.androidwordcloud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jolenechong.androidwordcloud.databinding.ActivityMainBinding
import com.jolenechong.wordcloud.WordCloud

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var wordCloudView: WordCloud

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wordCloudView = WordCloud(application, null)
        binding.graphContainer.addView(wordCloudView)

//        wordCloudView.setParagraph(
//            "Artificial Intelligence (AI) refers to the development of computer systems that can perform tasks that typically require human intelligence. These tasks include learning, reasoning, problem-solving, understanding natural language, and even perception. AI systems are designed to analyze data, identify patterns, and make decisions with minimal human intervention. Machine learning, a subset of AI, empowers systems to improve their performance over time by learning from experience. From voice assistants and image recognition to autonomous vehicles and predictive analytics, AI has permeated various aspects of our daily lives and industries. As AI technology advances, ethical considerations and responsible deployment become increasingly crucial to ensure that AI benefits society and aligns with human values. The ongoing research and development in the field of AI promise transformative applications that have the potential to revolutionize industries, enhance efficiency, and contribute to solving complex global challenges.",
//            topN = 28,
//            lemmatize = false
//        )

        wordCloudView.setParagraph(
            "Information Technology (IT) is a vast and dynamic field that encompasses the use of computers, software, networks, and other technological systems to manage and process information. In the contemporary landscape, IT is an integral part of virtually every aspect of modern life, influencing how we communicate, work, and access information. IT professionals are responsible for designing, implementing, and maintaining computer systems, networks, and applications, ensuring their efficiency, security, and functionality. This field constantly evolves as new technologies emerge, driving innovation and reshaping the way individuals and organizations interact with data. From cybersecurity and data analytics to cloud computing and artificial intelligence, IT plays a pivotal role in shaping the digital future, enabling connectivity, and driving advancements across diverse industries.",
            topN = 15,
            lemmatize = false
        )

//        // doesn't have cleaning, suitable when you want to implement the cleaning yourself
//        wordCloudView.setWords(arrayListOf(
//            "human",
//            "tasks",
//            "tasks",
//            "AI",
//            "AI",
//            "AI",
//            "AI",
//            "systems",
//            "systems",
//        ), topN = 10)
    }
}
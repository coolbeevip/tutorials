package com.coolbeevip.deeplearning4j.word2vec;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
public class Example {
  public static void main(String[] args) throws FileNotFoundException {
    // 数据集文件路径
    String filePath = new File("deeplearning4j/src/test/resources/raw_sentences.txt").getAbsolutePath();

    // 将数据集加载到迭代器中
    SentenceIterator iter = new BasicLineIterator(filePath);

    // 进行分词并定义分词工具
    TokenizerFactory tFactory = new DefaultTokenizerFactory();

    // 训练模型
    Word2Vec vec = new Word2Vec.Builder()
        .minWordFrequency(5)
        .iterations(1)
        .layerSize(100)
        .seed(42)
        .windowSize(5)
        .iterate(iter)
        .tokenizerFactory(tFactory)
        .build();
    vec.fit();

    // 获取单词之间的相似性
    double similarity = vec.similarity("day", "night");
    System.out.println("Similarity between day and night: " + similarity);

    // 获取与单词"water"最相似的10个单词
    Collection<String> similarWords = vec.wordsNearest("water", 10);
    System.out.println("Words closest to 'water': " + similarWords);
  }
}

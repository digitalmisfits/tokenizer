package tokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import tokenizer.analysis.StandardReasonAnalyser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println(Tokenizer.tokenize(new StandardReasonAnalyser(),
                "A: GLM [ PriceLimitBreach [24.0040 greater than 5%, [idf=2.34]"));
    }

    public static final class Tokenizer {

        public static List<String> tokenize(final Analyzer analyzer, final String string) {
            List<String> result = new ArrayList<>();
            try (TokenStream stream = analyzer.tokenStream(null, new StringReader(string))) {
                stream.reset();
                while (stream.incrementToken()) {
                    result.add(stream.getAttribute(CharTermAttribute.class).toString());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return result;
        }

    }
}

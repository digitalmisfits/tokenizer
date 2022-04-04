package tokenizer.analysis;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

public final class StandardReasonAnalyser extends StopwordAnalyzerBase {

    private final static Pattern NUMERIC_PATTERN = Pattern.compile("^(\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?)$");

    /**
     * Default maximum allowed token length
     */
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

    /**
     * Builds an analyzer with the given stop words.
     *
     * @param stopWords stop words
     */
    public StandardReasonAnalyser(CharArraySet stopWords) {
        super(stopWords);
    }

    /**
     * Builds an analyzer with no stop words.
     */
    public StandardReasonAnalyser() {
        this(CharArraySet.EMPTY_SET);
    }

    /**
     * Builds an analyzer with the stop words from the given reader.
     *
     * @param stopwords Reader to read stop words from
     * @see WordlistLoader#getWordSet(Reader)
     */
    public StandardReasonAnalyser(Reader stopwords) throws IOException {
        this(loadStopwordSet(stopwords));
    }

    /**
     * Set the max allowed token length. Tokens larger than this will be chopped up at this token
     * length and emitted as multiple tokens. If you need to skip such large tokens, you could
     * increase this max length, and then use {@code LengthFilter} to remove long tokens. The default
     * is {@link org.apache.lucene.analysis.standard.StandardAnalyzer#DEFAULT_MAX_TOKEN_LENGTH}.
     */
    public void setMaxTokenLength(int length) {
        maxTokenLength = length;
    }

    /**
     * Returns the current maximum token length
     *
     * @see #setMaxTokenLength
     */
    public int getMaxTokenLength() {
        return maxTokenLength;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        var src = new StandardTokenizer();
        src.setMaxTokenLength(maxTokenLength);

        TokenStream stream = new LowerCaseFilter(src);
        stream = new StopFilter(stream, stopwords);
        stream = new ExchangeTokenFilter(stream);
        stream = new PatternReplaceFilter(stream, NUMERIC_PATTERN, "<var>", true);

        return new TokenStreamComponents(input -> {
            src.setMaxTokenLength(StandardReasonAnalyser.this.maxTokenLength);
            src.setReader(input);
        }, stream);
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        return new LowerCaseFilter(in);
    }
}
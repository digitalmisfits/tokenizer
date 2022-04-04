package tokenizer.analysis;

import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ExchangeTokenFilter extends FilteringTokenFilter {

    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final PayloadAttribute payloadAtt = addAttribute(PayloadAttribute.class);
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    protected ExchangeTokenFilter(TokenStream input) {
        super(input);
    }

    @Override
    protected boolean accept() {
        if (offsetAtt.startOffset() == 0) {
            var term = Arrays.copyOfRange(termAtt.buffer(), 0, termAtt.length());
            payloadAtt.setPayload(new BytesRef(new String(term).getBytes(StandardCharsets.UTF_8)));
            return false;
        } else {
            return true;
        }
    }
}

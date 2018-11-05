package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class normalises a console log fix into a fix message.
 */
public class Log4jNormaliserTransformer extends FixTransformer {

    private static final Logger logger = LogManager.getLogger(Log4jNormaliserTransformer.class);

    private final String logSeparator;
    private final int index;

    /**
     * Constructor.
     *
     * @param next         the next transformer in the chain to be processed.
     * @param logSeparator the log separator.
     */
    public Log4jNormaliserTransformer(Transformable<String, String> next, String logSeparator, int index) {
        super(next);
        this.logSeparator = logSeparator;
        this.index = index;
        push(new SeparatorNormaliserTransformer());
    }

    @Override
    // "M|2|0|0|I|20180711-17:03:11.248|96|8=FIX.4.4^A9=74^A35=0^A34=2^A49=TEST-QUOTE^A52=20180711-21:03:11.144^A56=TEST-FX^A112=STBL-2^A10=003^A|0|",
    // Note that FIX is at 7th position in the log.
    public String transform(String msg) {
        return super.transform(msg.split(logSeparator)[7]);
    }
}

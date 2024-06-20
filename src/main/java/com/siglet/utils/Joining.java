package com.siglet.utils;

import java.util.Collection;
import java.util.stream.Collector;

public class Joining {


    public static Collector<CharSequence, ?, String> twoDelimiters(String delimiter, String finalDelimiter, String prefix, String suffix) {

        return Collector.of(
                StringBuilder::new, // Supplier
                (sb, str) -> {
                    if (!sb.isEmpty()) {
                        sb.append(delimiter);
                    } else {
                        sb.append(prefix);
                    }
                    sb.append(str);
                }, // Accumulator
                StringBuilder::append, // Combiner
                sb -> {
                    int lastDelimiterIndex = sb.lastIndexOf(delimiter);
                    if (lastDelimiterIndex >= 0) {
                        sb.replace(lastDelimiterIndex, lastDelimiterIndex + delimiter.length(), finalDelimiter);
                    }
                    sb.append(suffix);
                    if (sb.toString().equals(suffix)) {
                        return null;
                    } else {
                        return sb.toString();
                    }
                } // Finisher
        );
    }

    public static String collection(String delimiter, String finalDelimiter, Collection<String> elements) {
        return elements.stream()
                .collect(twoDelimiters(delimiter, finalDelimiter, "", ""));
    }

}

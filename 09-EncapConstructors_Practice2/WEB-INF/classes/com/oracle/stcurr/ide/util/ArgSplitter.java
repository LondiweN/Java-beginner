package com.oracle.stcurr.ide.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mheimer
 */
public class ArgSplitter {

    private static final Pattern whiteSpacePattern = Pattern.compile("\\s+");
    private static final Pattern singleQuotePattern = Pattern.compile("\".*?\"");
    private static final Pattern doubleQuotePattern = Pattern.compile("'.*?'");

    public static List<String> splitArgs(String args) {
        args = args.trim();
        List<String> list = new ArrayList<>();
        List<int[]> wsRanges = findWhiteSpaceRanges(args);
        List<int[]> quoteRanges = findQuoteRanges(args);
        List<int[]> wsRangesNotInQuotes = findWhiteSpaceRangesNotInQuotes(wsRanges, quoteRanges);
        int pos = 0;
        for (int[] wsRangeNotInQuotes : wsRangesNotInQuotes) {
            list.add(args.substring(pos, wsRangeNotInQuotes[0]));
            pos = wsRangeNotInQuotes[1]+1;
        }
        if(pos < args.length()) {
            list.add(args.substring(pos));
        }
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if((s.startsWith("'") && s.endsWith("'")) || (s.startsWith("\"") && s.endsWith("\""))) {
                s = s.substring(1, s.length()-1);
                list.set(i, s);
            }
            
            // http://bugs.sun.com/view_bug.do?bug_id=6511002
            if (System.getProperty("os.name").startsWith("Windows") && s.contains("\"")) {
                System.out.println("escaping double");
                list.set(i, s.replaceAll("\"", "\\\\\""));
            }
        }
        
        Iterator<String> i = list.iterator();
        while(i.hasNext()) {
            String s = i.next();
            if(s.length() == 0) {
                i.remove();
            }
        }
        
        return list;
    }

    private static List<int[]> findWhiteSpaceRanges(String s) {
        List<int[]> ranges = new ArrayList<>();
        Matcher whiteSpaceMatcher = whiteSpacePattern.matcher(s);
        while (whiteSpaceMatcher.find()) {
            int[] range = new int[2];
            range[0] = whiteSpaceMatcher.start();
            range[1] = whiteSpaceMatcher.end()-1;
            ranges.add(range);
        }
        return ranges;
    }

    private static List<int[]> findQuoteRanges(String s) {
        List<int[]> ranges = new ArrayList<>();
        Matcher singleQuoteMatcher = singleQuotePattern.matcher(s);
        Matcher doubleQuoteMatcher = doubleQuotePattern.matcher(s);
        boolean sqf = singleQuoteMatcher.find();
        boolean dqf = doubleQuoteMatcher.find();
        while (sqf || dqf) {
            int sqStart = sqf ? singleQuoteMatcher.start() : Integer.MAX_VALUE;
            int dqStart = dqf ? doubleQuoteMatcher.start() : Integer.MAX_VALUE;
            Matcher quoteMatcher = sqStart < dqStart ? singleQuoteMatcher : doubleQuoteMatcher;
            int[] range = new int[2];
            range[0] = quoteMatcher.start();
            range[1] = quoteMatcher.end()-1;
            ranges.add(range);
            sqf = singleQuoteMatcher.find(range[1]+1);
            dqf = doubleQuoteMatcher.find(range[1]+1);
        }
        return ranges;
    }

    private static List<int[]> findWhiteSpaceRangesNotInQuotes(List<int[]> wsRanges, List<int[]> quoteRanges) {
        List<int[]> ranges = new ArrayList<>();
        for (int[] range : wsRanges) {
            if (!isPositionInRanges(range[0], quoteRanges)) {
                ranges.add(range);
            }
        }
        return ranges;
    }

    private static boolean isPositionInRanges(int pos, List<int[]> ranges) {
        boolean isInRange = false;
        for (int[] range : ranges) {
            if (pos >= range[0] && pos <= range[1]) {
                isInRange = true;
                break;
            }
        }
        return isInRange;
    }
}

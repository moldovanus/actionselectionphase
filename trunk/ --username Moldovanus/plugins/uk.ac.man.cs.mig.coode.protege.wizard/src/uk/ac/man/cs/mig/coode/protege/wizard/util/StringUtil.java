package uk.ac.man.cs.mig.coode.protege.wizard.util;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Oct-2005
 */
public class StringUtil {

    /**
     * Gets the index of the closing bracket matching the first opening bracket in the str
     *
     * @param s         the full string
     * @param openChar  the character representing the opening bracket
     * @param closeChar the character representing the closing bracket
     * @return the index of the closing bracket or -1 if none found
     */
    public static int getIndexOfMatchingBracket(String s, char openChar, char closeChar) {
        if (openChar == closeChar) {
            return getIndexOfSecondOccurance(s, openChar);
        } else {
            int indent = 0;
            int i = 0;
            boolean started = false;
            while (i < s.length() && (!started || (indent > 0))) {
                if (s.charAt(i) == openChar) {
                    started = true;
                    indent++;
                } else if (s.charAt(i) == closeChar) {
                    indent--;
                }
                i++;
            }
            int result = i - 1;
            if ((indent != 0) || !started) {
                result = -1;
            }
            return result;
        }
    }

    public static int getIndexOfSecondOccurance(String s, char c) {
        int first = s.indexOf(c);
        return s.indexOf(c, first + 1);
    }

    public static void main(String[] args) {
        char open = '(';
        char close = ')';
        String[] tests = {"(abcdefg)", "a(bcdefg)", "abcdefg", "a(bcdefg", "a(bc(d))efg",
                "(abc))defg", "abc((def)g", "abcd)efg"};
        int[] expectedResults = {8, 8, -1, -1, 7, 4, -1, -1};
        for (int i = 0; i < tests.length; i++) {
            int index = getIndexOfMatchingBracket(tests[i], open, close);
            if (index != expectedResults[i]) {
                System.out.println("test failed " + tests[i] + " GOT " + index + " EXPECTED " + expectedResults[i]);
            }
        }
        System.out.println("DONE");
    }
}

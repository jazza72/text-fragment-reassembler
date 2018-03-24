package fragment.submissions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is a solution to the Reassemble Text Fragments challenge. The
 * algorithm simply checks for overlapping sections of text between the
 * fragments and merges the overlapping sections of text until there is only one
 * fragment left - the reconstructed line.
 * 
 * I am sure there is a very beautiful, elegant and efficient algorithm for
 * solving this problem out there somewhere ;), but for now the brute force approach will do.
 * 
 * @author jameswilson
 *
 */
public class JamesWilson {

	public static void main(String[] args) throws IOException {
		try (BufferedReader in = new BufferedReader(new FileReader(args[0]))) {
			in.lines().map(JamesWilson::reassemble).forEach(System.out::println);
		}
	}

	/**
	 * Static method to discover whether one string overlaps with another A string
	 * s1 is said to overlap another String s2 when some sequence of characters
	 * starting from the last character in s1 are found in s2. This method continues
	 * checking the end sequence from s1 against a match in s2 until no match is
	 * found, adding the previous character from s1 with each iteration.
	 * 
	 * @param s1
	 *            - the "overlapping" string
	 * @param s2
	 *            = the "overlapped" string
	 * @return number of overlapping characters - 0 indicates no overlap
	 */
	public static int overlap(String s1, String s2) {

		// if these are the same string object, then just return the length
		if (s1.equals(s2))
			return 0;

		// number of chars from end of s1 to start match string
		int len = 1;

		// start at the last char in s1, terminate if we go over the length of
		// overlapping string
		while (len <= s1.length() && s2.indexOf(s1.substring((s1.length() - len))) != -1) {
			len++;
		}

		// subtract 1 to get overlap
		len -= 1;

		// there is one edge case to consider - where there is a partial overlap of s1
		// but this overlap
		// does not start at the first char of s2 eg: XYZABC AABCET- this is not an
		// overlap
		if (s2.indexOf(s1.substring(s1.length() - len)) > 0 && len != s1.length()) {
			len = 0;
		}

		// subtract 1 to get length of overlap
		return len;

	}

	/**
	 * The main reassembly function - this takes the line of fragments and for each
	 * pair of fragments checks if there are any overlaps. Each iteration merges the
	 * two fragments with the greatest overlap until there is only one fragment in
	 * the list which should be the reconstructed line of text.
	 * 
	 * @param fragmentedLine
	 *            - the line of text fragments
	 * @return the defragmented, reconstructed line of text
	 */
	public static String reassemble(String fragmentedLine) {

		final String input = fragmentedLine;

		// empty string
		if (input.trim().equals(""))
			return input;

		// first split to a list
		List<String> fragments = new ArrayList<>(Arrays.asList(input.split("[;]")));

		// list of size 1?
		if (fragments.size() == 1)
			return input;

		// loop until there is only one element in the list
		// - the reconstructed sentence
		while (fragments.size() > 1) {

			// sort by length
			fragments.sort((o1, o2) -> o2.length() - o1.length());

			List<Overlap> overlaps = new ArrayList<>();

			// double loop - nasty
			for (String fragmentOuter : fragments) {
				for (String fragmentInner : fragments) {
					// test for overlap
					int overlap = JamesWilson.overlap(fragmentOuter, fragmentInner);

					// add to list of overlaps if required
					if (overlap > 0) {
						overlaps.add(new Overlap(fragmentOuter, fragmentInner, overlap));
					}
				}
			}

			// if for some reason, there are non-overlapping fragments left in the fragment
			// list
			// then simply take the longest fragment as the text to be returned
			if (overlaps.isEmpty()) {
				break;
			}

			// now sort the list
			overlaps.sort((o1, o2) -> o2.overlap - o1.overlap);

			// take the first element of the list - the largest overlap
			Overlap o = overlaps.get(0);

			// perform the merge
			String merged = JamesWilson.merge(o.front, o.back, o.overlap);

			// add the newly created fragment
			fragments.add(merged);

			// remove the merged elements from the list
			fragments.remove(o.front);
			fragments.remove(o.back);
		}

		// the first (only) element in the list is the defragmented text
		return fragments.get(0);
	}

	/**
	 * Static method to merge two strings using the number of overlapping characters
	 * as a guide. The prefix of s1 (ie: minus the overlap) is prepended to s2.
	 * 
	 * @param s1
	 * @param s2
	 * @overlap number of characters from the end of s1 that overlap with s2
	 * 
	 * @return the merged string
	 */
	public static String merge(String s1, String s2, int overlap) {

		// get the substring to prepend
		String prefix = s1.substring(0, s1.length() - overlap);

		// prepend it
		return prefix + s2;

	}

	/**
	 * Represents an overlap between two text fragnents
	 * 
	 * @author jameswilson
	 */
	private static class Overlap {

		// the overlapping string
		final private String front;

		// the overlapped string
		final private String back;

		final private int overlap;

		private Overlap(String front, String back, int overlap) {
			this.front = front;
			this.back = back;
			this.overlap = overlap;
		}
	}

	/**
	 * Class containing unit test cases for surrounding implementation class
	 * 
	 * @author jameswilson
	 *
	 */
	private static class TextFragmentReassemblerTester {

		private static void testOverlap() {

			// start with the specification test cases
			int overlap = JamesWilson.overlap("ABCDEF", "DEFG");

			assert (overlap == 3);

			overlap = JamesWilson.overlap("XYZABC", "ABCDEF");

			assert (overlap == 3);

			overlap = JamesWilson.overlap("BCDE", "ABCDEF");

			assert (overlap == 4);

			overlap = JamesWilson.overlap("ABCDEF", "XCDEZ");

			assert (overlap == 0);

			overlap = JamesWilson.overlap("BCDE", "ABCDEF");

			assert (overlap == 4);

			overlap = JamesWilson.overlap("XYZABC", "BCABCDEF");

			assert (overlap == 0);

			overlap = JamesWilson.overlap("O draconia", "conian devil! Oh la");

			assert (overlap == 5);
		}

		private static void testMerge() {

			String merged = JamesWilson.merge("ABCDEF", "DEFG", 3);

			assert (merged.equals("ABCDEFG"));

			merged = JamesWilson.merge("XYZABC", "ABCDEF", 3);

			assert (merged.equals("XYZABCDEF"));

			merged = JamesWilson.merge("ABCDEF", "XCDEZ", 0);

			assert (merged.equals("ABCDEFXCDEZ"));

			merged = JamesWilson.merge("O draconia", "conian devil! Oh la", 5);

			assert (merged.equals("O draconian devil! Oh la"));
		}

		private static void testReassemble() {
			String defragged = JamesWilson.reassemble("O draconia;conian devil! Oh la;h lame sa;saint!");

			assert (defragged.equals("O draconian devil! Oh lame saint!"));

			defragged = JamesWilson.reassemble(
					"m quaerat voluptatem.;pora incidunt ut labore et d;, consectetur, adipisci velit;olore magnam aliqua;idunt ut labore et dolore magn;uptatem.;i dolorem ipsum qu;iquam quaerat vol;psum quia dolor sit amet, consectetur, a;ia dolor sit amet, conse;squam est, qui do;Neque porro quisquam est, qu;aerat voluptatem.;m eius modi tem;Neque porro qui;, sed quia non numquam ei;lorem ipsum quia dolor sit amet;ctetur, adipisci velit, sed quia non numq;unt ut labore et dolore magnam aliquam qu;dipisci velit, sed quia non numqua;us modi tempora incid;Neque porro quisquam est, qui dolorem i;uam eius modi tem;pora inc;am a");

			assert (defragged.equals(
					"Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem."));

			// test the case where some fragments may not overlap - just return longest
			// remaining fragment
			defragged = JamesWilson.reassemble("va technical;I really lo; tests!; love doing Jav;");

			assert (defragged.equals("I really love doing Java technical"));
		}

		public static void main(String args[]) {
			TextFragmentReassemblerTester.testOverlap();

			TextFragmentReassemblerTester.testMerge();

			TextFragmentReassemblerTester.testReassemble();
			
			System.out.println("Unit tests passed successfully");
		}
	}
}

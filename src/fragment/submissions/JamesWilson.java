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
	 *            - the "overlapped" string
	 * @return number of overlapping characters - 0 indicates no overlap
	 */
	private static int overlap(String s1, String s2) {

		// if these are the same string object, then just return the length
		if (s1.equals(s2))
			return 0;

		int len = s1.length();

		//start with trying to match the whole of s1 to s2 and decrease by one char
		//each time until a match is found or the end of s1 is reached
		while (len > 0 && !s2.startsWith(s1.substring((s1.length() - len)))) {
			len--;
		}

		//if there is no match as a starting string, then see if the whole of the s1 string 
		//overlaps with the s2 string
		if (s2.indexOf(s1) != -1) {
			len = s1.length();
		}
		
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
	private static String reassemble(String fragmentedLine) {

		final String input = fragmentedLine;

		if (input.trim().equals(""))
			return input;

		List<String> fragments = new ArrayList<>(Arrays.asList(input.split("[;]")));

		if (fragments.size() == 1)
			return fragments.get(0);

		// loop until there is only one element in the list
		// - the reconstructed sentence
		while (fragments.size() > 1) {

			// sort by length
			fragments.sort((o1, o2) -> o2.length() - o1.length());

			//calculate overlaps
			List<Overlap> overlaps = JamesWilson.calculateOverlaps(fragments);
			
			// if for some reason, there are non-overlapping fragments left in the fragment
			// list then simply take the longest fragment as the text to be returned
			if (overlaps.isEmpty()) {
				break;
			}

			overlaps.sort((o1, o2) -> o2.overlap - o1.overlap);

			// take the first element of the list - the largest overlap
			Overlap o = overlaps.get(0);

			String merged = JamesWilson.merge(o.prefix, o.suffix, o.overlap);

			fragments.add(merged);
			fragments.remove(o.prefix);
			fragments.remove(o.suffix);
		}

		// the first (only) element in the list is the defragmented text
		return fragments.get(0);
	}
	
	
	/**
	 * Generates a <code>List</code> of <code>Overlaps</code> for the passed
	 * <code>List</code> of text fragments.
	 *  
	 * @param text fragments
	 * @return list of overlaps (if any)
	 */
	private static List<Overlap> calculateOverlaps (List<String> fragments) {
		
		List<Overlap> overlaps = new ArrayList<>();
		
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
		
		return overlaps;		
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
	private static String merge(String s1, String s2, int overlap) {

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
		final private String prefix;

		// the overlapped string
		final private String suffix;

		final private int overlap;

		private Overlap(String front, String back, int overlap) {
			this.prefix = front;
			this.suffix = back;
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

			overlap = JamesWilson.overlap("XYZABC", "ZZABCDEF");

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
		
		private static void testOverlapCalculation() {
			
			List<String> l = new ArrayList<>(Arrays.asList(new String[]{"Dirty British coaster with a salt-caked smoke stack, ", 
												 "With a cargo of Tyne coal, Roa",
												 "ack, Butting through the Channel in the mad March days, With a c",
												 "Road-rails, pig-lead, F",
												 "Firewood, iron-ware, and cheap tin trays."}));
			
			List<Overlap> o = JamesWilson.calculateOverlaps(l);
			
			assert (o.size()==5);
						
		}

		private static void testReassemble() {
			String defragged = JamesWilson.reassemble("O draconia;conian devil! Oh la;h lame sa;saint!");

			assert (defragged.equals("O draconian devil! Oh lame saint!"));

			defragged = JamesWilson.reassemble(
					"m quaerat voluptatem.;pora incidunt ut labore et d;, consectetur, adipisci velit;olore magnam aliqua;idunt ut labore et dolore magn;uptatem.;i dolorem ipsum qu;iquam quaerat vol;psum quia dolor sit amet, consectetur, a;ia dolor sit amet, conse;squam est, qui do;Neque porro quisquam est, qu;aerat voluptatem.;m eius modi tem;Neque porro qui;, sed quia non numquam ei;lorem ipsum quia dolor sit amet;ctetur, adipisci velit, sed quia non numq;unt ut labore et dolore magnam aliquam qu;dipisci velit, sed quia non numqua;us modi tempora incid;Neque porro quisquam est, qui dolorem i;uam eius modi tem;pora inc;am a");

			assert (defragged.equals(
					"Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem."));

			defragged = JamesWilson.reassemble("Dirty British coaster with a salt-caked smoke stack, ;Firewood, iron-ware, and cheap tin trays.;With a cargo of Tyne coal, Roa;ack, Butting through the Channel in the mad March days, With a c;Road-rails, pig-lead, F;");
			
			assert (defragged.equals("Dirty British coaster with a salt-caked smoke stack, Butting through the Channel in the mad March days, With a cargo of Tyne coal, Road-rails, pig-lead, Firewood, iron-ware, and cheap tin trays."));
			
			// test the case where some fragments may not overlap - just return longest
			// remaining fragment
			defragged = JamesWilson.reassemble("va technical;I really lo; tests!; love doing Jav;");

			assert (defragged.equals("I really love doing Java technical"));
			
			//single word - no delimiter
			defragged = JamesWilson.reassemble("This is a test");
			
			assert (defragged.equals("This is a test"));
			
			//single word - with delimiter
			defragged = JamesWilson.reassemble(";;This is a test;");
			
			assert (defragged.equals("This is a test"));
			
			//empty string
			defragged = JamesWilson.reassemble("        ;    ");
			
			assert (defragged.equals("        "));
			
			//empty string
			defragged = JamesWilson.reassemble("     ");
			
			assert (defragged.equals("     "));
			
			//empty string
			defragged = JamesWilson.reassemble(" ;");
			
			assert (defragged.equals(" "));
			
			defragged = JamesWilson.reassemble("repeat, now;now let's repeat; repeat now!");
			
			assert (defragged.equals("repeat, now let's repeat now!"));
			
		}

		public static void main(String args[]) {
			TextFragmentReassemblerTester.testOverlap();

			TextFragmentReassemblerTester.testMerge();

			TextFragmentReassemblerTester.testOverlapCalculation();
			
			TextFragmentReassemblerTester.testReassemble();
						
			System.out.println("Unit tests passed successfully");
		}
	}
}

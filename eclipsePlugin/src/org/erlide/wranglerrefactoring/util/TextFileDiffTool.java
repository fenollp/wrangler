package org.erlide.wranglerrefactoring.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

public class TextFileDiffTool {

	static private File inFile;
	static private File outFile;

	static Diff algorithm;
	static private ArrayList<Character> inFileCharArray;
	static private ArrayList<Character> outFileCharArray;
	static private List<Difference> differencesList;

	private TextFileDiffTool() {
	}

	static private ArrayList<Character> splitFile(File file) throws IOException {
		ArrayList<Character> result = new ArrayList<Character>();
		BufferedReader input = new BufferedReader(new FileReader(file));

		String line;
		while ((line = input.readLine()) != null) {
			char[] chars = line.toCharArray();
			for (int i = 0; i < chars.length; ++i) {
				result.add(chars[i]);
			}
			result.add('\n');
		}
		input.close();

		return result;
	}

	@SuppressWarnings("unchecked")
	static public ArrayList<TextEdit> createEdits(File in, File out) {
		inFile = in;
		outFile = out;

		ArrayList<TextEdit> edits = new ArrayList<TextEdit>();
		inFileCharArray = null;
		outFileCharArray = null;
		try {
			inFileCharArray = splitFile(inFile);
			outFileCharArray = splitFile(outFile);
		} catch (Exception e) {
			// TODO io error???
			e.printStackTrace();
		}

		algorithm = new Diff(inFileCharArray, outFileCharArray);

		differencesList = algorithm.diff();
		for (Difference d : differencesList) {
			edits.add(createEditFromDiff(d));
		}

		return edits;
	}

	// new version of creating changes
	static public ArrayList<TextEdit> _createEdits(File in, String out) {
		inFile = in;

		ArrayList<TextEdit> edits = new ArrayList<TextEdit>();
		inFileCharArray = null;
		outFileCharArray = null;
		try {
			inFileCharArray = splitFile(inFile);
			outFileCharArray = new ArrayList<Character>();
			outFileCharArray = convertArryaToArrayList(out.toCharArray());
		} catch (Exception e) {
			// TODO io error???
			e.printStackTrace();
		}

		algorithm = new Diff(inFileCharArray, outFileCharArray);

		differencesList = algorithm.diff();
		for (Difference d : differencesList) {
			edits.add(createEditFromDiff(d));
		}

		return edits;
	}

	private static ArrayList<Character> convertArryaToArrayList(char[] charArray) {
		ArrayList<Character> result = new ArrayList<Character>();
		for (char c : charArray) {
			result.add(c);
		}
		return result;
	}

	private static TextEdit createEditFromDiff(Difference diff) {
		TextEdit result = null;

		// delete
		if (diff.getAddedEnd() == -1 && diff.getDeletedEnd() != -1) {
			result = new DeleteEdit(diff.getDeletedStart(), diff
					.getDeletedEnd()
					- diff.getDeletedStart() + 1);
		}
		// replace
		else if (diff.getAddedEnd() != -1 && diff.getDeletedEnd() != -1) {
			result = createReplaceEdit(diff.getAddedStart(),
					diff.getAddedEnd(), diff.getDeletedStart(), diff
							.getDeletedEnd());
		}
		// insert
		else if (diff.getAddedEnd() != -1 && diff.getDeletedEnd() == -1) {
			result = new InsertEdit(diff.getDeletedStart(), getString(diff
					.getAddedStart(), diff.getAddedEnd()));
		}

		return result;
	}

	private static TextEdit createReplaceEdit(int addedStart, int addedEnd,
			int deletedStart, int deletedEnd) {
		TextEdit result = new MultiTextEdit();

		int addedLength = addedEnd - addedStart + 1;
		int deletedLength = deletedEnd - deletedStart + 1;
		int minLength = Math.min(addedLength, deletedLength);

		if (deletedLength < addedLength) {
			result.addChild(new InsertEdit(deletedStart + minLength, getString(
					addedStart + minLength, addedEnd)));
		}

		result.addChild(new ReplaceEdit(deletedStart, minLength, getString(
				addedStart, addedStart + minLength - 1)));

		if (addedLength < deletedLength) {
			result.addChild(new DeleteEdit(deletedStart + minLength,
					deletedLength - minLength));
		}

		return result;
	}

	private static String getString(int from, int to) {
		String s = "";
		// from, to+1
		for (char c : outFileCharArray)
			s += c;
		return s.substring(from, to + 1);
	}

}

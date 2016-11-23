package net.sce.bytecode;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Credits to Freddy1990 - basically a stripped down version
 * of his RegexInsnSearcher.
 */
public class RegexSearcher {
	private Map<String, Matcher> matches;
	private AbstractInsnNode[] list;
	private int[] offsets;
	private String code;

	public static String getCode(MethodNode m) {
		AbstractInsnNode[] list = m.instructions.toArray();
		String code = "";
		for(int k = 0; k < list.length; k++) {
			AbstractInsnNode in = list[k];
			if(in.getOpcode() < 0)
				continue;
			code += ASMConstants.OPCODES[in.getOpcode()] + " ";
		}
		return code.toLowerCase();
	}

	public RegexSearcher(MethodNode m) {
		this.matches = new HashMap<String, Matcher>();
		this.list = m.instructions.toArray();
		this.code = "";
		offsets = new int[list.length];
		StringBuilder sb = new StringBuilder();
		for(int k = 0; k < list.length; k++) {
			offsets[k] = -1;
			AbstractInsnNode in = list[k];
			if(in.getOpcode() < 0)
				continue;
			offsets[k] = sb.length();
			sb.append(ASMConstants.OPCODES[in.getOpcode()]);
			sb.append(" ");
		}
		code = sb.toString().toLowerCase();
		// System.out.println(code);
	}

	private String prepare(String pattern) {
		pattern = pattern.toLowerCase();
		// System.out.println(pattern);
		return pattern;
	}

	public AbstractInsnNode[] nextMatch(String regex) {
		regex = prepare(regex);
		Matcher match = matches.get(regex);
		if(match == null)
			match = Pattern.compile(regex).matcher(code);
		matches.put(regex, match);

		if(match.find())
			return makeResult(match.start(), match.end());
		else
			matches.remove(regex);
		return null;
	}

	private AbstractInsnNode[] makeResult(int start, int end) {
		int startIndex = 0;
		int endIndex = -1;
		for (int i = 0; i < offsets.length - 1; i++) {
			int offset = offsets[i];
			if (offset == start)
				startIndex = i;
			if ((offset < end) && (offsets[i + 1] >= end)) {
				endIndex = i;
				break;
			}
		}
		if (endIndex == -1)
			endIndex = offsets.length - 1;
		int length = endIndex - startIndex + 1;
		AbstractInsnNode[] result = new AbstractInsnNode[length];
		System.arraycopy(list, startIndex, result, 0, length);
		return result;
	}
}
package net.sce.bytecode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;

public class InstructionSearcher {
	private InsnList list;
	private AbstractInsnNode current;

	public InstructionSearcher(MethodNode m) {
		this.list = m.instructions;
		this.current = list.getFirst();
	}

	public AbstractInsnNode getCurrent() {
		return current;
	}

	public void setCurrent(AbstractInsnNode in) {
		current = in;
	}

	public AbstractInsnNode getNext(int opcode) {
		while (current != null) {
			if (current.getOpcode() == opcode) {
				AbstractInsnNode old = current;
				current = current.getNext();
				return old;
			}
			current = current.getNext();
		}
		return null;
	}
	
	public AbstractInsnNode getNext() {
		current = current.getNext();
		while(current.getOpcode() == -1) current = current.getNext();
		System.out.println(current.getOpcode());
		return current;
	}

	public AbstractInsnNode getPrevious(int opcode) {
		while (current != null) {
			if (current.getOpcode() == opcode) {
				AbstractInsnNode old = current;
				current = current.getPrevious();
				return old;
			}
			current = current.getPrevious();
		}
		return null;
	}
	
	public AbstractInsnNode getPrevious() {
		current = current.getPrevious();
		while(current.getOpcode() == -1) current = current.getPrevious();
		return current;
	}

	public LdcInsnNode getNextLDC(Object cst) {
		AbstractInsnNode in;
		while ((in = getNext(Opcodes.LDC)) != null) {
			LdcInsnNode ln = (LdcInsnNode) in;
			if (ln.cst.equals(cst)) return ln;
		}
		return null;
	}

	public LdcInsnNode getPreviousLDC(Object cst) {
		AbstractInsnNode in;
		while ((in = getPrevious(Opcodes.LDC)) != null) {
			LdcInsnNode ln = (LdcInsnNode) in;
			if (ln.cst.equals(cst))
				return ln;
		}
		return null;

	}

	/**
	 * @param opcode One of Opcodes.BIPUSH/Opcodes.SIPUSH
	 * @param value  Value to look for
	 * @return
	 */
	public IntInsnNode getNextPush(int opcode, int value) {
		AbstractInsnNode in;
		while ((in = getNext(opcode)) != null) {
			IntInsnNode iin = (IntInsnNode) in;
			if (iin.operand == value) return iin;
		}
		return null;
	}

	public List<AbstractInsnNode> analyze(int opcode) {
		reset();
		List<AbstractInsnNode> list = new ArrayList<AbstractInsnNode>();
		AbstractInsnNode in;
		while((in = getNext(opcode)) != null) {
			list.add(in);					
		}
		return list;
	}

	public int getIndex() {
		return list.indexOf(current);
	}

	public void setIndex(int index) {
		current = list.get(index);
	}

	/**
	 * Resets us back to the first instruction
	 */
	public void reset() {
		current = list.getFirst();
	}
}
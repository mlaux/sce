package net.sce.bytecode;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.MethodNode;

public class SCEMethodNode extends MethodNode {
	private SCEClassNode parent;

	public SCEMethodNode(SCEClassNode parent, int access, String name, String desc, String signature, String[] exceptions) {
		super(access, name, desc, signature, exceptions);
		this.parent = parent;
	}

	public void visitLdcInsn(Object cst) {
		super.visitLdcInsn(cst);
		Set<SCEMethodNode> mths = parent.constants.get(cst);
		if(mths == null)
			mths = new HashSet<SCEMethodNode>();
		mths.add(this);
		parent.constants.put(cst, mths);
	}
}

package net.sce.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class SCEClassNode extends ClassNode {
	public Map<Object, Set<SCEMethodNode>> constants = new HashMap<Object, Set<SCEMethodNode>>();
	public List<FieldNode> instanceFields = new ArrayList<FieldNode>();

	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		SCEMethodNode mn = new SCEMethodNode(this, access, name, desc, signature, exceptions);
		methods.add(mn);
		return mn;
	}
	
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if((access & Opcodes.ACC_STATIC) == 0)
			instanceFields.add(new FieldNode(access, name, desc, signature, value));
		return super.visitField(access, name, desc, signature, value);
	}
}

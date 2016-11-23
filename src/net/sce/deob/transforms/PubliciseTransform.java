package net.sce.deob.transforms;

import java.util.Iterator;
import java.util.List;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.SCEClassNode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class PubliciseTransform extends BytecodeTask implements Opcodes {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		result.addAll(all);
	}

	public boolean transform(SCEClassNode classNode, List<Hook> results) {
		classNode.access &= ~ACC_PRIVATE;
		classNode.access &= ~ACC_PROTECTED;
		classNode.access &= ~ACC_FINAL;
		classNode.access |= ACC_PUBLIC;
		Iterator<FieldNode> fieldIterator = classNode.fields.iterator();
		while (fieldIterator.hasNext()) {
			FieldNode fieldNode = fieldIterator.next();
			fieldNode.access &= ~ACC_PRIVATE;
			fieldNode.access &= ~ACC_PROTECTED;
			fieldNode.access &= ~ACC_FINAL;
			fieldNode.access |= ACC_PUBLIC;
		}
		Iterator<MethodNode> methodIterator = classNode.methods.iterator();
		while (methodIterator.hasNext()) {
			MethodNode methodNode = methodIterator.next();
			methodNode.access &= ~ACC_PRIVATE;
			methodNode.access &= ~ACC_PROTECTED;
			methodNode.access &= ~ACC_FINAL;
			methodNode.access |= ACC_PUBLIC;
		}
		return false;
	}
}

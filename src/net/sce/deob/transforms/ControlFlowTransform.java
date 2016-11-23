package net.sce.deob.transforms;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.SCEClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class ControlFlowTransform extends BytecodeTask {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		result.addAll(all);
	}

	public boolean transform(SCEClassNode scn, List<Hook> results) {
		for (MethodNode mn : scn.methods) {
			InsnList il = mn.instructions;
			if (il == null) continue;

		}
		return true;
	}

}

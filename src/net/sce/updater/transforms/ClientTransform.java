package net.sce.updater.transforms;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.InstructionSearcher;
import net.sce.bytecode.SCEClassNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class ClientTransform extends BytecodeTask {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for (SCEClassNode classNode : all) {
			for (MethodNode methodNode : classNode.methods) {
				if (methodNode.name.contains("main"))
					result.add(classNode);
			}
		}
	}

	public boolean transform(SCEClassNode scn, List<Hook> results) {
		identifyClass("client", scn);
		for(MethodNode mn : scn.methods) {
			if(mn.desc.contains("Ljava/lang/String;") && mn.desc.contains("I")) {
				InstructionSearcher is = new InstructionSearcher(mn);
				if(is.getNextLDC("[1)") != null) {
					FieldInsnNode fieldInsn = (FieldInsnNode) is.getNext(Opcodes.GETSTATIC);
					results.add(new Hook("client.baseX", fieldInsn));
					fieldInsn = (FieldInsnNode) is.getNext(Opcodes.GETSTATIC);
					results.add(new Hook("client.baseY", fieldInsn));
				}
			}
		}
		return true;
	}
}

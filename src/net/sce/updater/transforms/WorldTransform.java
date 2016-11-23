package net.sce.updater.transforms;

import net.sce.bytecode.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class WorldTransform extends BytecodeTask {
	
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for(SCEClassNode scn : all) {
			if(scn.constants.get(43594) != null) {
				for(MethodNode mn : scn.methods) {
					if(mn.desc.contains("Z")) {
						if(mn.desc.contains("L"+scn.name+";"))
							result.add(scn);
					}
				}
			}
		}
	}

	
	public boolean transform(SCEClassNode scn, List<Hook> results) {
		identifyClass("World", scn);
		for(MethodNode mn : scn.methods) {
			if(mn.desc.contains("L"+scn.name+";")) {
				InstructionSearcher is = new InstructionSearcher(mn);
				FieldInsnNode field = (FieldInsnNode) is.getNext(Opcodes.GETFIELD);
				results.add(new Hook("world.id", field));
			}
		}
		return true;  //To change body of implemented methods use File | Settings | File Templates.
	}
}

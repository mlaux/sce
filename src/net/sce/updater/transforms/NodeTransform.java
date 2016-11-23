package net.sce.updater.transforms;

import java.util.List;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.SCEClassNode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

public class NodeTransform extends BytecodeTask {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for (SCEClassNode scn : all) {
			if (scn.superName.equals("java/lang/Object")) {
				int nodesFound = 0, longsFound = 0;
				for (FieldNode fn : scn.fields) {
					if((fn.access & Opcodes.ACC_STATIC) != 0)
						continue;
					if (fn.desc.equalsIgnoreCase("L" + scn.name + ";"))
						nodesFound++;
					if (fn.desc.equalsIgnoreCase("J"))
						longsFound++;
					if (nodesFound == 2 && longsFound == 1)
						result.add(scn);
				}
			}
		}
	}

	
	public boolean transform(SCEClassNode scn, List<Hook> results) {
		identifyClass("Node", scn);
		return true;
	}
}

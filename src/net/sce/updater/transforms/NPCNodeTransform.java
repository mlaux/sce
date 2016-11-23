package net.sce.updater.transforms;

import java.util.List;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Dependent;
import net.sce.bytecode.Hook;
import net.sce.bytecode.SCEClassNode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

@Dependent({ NPCTransform.class, NodeTransform.class })
public class NPCNodeTransform extends BytecodeTask {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		String npcname = "L" + getIdentifiedClass("NPC").name + ";";
		for (SCEClassNode scn : all) {
			if(!scn.superName.equals(getIdentifiedClass("Node").name))
				continue;
			if(scn.instanceFields.size() > 1) continue;
			for (FieldNode fn : scn.fields) {
				if((fn.access & Opcodes.ACC_STATIC) == 0 && fn.desc.equals(npcname))
					result.add(scn);
			}
		}
	}

	
	public boolean transform(SCEClassNode scn, List<Hook> results) {
		if(scn == null) return false;
		identifyClass("NPCNode", scn);
		String npcname = "L" + getIdentifiedClass("NPC").name + ";";
		for (FieldNode fn : scn.fields) {
			if((fn.access & Opcodes.ACC_STATIC) == 0 && fn.desc.equals(npcname))
				results.add(new Hook("npcNode.npc", scn, fn));
		}
		return true;
	}
}

package net.sce.updater.transforms;

import java.util.List;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.SCEClassNode;

public class InterfaceNodeTransform extends BytecodeTask {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		// for(SCEClassNode scn : all) {
		// 	for(MethodNode mn : scn.methods) {
				// InstructionSearcher is = new InstructionSearcher(mn);
				// TODO finish this
		// 	}
		// }
	}
	
	public boolean transform(SCEClassNode scn, List<Hook> results) {
		identifyClass("InterfaceNode", scn);
		return true;  //To change body of implemented methods use File | Settings | File Templates.
	}
}

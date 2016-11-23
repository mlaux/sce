package net.sce.updater.transforms;

import java.util.List;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.SCEClassNode;

public class ItemTransform extends BytecodeTask {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for(SCEClassNode scn : all) {
			if(scn.constants.get("<col=00ffff>") != null) {
				result.add(scn);
			}
		}
	}

	
	public boolean transform(SCEClassNode scn, List<Hook> results) {
		identifyClass("Item", scn);
		return true;  //To change body of implemented methods use File | Settings | File Templates.
	}
}

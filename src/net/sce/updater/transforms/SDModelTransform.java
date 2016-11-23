package net.sce.updater.transforms;

import java.util.List;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.RegexSearcher;
import net.sce.bytecode.SCEClassNode;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class SDModelTransform extends BytecodeTask {
	private static final String xyzPoints = "aload getfield iload dup2 iaload";
	private static final String indices = "aload getfield iload saload iaload";
	private static final String[] namesPts = { "pointsX", "pointsY", "pointsZ" };
	private static final String[] namesIndices = { "indices1", "indices2", "indices3" };
    
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for(SCEClassNode scn : all) {
			if(scn.constants.containsKey("FMT"))
				result.add(scn);
		}
	}
	
	public boolean transform(SCEClassNode scn, List<Hook> results) {
		identifyClass("SDModel", scn);
		boolean foundPoints = false;
		boolean foundIndices = false;
		for(MethodNode mn : scn.methods) {
			RegexSearcher rs = new RegexSearcher(mn);
			if(!foundPoints)
				foundPoints = findPoints(rs, results);
			if(!foundIndices)
				foundIndices = findIndices(rs, results);
		}
		identifyClass("Model", classes.get(scn.superName));
		return foundPoints && foundIndices;
	}
	
	private boolean findPoints(RegexSearcher rs, List<Hook> results) {
		int identified = 0;
		AbstractInsnNode[] insns;
		while((insns = rs.nextMatch(xyzPoints)) != null) {
			results.add(new Hook("sdModel." + namesPts[identified++], (FieldInsnNode) insns[1]));
			if(identified == 3)
				return true;
		}
		return false;
	}
	
	private boolean findIndices(RegexSearcher rs, List<Hook> results) {
		int identified = 0;
		AbstractInsnNode[] insns;
		while((insns = rs.nextMatch(indices)) != null) {
			results.add(new Hook("sdModel." + namesIndices[identified++], (FieldInsnNode) insns[1]));
			if(identified == 3)
				return true;
		}
		return false;
	}
}

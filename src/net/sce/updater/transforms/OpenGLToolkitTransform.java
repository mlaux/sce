package net.sce.updater.transforms;

import java.util.List;
import java.util.Set;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.SCEClassNode;
import net.sce.bytecode.SCEMethodNode;

import org.objectweb.asm.tree.FieldNode;

public class OpenGLToolkitTransform extends BytecodeTask {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for(SCEClassNode scn : all) {
			Set<SCEMethodNode> methodNodes = scn.constants.get("GL_ARB_texture_non_power_of_two");
			if(methodNodes != null)
				result.add(scn);
		}
	}

	
	public boolean transform(SCEClassNode scn, List<Hook> results) {
		identifyClass("OpenGLToolkit", scn);
		for(FieldNode fn : scn.fields) {
			if(fn.desc.equals("Ljaggl/OpenGL;"))
				results.add(new Hook("openGLToolkit.openGL", scn, fn));
		}
		return true;
	}
}

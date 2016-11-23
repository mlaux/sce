package net.sce.updater.transforms;

import java.util.List;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.SCEClassNode;

import org.objectweb.asm.tree.FieldNode;

public class DirectXTransform extends BytecodeTask {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for(SCEClassNode sce : all) {
			for(FieldNode f : sce.fields) {
				if(f.desc.equals("Ljagdx/D3DPRESENT_PARAMETERS;")) {
					result.add(sce);
				}
			}
		}
	}

	
	public boolean transform(SCEClassNode scn, List<Hook> results) {
		identifyClass("DirectXToolkit", scn);
		for(FieldNode f : scn.fields) {
			if(f.desc.equals("Ljagdx/IDirect3D;"))
				results.add(new Hook("directXToolkit.direct3D", scn, f));
			else if(f.desc.equals("Ljagdx/GeometryBuffer;"))
				results.add(new Hook("directXToolkit.geometryBuffer", scn, f));
			else if(f.desc.equals("Ljagdx/D3DCAPS;"))
				results.add(new Hook("directXToolkit.direct3DCaps", scn, f));
			else if(f.desc.equals("Ljagdx/PixelBuffer;"))
				results.add(new Hook("directXToolkit.pixelBuffer", scn, f));	
			else if(f.desc.equals("Ljagdx/IDirect3DDevice;"))
				results.add(new Hook("directXToolkit.direct3DDevice", scn, f));
			else if(f.desc.equals("Ljagdx/D3DPRESENT_PARAMETERS;"))
				results.add(new Hook("directXToolkit.direct3DParameters", scn, f));
		}
		return true;
	}
}

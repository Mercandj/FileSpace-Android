/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package mercandalli.com.filespace.physics.lib;

public class IndicesVertices {
	public float[] vertices;
	public short[] indices;
	
	public 	myVector3D 				edgeVerticeMin		= null;
	public 	myVector3D 				edgeVerticeMax		= null;
	
	public IndicesVertices(float[] vertices, short[] indices) {
		super();
		this.vertices = vertices;
		this.indices = indices;
	}
	
	public IndicesVertices(float[] vertices, short[] indices, myVector3D edgeVerticeMin, myVector3D edgeVerticeMax) {
		super();
		this.vertices = vertices;
		this.indices = indices;
		this.edgeVerticeMin = new myVector3D(edgeVerticeMin);
		this.edgeVerticeMax = new myVector3D(edgeVerticeMax);
	}
	
	public IndicesVertices(IndicesVertices indicesVertices) {
		super();
		vertices = new float[indicesVertices.vertices.length];
		indices = new short[indicesVertices.indices.length];
		int max = Math.max(indicesVertices.vertices.length, indicesVertices.indices.length);
		for(int i = 0; i<max;i++) {
			if(i<indicesVertices.vertices.length)
				vertices[i] = indicesVertices.vertices[i];
			if(i<indicesVertices.indices.length)
				indices[i] = indicesVertices.indices[i];
		}
		this.edgeVerticeMin = new myVector3D(indicesVertices.edgeVerticeMin);
		this.edgeVerticeMax = new myVector3D(indicesVertices.edgeVerticeMax);
	}
}

/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

precision mediump float;
uniform int isBumpMapping;
uniform mat4 uMVPMatrix;
uniform mat4 uNMatrix;
uniform mat4 uMVMatrix;
uniform vec4 vColor;
uniform sampler2D texMap;
uniform sampler2D texMap_bump;
varying vec3 mynormal;
varying vec3 mytangent;
varying vec4 myvertex;
varying vec2 mytexturecoords;

void main() {
	vec4 texture = texture2D(texMap, mytexturecoords);

	vec3 eyepos = vec3(0,0,0); 	
	vec4 _mypos = uMVMatrix * myvertex;
	vec3 mypos = _mypos.xyz / _mypos.w;	
	
	vec4 _lightpos = vec4(0,10,15,1); // x->java_x, y->-z_java, z->y_java (hauteur) /2
	_lightpos = uMVMatrix * _lightpos;
	vec3 lightpos = _lightpos.xyz / _lightpos.w;	
	
	vec3 normal = normalize(vec3(uNMatrix * vec4(mynormal,0)));	
	vec3 eyedir = normalize(eyepos - mypos);
	vec3 lightdir = normalize (lightpos - mypos);	
	vec3 reflectdir = normalize( reflect(-lightdir, normal) );
	
	float diffuse = max( dot(lightdir, normal), 0.0);
	float specular = pow(max( dot(reflectdir,eyedir), 0.0), 50.0);
	
	if(isBumpMapping==1) {
		// # In your fragment shader, compute the matrix to transform the light direction, and the eye direction from the world space to tangent space.		
		vec3 n = normalize (vec3(uNMatrix * vec4(mynormal, 0)));
		vec3 t = normalize (vec3(uNMatrix * vec4(mytangent, 0)));
		vec3 b = normalize (cross(n,t));		
		//mat3 in_m = mat3(t,b,n); mat3 out_m = transpose(in_m);
		mat3 out_m = mat3(
			vec3(t.x, b.x, n.x),
			vec3(t.y, b.y, n.y),
			vec3(t.z, b.z, n.z));
		
		
		// Use the matrix out_m to transform light and eye directions to tangent space.		
		lightdir = out_m * lightdir;
		eyedir = out_m * eyedir;
	
		// # In your fragment shader, then read the perturbed normals from the normal texture map, and the diffuse coefficients from the diffuse texture map.
		normal = normalize (2.0 * texture2D(texMap_bump, mytexturecoords).rgb - 1.0);
		diffuse = max( dot(lightdir, normal), 0.0);
		
		reflectdir = normalize( reflect(-lightdir, normal) );
		specular = pow(max( dot(reflectdir,eyedir), 0.0), 50.0);
	}
	
	gl_FragColor =  vec4(0.1,0.1,0.1,1) + (texture) * (diffuse + specular);
}
/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

uniform int isBumpMapping;
uniform mat4 uMVPMatrix;
uniform mat4 uNMatrix;
uniform mat4 uMVMatrix;
attribute vec3 vNormal;
attribute vec3 vTangent;
attribute vec4 vPosition;
attribute vec2 vTexturecoords;
varying vec3 mynormal;
varying vec3 mytangent;
varying vec4 myvertex;
varying vec2 mytexturecoords;

void main() {
	gl_Position = uMVPMatrix * vPosition;
	mynormal = vNormal;
	mytangent = vTangent;
	myvertex = vPosition;
	mytexturecoords = vTexturecoords;
}
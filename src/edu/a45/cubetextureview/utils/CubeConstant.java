package edu.a45.cubetextureview.utils;

/**
 * Created by w1 on 12/11/2014.
 */

/**
 * Contains cube necessary constants:
 * vertices, texture coordinates, indices
 */
public class CubeConstant {
    // cube vertices
	public static float[] vertices = {
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f,  1.0f, 1.0f,
            1.0f,  1.0f, 1.0f,

            1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f,  1.0f, 1.0f,
            1.0f,  1.0f, -1.0f,

            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,

            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,

            -1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
    };

    // texture coordinates
    public static float[] texture = {
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
    };

    // faces definition
    public static byte[] indices = {
            0,1,3,  0,3,2,  // front
            4,5,7,  4,7,6,  // right
            8,9,11, 8,11,10,
            12,13,15,   12,15,14,
            16,17,19,   16,19,18,
            20,21,23,   20,23,22,
    };
}

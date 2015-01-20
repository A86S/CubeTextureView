package edu.a45.cubetextureview.utils;

import android.util.Log;

import static android.opengl.GLES20.*;

/**
 * Created by w1 on 12/11/2014.
 */

/**
 * Handle shader compilation, linking and verification
 */
public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    /**
     * Loads and compiles a vertex shader, returning the OpenGL object 3D
     * @param source shader source code
     * @return OpenGL object ID
     */
    public static int compileVertexShader(String source){
        return compileShader(GL_VERTEX_SHADER, source);
    }

    /**
     * Loads and compiles a fragment shader, returning the OpenGL object 3D
     * @param source shader source code
     * @return OpenGL object ID
     */
    public static int compileFragmentShader(String source){
        return compileShader(GL_FRAGMENT_SHADER, source);
    }

    /**
     * Compiles a shader, returning the OpenGL object ID.
     * @param type type of shader
     * @param source shader source code
     * @return OpenGL object ID
     */
    private static int compileShader(int type, String source) {
        final int shaderObjectId = glCreateShader(type);
        if(shaderObjectId == 0){
            if(LoggerConfig.ON){
                Log.w(TAG, "Could not create new shader");
            }
            return 0;
        }

        glShaderSource(shaderObjectId,source);
        glCompileShader(shaderObjectId);

        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        if(LoggerConfig.ON){
            Log.v(TAG, "Results of compiling source:"
                    +"\n"+source+"\n:"
                    +glGetShaderInfoLog(shaderObjectId));
        }

        if(compileStatus[0] == 0){ // fail
            glDeleteShader(shaderObjectId);
            if(LoggerConfig.ON){
                Log.w(TAG, "Compilation of shader failed.");
            }
            return 0;
        }

        return shaderObjectId;
    }

    /**
     * Links a vertex shader and a fragment shader together into an OpenGL
     * program.
     * @param vertexShaderId
     * @param fragmentShaderId
     * @return the OpenGL program object ID, or 0 if linking failed.
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId){
        final int programObjectId = glCreateProgram();

        if(programObjectId == 0){
            if(LoggerConfig.ON){
                Log.w(TAG, "Could not create new program");
            }
            return 0;
        }

        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);
        glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        if(LoggerConfig.ON){
            Log.v(TAG, "Results of linking program:\n"
                            +glGetProgramInfoLog(programObjectId)
            );
        }

        if(linkStatus[0] == 0){
            glDeleteProgram(programObjectId);
            if(LoggerConfig.ON){
                Log.w(TAG, "Linking of program failed.");
            }
            return 0;
        }

        return programObjectId;
    }

    /**
     * Validates an OpenGL program.
     * Should only be called when developing the application.
     * @param programObjectId
     * @return
     */
    public static boolean validateProgram(int programObjectId){
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        if(LoggerConfig.ON){
            Log.v(TAG, "Results of validating program: "+validateStatus[0]
                            + "\nLog:"+glGetProgramInfoLog(programObjectId)
            );
        }
        return validateStatus[0] != 0;
    }
}

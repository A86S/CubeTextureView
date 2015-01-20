package edu.a45.cubetextureview;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import edu.a45.cubetextureview.utils.CubeConstant;
import edu.a45.cubetextureview.utils.LoggerConfig;
import edu.a45.cubetextureview.utils.ShaderHelper;
import edu.a45.cubetextureview.utils.TextResourceReader;
import edu.a45.cubetextureview.utils.TextureHelper;
import edu.a45.cubetextureview.utils.TextureSurfaceRenderer;
import edu.a45.cubetextureview.utils.TweenFloat;

@SuppressLint("NewApi")
public class CubeRenderer extends TextureSurfaceRenderer {

	private Context ctx;
	private FloatBuffer textureBuffer;
	private FloatBuffer vertexBuffer;
	private ByteBuffer indexBuffer;
	private int[] textures = new int[1];

	private int shaderProgram;
	private SurfaceTexture cubeTexture;
	
	private int cubeWidth;
	private int cubeHeight;
	private boolean adjustViewport = false;

	SurfaceTexture texture;
	
	private int[] resourceIds = new int[6];
	private String[] labels = new String[6];

	private float[] projectionMatrix = new float[16];
	private float[] savedViewMatrix = new float[16];
	private float[] viewProjectionMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private float[] rotateMatrix = new float[16];

	private int program;
	private int uMatrixLocation;
	private int uTextureUnitLocation;
	private int aPositionLocation;
	private int aTextureCoordinatesLocation;

	private boolean rolling;
	private TweenFloat tween = null;
	private float theta;
	private boolean draggingX;

	public CubeRenderer(Context context, SurfaceTexture texture, int width,
			int height) {
		super(texture, width, height);
		this.texture = texture;
		this.ctx = context;
		
		labels = new String[] { "Side A", "side B",
				"side C", "side D", "side E", "side F" };

		resourceIds = new int[] { R.drawable.side0,
				R.drawable.side1, R.drawable.side2,
				R.drawable.side3, R.drawable.side4,
				R.drawable.side5 };
		}

	private void adjustViewport() {
		glViewport(0, 0, width, height);
		// set projection matrix
		orthoM(projectionMatrix, 0, -1f, 1f, -1f, 1f, -5f, 5f);
		adjustViewport = false;
	}

	@Override
	protected boolean draw() {

		Log.d("###", "draw");

		if (adjustViewport) {
			adjustViewport();
		}

		// clear color and depth buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (rolling) {
			// step if rolling
			tween.step(0.03f);
			theta = tween.getValue();
		}

		setIdentityM(rotateMatrix, 0);
		if (draggingX) {
			rotateM(rotateMatrix, 0, theta, 1f, 0f, 0f);
		} else {
			rotateM(rotateMatrix, 0, theta, 0f, 1f, 0f);
		}
		multiplyMM(viewMatrix, 0, rotateMatrix, 0, savedViewMatrix, 0);
		if (rolling) {
			if (tween.isDone()) {
				// save when rolling is done.
				copyMatrix(viewMatrix, savedViewMatrix);
				theta = 0;
				rolling = false;
			}
		}
		// send transform matrix to shader
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		glUniformMatrix4fv(uMatrixLocation, 1, false, viewProjectionMatrix, 0);
		glActiveTexture(GL_TEXTURE0);

		for (int i = 0; i < 6; i++) {
			// draw each side

			glBindTexture(GL_TEXTURE_2D, textures[i]);
			// read from texture unit location 0
			glUniform1i(uTextureUnitLocation, 0);
			indexBuffer.position(6 * i);
			glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, indexBuffer);
		}
		glBindTexture(GL_TEXTURE_2D, 0); // unbind

		return true;
	}

	@Override
	protected void initGLComponents() {
		setupVertexBuffer();
		// setupTexture(ctx);
		loadShaders(ctx);

		GLES20.glClearColor(.0f, 1.0f, .0f, .0f);
		GLES20.glEnable(GL_DEPTH_TEST);

		setIdentityM(viewMatrix, 0);
		copyMatrix(viewMatrix, savedViewMatrix);

		if (LoggerConfig.ON) {
			ShaderHelper.validateProgram(program);
		}

		GLES20.glUseProgram(program);
		// get shader variable address
		uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
		uTextureUnitLocation = glGetUniformLocation(program, "u_TextureUnit");
		aPositionLocation = glGetAttribLocation(program, "a_Position");
		aTextureCoordinatesLocation = glGetAttribLocation(program,
				"a_TextureCoordinates");

		// fill shader attributes
		vertexBuffer.position(0);
		glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0,
				vertexBuffer);

		glEnableVertexAttribArray(aPositionLocation);
		textureBuffer.position(0);
		glVertexAttribPointer(aTextureCoordinatesLocation, 2, GL_FLOAT, false,
				0, textureBuffer);
		glEnableVertexAttribArray(aTextureCoordinatesLocation);

		// load textures
		textures = new int[resourceIds.length];
		for (int i = 0; i < resourceIds.length; i++) {
			textures[i] = TextureHelper.createTexture(ctx, resourceIds[i],
					labels[i], cubeWidth, cubeHeight, false);
		}

	}

	@Override
	protected void deinitGLComponents() {
		GLES20.glDeleteTextures(1, textures, 0);
		GLES20.glDeleteProgram(shaderProgram);
		cubeTexture.release();
		cubeTexture.setOnFrameAvailableListener(null);
	}

	public void setCubeSize(int width, int height) {
		this.cubeWidth = width;
		this.cubeHeight = height;
		adjustViewport = true;
	}

	@SuppressWarnings("unused")
	private void setupTexture(Context context) {

		// Generate the actual texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glGenTextures(1, textures, 0);
		checkGlError("Texture generate");

		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
		checkGlError("Texture bind");

		cubeTexture = new SurfaceTexture(textures[0]);
	}

	private void loadShaders(Context context) {

		program = ShaderHelper
				.linkProgram(ShaderHelper
						.compileVertexShader(TextResourceReader
								.readTextFileFromResource(context,
										R.raw.cube_vertex)), ShaderHelper
						.compileFragmentShader(TextResourceReader
								.readTextFileFromResource(context,
										R.raw.cube_fragment)));

	}

	private void setupVertexBuffer() {
		// Draw list buffer
		// vertices
		vertexBuffer = ByteBuffer
				.allocateDirect(CubeConstant.vertices.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(CubeConstant.vertices);
		vertexBuffer.position(0);

		// texture map
		textureBuffer = ByteBuffer
				.allocateDirect(CubeConstant.texture.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		textureBuffer.put(CubeConstant.texture);
		textureBuffer.position(0);

		// indices
		indexBuffer = ByteBuffer.allocateDirect(CubeConstant.indices.length);
		indexBuffer.put(CubeConstant.indices);
		indexBuffer.position(0);
	}

	public void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("SurfaceTest",
					op + ": glError " + GLUtils.getEGLErrorString(error));
		}
	}

	public SurfaceTexture getCubeTexture() {
		return cubeTexture;
	}

	private void copyMatrix(float[] src, float[] dst) {
		for (int i = 0; i < dst.length; i++) {
			dst[i] = src[i];
		}
	}
	
	
	public void dragEnd() {
		rolling = true;

		float supposed_start = 0f;
		float theta_target;
		if (Math.abs(theta) < 45f) {
			theta_target = 0f;
			if (theta < 0f) {
				supposed_start = -90f;
			} else {
				supposed_start = 90f;
			}
		} else {
			theta_target = 90f * Math.signum(theta);
		}
		tween = new TweenFloat(supposed_start, theta_target, theta);
	}

	public boolean dragStart(boolean xDirection) {
		if (!rolling) {
			draggingX = !xDirection;
			theta = 0;
			return true;
		} else {
			return false;
		}
	}

	public void drag(float angle) {
		theta = angle;
	}



}

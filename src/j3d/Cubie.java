package j3d;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;

public class Cubie extends TransformGroup {
	private static final Color3f Red = new Color3f(1.0f, 0.0f, 0.0f);
	private static final Color3f Green = new Color3f(0.0f, 1.0f, 0.0f);
	private static final Color3f Blue = new Color3f(0.0f, 0.0f, 1.0f);
	private static final Color3f Yellow = new Color3f(1.0f, 1.0f, 0.0f);
	private static final Color3f Orange = new Color3f(1.0f, 0.4f, 0.0f);
	private static final Color3f White = new Color3f(1.0f, 1.0f, 1.0f);
	private static final Color3f[] defaultColor = {Blue, Green, Orange, Yellow, Red, White}; 
	private Vector3d currentAxis;
	private Vector3d ax;
	private Vector3d ay;
	private Vector3d az;
	private Matrix3d mat;
	private double angle;
	private Box cube;
	
	public Cubie(double size, double x, double y, double z) {
		this(size, x, y, z, defaultColor);
	}
	
	public Cubie(double size, double x, double y, double z, Color3f[] colors) {
		super();
		cube = new Box((float)size, (float)size, (float)size, Box.GENERATE_NORMALS, null);
		setDefaultColor(cube, colors);
        Transform3D t3dMove = new Transform3D();
        t3dMove.setTranslation(new Vector3d(x, y, z));
        setTransform(t3dMove);
        addChild(cube);
        setCapability(ALLOW_TRANSFORM_READ);
        setCapability(ALLOW_TRANSFORM_WRITE);
        initAxis();
	}
		
	private void setColor(Box cube, int face, Color3f color) {
		Shape3D shape = cube.getShape(face);
		Appearance ap = (Appearance)shape.getAppearance().cloneNodeComponent(true);
		Material mat = new Material();
		mat.setDiffuseColor(color);
		//mat.setEmissiveColor(color);
		//mat.setSpecularColor(color);
		ap.setMaterial(mat);
		shape.setAppearance(ap);
	}
	
	private void setDefaultColor(Box cube, Color3f[] colors) {
		setColor(cube, Box.TOP, colors[0]);
		setColor(cube, Box.FRONT, colors[1]);
		setColor(cube, Box.RIGHT, colors[2]);
		setColor(cube, Box.BACK, colors[3]);
		setColor(cube, Box.LEFT, colors[4]);
		setColor(cube, Box.BOTTOM, colors[5]);
	}

	private void initAxis() {
		ax = new Vector3d(1, 0, 0);
		ay = new Vector3d(0, 1, 0);
		az = new Vector3d(0, 0, 1);
	}

	public void start(Command com, double angle) {
		double sin;
		double cos;
		switch (com) {
		case U1:
		case U2:
		case U3:
			this.angle = angle;
			sin = Math.sin(this.angle);
			cos = Math.cos(this.angle);
			mat = new Matrix3d(cos, 0, -sin,
					           0,   1,    0,
					           sin, 0, cos);
			currentAxis = ay;
			break;
		case R1:
		case R2:
		case R3:
			this.angle = angle;
			sin = Math.sin(-this.angle);
			cos = Math.cos(-this.angle);
			currentAxis = ay;
			mat = new Matrix3d(1,   0,    0,
					           0, cos, -sin,
					           0, sin,  cos);
			currentAxis = ax;
			break;
		case F1:
		case F2:
		case F3:
		default:
			this.angle = angle;
			sin = Math.sin(-this.angle);
			cos = Math.cos(-this.angle);
			currentAxis = ay;
			mat = new Matrix3d(cos, -sin, 0,
					           sin,  cos, 0,
					             0,    0, 1);
			currentAxis = az;
		}
	}
	
	public void stop(Command com) {
		Vector3d tmp;
		switch (com) {
		case U1:
			tmp = az;
			az = ax;
			ax = new Vector3d(-tmp.x, -tmp.y, -tmp.z);
			break;
		case U2:
			az = new Vector3d(-az.x, -az.y, -az.z);
			ax = new Vector3d(-ax.x, -ax.y, -ax.z);
			break;
		case U3:
			tmp = az;
			az = new Vector3d(-ax.x, -ax.y, -ax.z);
			ax = tmp;
			break;
		case R1:
			tmp = ay;
			ay = az;
			az = new Vector3d(-tmp.x, -tmp.y, -tmp.z);
			break;
		case R2:
			ay = new Vector3d(-ay.x, -ay.y, -ay.z);
			az = new Vector3d(-az.x, -az.y, -az.z);
			break;
		case R3:
			tmp = ay;
			ay = new Vector3d(-az.x, -az.y, -az.z);
			az = tmp;
			break;
		case F1:
			tmp = ax;
			ax = ay;
			ay = new Vector3d(-tmp.x, -tmp.y, -tmp.z);
			break;
		case F2:
			ax = new Vector3d(-ax.x, -ax.y, -ax.z);
			ay = new Vector3d(-ay.x, -ay.y, -ay.z);
			break;
		case F3:
		default:
			tmp = ax;
			ax = new Vector3d(-ay.x, -ay.y, -ay.z);
			ay = tmp;
		}
	}
	
	public void rotate() {
		AxisAngle4d axis = new AxisAngle4d(currentAxis.x, currentAxis.y, currentAxis.z, -angle);
		Transform3D rot = new Transform3D();
		Matrix3d m = new Matrix3d();
		Matrix3d n = new Matrix3d();
		Vector3d v = new Vector3d();
		rot.setRotation(axis);
		rot.get(n);
		Transform3D tr = new Transform3D();
		getTransform(tr);
		tr.get(m, v);
		mat.transform(v);
		m.mul(n);
		setTransform(new Transform3D(m, v, 1));
	}
}

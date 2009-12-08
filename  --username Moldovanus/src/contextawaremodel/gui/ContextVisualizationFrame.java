package contextawaremodel.gui;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.Sphere;
import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.DirectionalLight;


import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.util.Hashtable;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class ContextVisualizationFrame extends JFrame {

    private Canvas3D canvas3d;
    private SimpleUniverse simpleUniverse;
    private BranchGroup scene;
    private BranchGroup contextObjects;
    private Appearance influenceVolumeAppereance;
    private Appearance sensorAppereance;
    private Hashtable<String, Text2D> textObjects;
    private Hashtable<String, Sphere> sensorObjects;

    public ContextVisualizationFrame(GUIAgent guia) {

        setSize(800, 500);
        setTitle("Context Visualization");
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas3d = new Canvas3D(config) {

            @Override
            public void postRender() {
                J3DGraphics2D g2d = getGraphics2D();
                g2d.setColor(Color.BLACK);
                g2d.drawString("Left Button: Rotate ", 10, 20);
                g2d.drawString("Middle Button: Zoom ", 10, 40);
                g2d.drawString("Right Button: Pan ", 10, 60);
                g2d.flush(true);
            }
        };
        add("Center", canvas3d);

        simpleUniverse = new SimpleUniverse(canvas3d);
        textObjects = new Hashtable<String, Text2D>();
        sensorObjects = new Hashtable<String, Sphere>();
        scene = new BranchGroup();

        TransformGroup vpTrans = simpleUniverse.getViewingPlatform().getViewPlatformTransform();
        BoundingSphere mouseBounds = new BoundingSphere(new Point3d(), 1000);

        TransformGroup objRotate = new TransformGroup();
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRotate.addChild(create3DContext());
        scene.addChild(objRotate);

        // Add rotation using left button drag
        MouseRotate myMouseRotate = new MouseRotate();
        myMouseRotate.setTransformGroup(objRotate);
        myMouseRotate.setSchedulingBounds(mouseBounds);
        scene.addChild(myMouseRotate);

        // Add panning using right button drag
        MouseTranslate myMouseTranslate = new MouseTranslate(MouseBehavior.INVERT_INPUT);
        myMouseTranslate.setTransformGroup(vpTrans);
        myMouseTranslate.setSchedulingBounds(mouseBounds);
        scene.addChild(myMouseTranslate);

        // Add zooming using middle button drag
        MouseZoom myMouseZoom = new MouseZoom(MouseBehavior.INVERT_INPUT);
        myMouseZoom.setTransformGroup(vpTrans);
        myMouseZoom.setSchedulingBounds(mouseBounds);
        scene.addChild(myMouseZoom);

        // Add background color
        Background bg = new Background(1, 1, 1);
        bg.setApplicationBounds(new BoundingSphere());
        scene.addChild(bg);

        // Stepback a little so we can see the whole scene
        Transform3D stepback = new Transform3D();
        stepback.setTranslation(new Vector3d(0, 0, 10));
        vpTrans.setTransform(stepback);

        // Start rendering
        scene.compile();
        simpleUniverse.addBranchGraph(scene);
    }

    private synchronized Node create3DContext() {

        contextObjects = new BranchGroup();
        contextObjects.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        contextObjects.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        // generate the context
        Shape3D contextBox = new Shape3D();

        IndexedQuadArray indexedCube = new IndexedQuadArray(8, IndexedQuadArray.COORDINATES | IndexedQuadArray.NORMALS, 24);
        //The vertex coordinates defined as an array of points.
        Point3f[] cubeCoordinates = {new Point3f(5.0f, 2.5f, 3.5f),
            new Point3f(-5.0f, 2.5f, 3.5f),
            new Point3f(-5.0f, 0.0f, 3.5f),
            new Point3f(5.0f, 0.0f, 3.5f), new Point3f(5.0f, 2.5f, -3.5f),
            new Point3f(-5.0f, 2.5f, -3.5f),
            new Point3f(-5.0f, 0.0f, -3.5f),
            new Point3f(5.0f, 0.0f, -3.5f)};
        //The vertex normals defined as an array of vectors
        Vector3f[] normals = {new Vector3f(0.0f, 0.0f, 1.0f),
            new Vector3f(0.0f, 0.0f, -1.0f),
            new Vector3f(1.0f, 0.0f, 0.0f),
            new Vector3f(-1.0f, 0.0f, 0.0f),
            new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)};
        //Define the indices used to reference vertex array
        int coordIndices[] = {0, 1, 2, 3, 7, 6, 5, 4, 0, 3, 7, 4, 5, 6, 2, 1,
            0, 4, 5, 1, 6, 7, 3, 2};
        //Define the indices used to reference normal array
        int normalIndices[] = {0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3,
            4, 4, 4, 4, 5, 5, 5, 5};
        //Set the data
        indexedCube.setCoordinates(0, cubeCoordinates);
        indexedCube.setNormals(0, normals);
        indexedCube.setCoordinateIndices(0, coordIndices);
        indexedCube.setNormalIndices(0, normalIndices);

        Appearance app = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(0, 0, 0);
        app.setColoringAttributes(ca);
        PolygonAttributes pa = new PolygonAttributes();
        pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        LineAttributes la = new LineAttributes();
        la.setLineWidth(1);
        la.setLinePattern(LineAttributes.PATTERN_DASH);
        app.setLineAttributes(la);
        app.setPolygonAttributes(pa);

        contextBox.setGeometry(indexedCube);
        contextBox.setAppearance(app);

        contextObjects.addChild(contextBox);

        // Add lights
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        //Create the colours and directions
        Color3f lightColour = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f lightDir = new Vector3f(-1.0f, -1.0f, -1.0f);
        Color3f ambientColour = new Color3f(1f, 0.2f, 0.2f);
        //Create the lights
        AmbientLight ambientLight = new AmbientLight(ambientColour);
        ambientLight.setInfluencingBounds(bounds);
        DirectionalLight directionalLight = new DirectionalLight(lightColour,
                lightDir);
        directionalLight.setInfluencingBounds(bounds);

        contextObjects.addChild(ambientLight);
        contextObjects.addChild(directionalLight);

        return contextObjects;
    }

    public synchronized void addSensor(String name, float x, float y, float z, float radius) {
        // Create the two spheres representing the sensor 
        TransformGroup newNode = new TransformGroup();
        Sphere s1 = new Sphere(0.05f, Sphere.ENABLE_APPEARANCE_MODIFY | Sphere.ALLOW_LOCAL_TO_VWORLD_READ | Sphere.ALLOW_CHILDREN_WRITE | Sphere.ALLOW_CHILDREN_READ | Sphere.ALLOW_CHILDREN_EXTEND | Sphere.ALLOW_LOCALE_READ, getSensorAppereance());
        Sphere s2 = new Sphere(radius, getInfluenceVolumeAppereance());

        // Add a node to invert the rotation tranform applied to the world,
        // so that the text will always face the camera
        TransformGroup invertRotation = new TransformGroup();
        invertRotation.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        invertRotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        MouseRotate myMouseRotate = new MouseRotate(MouseBehavior.INVERT_INPUT);
        myMouseRotate.setTransformGroup(invertRotation);
        myMouseRotate.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
        newNode.addChild(myMouseRotate);

        // Create the text element
        Text2D text = new Text2D(name, new Color3f(0, 0, 0), "Helvetica", 60, 0);
        text.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_WRITE);
        text.setCapability(Text2D.ALLOW_GEOMETRY_WRITE);
        text.setCapability(Text2D.ALLOW_GEOMETRY_READ);
        text.setCapability(Text2D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
        text.setCapability(Text2D.ALLOW_GEOMETRY_WRITE);
        //text.setCapability(Text2D.ALLOW_APPEARANCE_WRITE);
        invertRotation.addChild(text);

        // Add the newly created branch to the scene tree
        newNode.addChild(s1);
        newNode.addChild(s2);
        newNode.addChild(invertRotation);

        // Translate it into its specified position
        Transform3D translate = new Transform3D();
        translate.setTranslation(new Vector3d(x, y, z));
        newNode.setTransform(translate);

        BranchGroup newBranch = new BranchGroup();
        newBranch.addChild(newNode);
        newBranch.compile();
        contextObjects.addChild(newBranch);
        textObjects.put(name, text);
        sensorObjects.put(name, s1);
    }

    private Appearance getInfluenceVolumeAppereance() {
        if (influenceVolumeAppereance != null) {
            return influenceVolumeAppereance;
        }
        influenceVolumeAppereance = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(0.0f, 0.5f, 0.0f);
        influenceVolumeAppereance.setColoringAttributes(ca);
        TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.9f);
        influenceVolumeAppereance.setTransparencyAttributes(ta);
        return influenceVolumeAppereance;
    }

    private Appearance getSensorAppereance() {
        if (sensorAppereance != null) {
            return sensorAppereance;
        }
        sensorAppereance = new Appearance();
        sensorAppereance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        sensorAppereance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        sensorAppereance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        sensorAppereance.setCapability(Appearance.ALLOW_TEXGEN_WRITE);
        Material m = new Material();
        m.setDiffuseColor(0.0f, 0.0f, 1.0f);
        m.setAmbientColor(0.0f, 0.0f, 1.0f);
        m.setShininess(20.0f);
        m.setSpecularColor(1.0f, 1.0f, 1.0f);
        sensorAppereance.setMaterial(m);
        return sensorAppereance;
    }

    public void updateText(String name, String newValue) {
        if (!this.isActive()) {
            return;
        }
        if (!textObjects.containsKey(name)) {
            return;
        }
        Text2D text = textObjects.get(name);
        //float f = text.getRectangleScaleFactor();
        try {
            text.setString(name + ": " + newValue);
           // text.setRectangleScaleFactor(f);
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    public void setColor(String name, Boolean alternate) {
        if (!this.isActive()) {
            return;
        }
        if (!textObjects.containsKey(name)) {
            return;
        }
        try {
            Sphere s1 = sensorObjects.get(name);
            if (alternate) {

                Material m = new Material();
                m.setDiffuseColor(1.0f, 0.0f, 0.0f);
                m.setAmbientColor(1.0f, 0.0f, 0.0f);
                m.setShininess(10.0f);
                m.setSpecularColor(1.0f, 0.0f, 0.0f);

                s1.getAppearance().setMaterial(m);
            } else {

                Material m = new Material();
                m.setDiffuseColor(0.0f, 0.0f, 1.0f);
                m.setAmbientColor(0.0f, 0.0f, 1.0f);
                m.setShininess(20.0f);
                m.setSpecularColor(1.0f, 1.0f, 1.0f);
                s1.getAppearance().setMaterial(m);
            }
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    public static void main(String[] args) {
        (new ContextVisualizationFrame(null)).setVisible(true);
    }
}

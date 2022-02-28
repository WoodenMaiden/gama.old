/*******************************************************************************************************
 *
 * AbstractCamera.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.camera;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

import com.jogamp.opengl.GLRunnable;
import com.jogamp.opengl.glu.GLU;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.ICamera;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.PlatformHelper;
import msi.gaml.operators.Maths;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.FLAGS;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.utils.DPIHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class AbstractCamera.
 */
public class AbstractCamera implements ICamera, IMultiListener {

	static {
		DEBUG.ON();
	}

	/** The renderer. */
	protected final IOpenGLRenderer renderer;

	/** The glu. */
	private final GLU glu;

	/** The initialized. */
	protected boolean initialized;

	/** The mouse position. */
	// Mouse
	private final GamaPoint mousePosition = new GamaPoint(0, 0);

	/** The last mouse pressed position. */
	protected final GamaPoint lastMousePressedPosition = new GamaPoint(0, 0);

	/** The first mouse pressed position. */
	protected final GamaPoint firstMousePressedPosition = new GamaPoint(0, 0);

	/** The firsttime mouse down. */
	protected boolean firsttimeMouseDown = true;

	/** The theta. */
	protected double theta;

	/** The phi. */
	protected double phi;

	/** The flipped. */
	protected boolean flipped = false;

	/** The up. */
	protected final GamaPoint up = new GamaPoint();

	/** The keyboard sensivity. */
	private final double _keyboardSensivity = 1d;

	/** The sensivity. */
	private final double _sensivity = 1;

	/** The goes forward. */
	// Mouse and keyboard state
	private boolean goesForward;

	/** The goes backward. */
	private boolean goesBackward;

	/** The strafe left. */
	private boolean strafeLeft;

	/** The strafe right. */
	private boolean strafeRight;

	/** The ROI currently drawn. */
	private volatile boolean ROICurrentlyDrawn = false;

	/** The ctrl pressed. */
	protected boolean ctrlPressed = false;

	/** The shift pressed. */
	protected boolean shiftPressed = false;

	/** The keystone mode. */
	protected boolean keystoneMode = false;

	/** The use num keys. */
	private final boolean useNumKeys = GamaPreferences.Displays.OPENGL_NUM_KEYS_CAM.getValue();

	/** The data. */
	private final LayeredDisplayData data;

	/**
	 * Instantiates a new abstract camera.
	 *
	 * @param renderer2
	 *            the renderer 2
	 */
	public AbstractCamera(final IOpenGLRenderer renderer2) {
		this.data = renderer2.getData();
		this.renderer = renderer2;
		glu = new GLU();
		applyPreset(data.getCameraName());
	}

	@Override
	public void initialize() {
		flipped = false;
		initialized = false;
		// updatePosition();
		// updateTarget();
		data.resetCamera();
		updateSphericalCoordinatesFromLocations();
	}

	/**
	 * Update cartesian coordinates from angles.
	 */
	// @Override
	public void updateCartesianCoordinatesFromAngles() {
		theta = theta % 360;
		phi = phi % 360;
		if (phi <= 0) {
			phi = 0.001;
		} else if (phi >= 180) { phi = 179.999; }
		final double factorT = theta * Maths.toRad;
		final double factorP = phi * Maths.toRad;
		final double cosT = Math.cos(factorT);
		final double sinT = Math.sin(factorT);
		final double cosP = Math.cos(factorP);
		final double sinP = Math.sin(factorP);
		final double radius = data.getDistance();
		GamaPoint target = getTarget();
		data.setCameraPos(new GamaPoint(radius * cosT * sinP + target.x, radius * sinT * sinP + target.y,
				radius * cosP + target.z));
	}

	/**
	 * Update spherical coordinates from locations.
	 */
	public void updateSphericalCoordinatesFromLocations() {
		final GamaPoint p = getPosition().minus(getTarget());
		// setDistance(p.norm());

		theta = Maths.toDeg * Math.atan2(p.y, p.x);
		// See issue on camera_pos
		if (theta == 0) { theta = -90; }
		phi = Maths.toDeg * Math.acos(p.z / data.getDistance());
	}

	/**
	 * Translate camera from screen plan.
	 *
	 * @param xTranslationOnScreen
	 *            the x translation in screen
	 * @param yTranslationOnScreen
	 *            the y translation in screen
	 */
	private void translateCameraFromScreenPlan(final double xTranslationOnScreen, final double yTranslationOnScreen) {

		final double theta_vect_x = -Math.sin(theta * Maths.toRad);
		final double theta_vect_y = Math.cos(theta * Maths.toRad);
		final double theta_vect_ratio =
				xTranslationOnScreen / (theta_vect_x * theta_vect_x + theta_vect_y * theta_vect_y);
		final double theta_vect_x_norm = theta_vect_x * theta_vect_ratio;
		final double theta_vect_y_norm = theta_vect_y * theta_vect_ratio;

		final double phi_vect_x = Math.cos(theta * Maths.toRad) * Math.cos(phi * Maths.toRad);
		final double phi_vect_y = Math.sin(theta * Maths.toRad) * Math.cos(phi * Maths.toRad);
		final double phi_vect_z = -Math.sin(phi * Maths.toRad);
		final double phi_vect_ratio =
				yTranslationOnScreen / (phi_vect_x * phi_vect_x + phi_vect_y * phi_vect_y + phi_vect_z * phi_vect_z);
		final double phi_vect_x_norm = phi_vect_x * phi_vect_ratio;
		final double phi_vect_y_norm = phi_vect_y * phi_vect_ratio;

		final double x_translation_in_world = theta_vect_x_norm + phi_vect_x_norm;
		final double y_translation_in_world = theta_vect_y_norm + phi_vect_y_norm;
		GamaPoint position = getPosition();
		GamaPoint target = getTarget();
		Double distance = data.getDistance();
		data.setCameraPos(new GamaPoint(position.x - x_translation_in_world * distance / 1000,
				position.y - y_translation_in_world * distance / 1000, position.z));
		data.setCameraTarget(new GamaPoint(target.x - x_translation_in_world * distance / 1000,
				target.y - y_translation_in_world * distance / 1000, target.z));

		updateSphericalCoordinatesFromLocations();
	}

	/**
	 * Apply preset.
	 *
	 * @param name
	 *            the name
	 */
	public void applyPreset(final String name) {
		// data.setCameraNameFromUser(name);
		flipped = false;
		initialized = false;
		update();
		data.setZoomLevel(zoomLevel(), true, true);
	}

	@Override
	public void update() {
		updateSphericalCoordinatesFromLocations();
		if (initialized) { drawRotationHelper(); }
		initialized = true;
	}

	/* -------Get commands--------- */

	@Override
	public GamaPoint getPosition() { return data.getCameraPos(); }

	@Override
	public GamaPoint getTarget() { return data.getCameraTarget(); }

	@Override
	public GamaPoint getOrientation() { return up; }

	@Override
	public void animate() {

		if (!data.isLocked()) {
			// And we animate it if the keyboard is invoked
			if (isForward()) {
				if (ctrlPressed) {
					if (flipped) {
						if (phi - getKeyboardSensivity() * getSensivity() > 0) {
							phi -= getKeyboardSensivity() * getSensivity();
						} else {
							phi = -phi + getKeyboardSensivity() * getSensivity();
							flipped = false;
							theta += 180;
						}
					} else if (phi + getKeyboardSensivity() * getSensivity() < 180) {
						phi += getKeyboardSensivity() * getSensivity();
					} else {
						phi = 360 - phi - getKeyboardSensivity() * getSensivity();
						flipped = true;
						theta += 180;
					}
					updateCartesianCoordinatesFromAngles();
				} else if (flipped) {
					translateCameraFromScreenPlan(0.0, getKeyboardSensivity() * getSensivity());
				} else {
					translateCameraFromScreenPlan(0.0, -getKeyboardSensivity() * getSensivity());
				}
			}
			if (isBackward()) {
				if (ctrlPressed) {
					if (flipped) {
						if (phi + getKeyboardSensivity() * getSensivity() < 180) {
							phi += getKeyboardSensivity() * getSensivity();
						} else {
							phi = 360 - phi - getKeyboardSensivity() * getSensivity();
							flipped = false;
							theta += 180;
						}
					} else if (phi - getKeyboardSensivity() * getSensivity() > 0) {
						phi -= getKeyboardSensivity() * getSensivity();
					} else {
						phi = -phi + getKeyboardSensivity() * getSensivity();
						flipped = true;
						theta += 180;
					}
					updateCartesianCoordinatesFromAngles();
				} else if (flipped) {
					translateCameraFromScreenPlan(0.0, -getKeyboardSensivity() * getSensivity());
				} else {
					translateCameraFromScreenPlan(0.0, getKeyboardSensivity() * getSensivity());
				}
			}
			if (isStrafeLeft()) {
				if (ctrlPressed) {
					if (flipped) {
						theta = theta + -getKeyboardSensivity() * getSensivity();
					} else {
						theta = theta - -getKeyboardSensivity() * getSensivity();
					}
					updateCartesianCoordinatesFromAngles();
				} else if (flipped) {
					translateCameraFromScreenPlan(getKeyboardSensivity() * getSensivity(), 0.0);
				} else {
					translateCameraFromScreenPlan(-getKeyboardSensivity() * getSensivity(), 0.0);
				}
			}
			if (isStrafeRight()) {
				if (ctrlPressed) {
					if (flipped) {
						theta = theta + getKeyboardSensivity() * getSensivity();
					} else {
						theta = theta - getKeyboardSensivity() * getSensivity();
					}
					updateCartesianCoordinatesFromAngles();
				} else if (flipped) {
					translateCameraFromScreenPlan(-getKeyboardSensivity() * getSensivity(), 0.0);
				} else {
					translateCameraFromScreenPlan(getKeyboardSensivity() * getSensivity(), 0.0);
				}
			}
		}

		// Completely recomputes the up-vector
		double tr = theta * Maths.toRad;
		double pr = phi * Maths.toRad;
		GamaPoint position = data.getCameraPos();
		GamaPoint target = data.getCameraTarget();
		double cp = Math.cos(pr);
		up.setLocation(-Math.cos(tr) * cp, -Math.sin(tr) * cp, Math.sin(pr));
		if (flipped) { up.negate(); }
		// DEBUG.OUT(
		// "Position " + position.rounded() + " target " + target.rounded() + " up " + up + " flipped " + flipped);
		glu.gluLookAt(position.x, position.y, position.z, target.x, target.y, target.z, up.x, up.y, up.z);
	}

	/*------------------ Events controls ---------------------*/

	/**
	 * Sets the shift pressed.
	 *
	 * @param value
	 *            the new shift pressed
	 */
	final void setShiftPressed(final boolean value) { shiftPressed = value; }

	/**
	 * Sets the ctrl pressed.
	 *
	 * @param value
	 *            the new ctrl pressed
	 */
	final void setCtrlPressed(final boolean value) { ctrlPressed = value; }

	/**
	 * Sets the mouse left pressed.
	 *
	 * @param b
	 *            the new mouse left pressed
	 */
	protected void setMouseLeftPressed(final boolean b) {}

	/**
	 * Invoke on GL thread.
	 *
	 * @param runnable
	 *            the runnable
	 */
	protected void invokeOnGLThread(final GLRunnable runnable) {
		// Fixing issue #2224
		// runnable.run(renderer.getCanvas());
		renderer.getCanvas().invoke(false, runnable);
	}

	/**
	 * Method mouseScrolled()
	 *
	 * @see org.eclipse.swt.events.MouseWheelListener#mouseScrolled(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseScrolled(final MouseEvent e) {
		invokeOnGLThread(drawable -> {
			if (!data.isLocked()) { internalMouseScrolled(e.count); }
			return false;
		});

	}

	@Override
	public final void mouseWheelMoved(final com.jogamp.newt.event.MouseEvent e) {
		invokeOnGLThread(drawable -> {
			if (!data.isLocked()) { internalMouseScrolled((int) e.getRotation()[1]); }
			return false;
		});
	}

	/**
	 * Internal mouse scrolled.
	 *
	 * @param e
	 *            the e
	 */
	protected final void internalMouseScrolled(final int count) {
		zoom(count > 0);
	}

	/**
	 * Method mouseMove()
	 *
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseMove(final org.eclipse.swt.events.MouseEvent e) {

		invokeOnGLThread(drawable -> {
			internalMouseMove(autoScaleUp(e.x), autoScaleUp(e.y), e.button, (e.stateMask & SWT.BUTTON_MASK) != 0,
					GamaKeyBindings.ctrl(e), GamaKeyBindings.shift(e));
			return false;
		});

	}

	/**
	 * Auto scale up.
	 *
	 * @param nb
	 *            the nb
	 * @return the int
	 */
	private int autoScaleUp(final int nb) {
		if (FLAGS.USE_NATIVE_OPENGL_WINDOW) return nb;
		return DPIHelper.autoScaleUp(nb);
	}

	@Override
	public final void mouseMoved(final com.jogamp.newt.event.MouseEvent e) {
		invokeOnGLThread(drawable -> {
			internalMouseMove(autoScaleUp(e.getX()), autoScaleUp(e.getY()), e.getButton(), e.getButton() > 0,
					isControlDown(e), e.isShiftDown());
			return false;
		});
	}

	/**
	 * Checks if is control down.
	 *
	 * @param e
	 *            the e
	 * @return true, if is control down
	 */
	private boolean isControlDown(final com.jogamp.newt.event.MouseEvent e) {
		return e.isControlDown() || PlatformHelper.isMac() && e.isMetaDown();
	}

	/**
	 * Checks if is control down.
	 *
	 * @param e
	 *            the e
	 * @return true, if is control down
	 */
	private boolean isControlDown(final com.jogamp.newt.event.KeyEvent e) {
		return e.isControlDown() || PlatformHelper.isMac() && e.isMetaDown();
	}

	@Override
	public final void mouseDragged(final com.jogamp.newt.event.MouseEvent e) {
		mouseMoved(e);
	}

	/**
	 * Internal mouse move.
	 *
	 * @param x
	 *            the x already scaled
	 * @param y
	 *            the y already scaled
	 * @param button
	 *            the button 0 for no activity
	 * @param isCtrl
	 *            the is ctrl
	 * @param isShift
	 *            the is shift
	 */
	protected void internalMouseMove(final int x, final int y, final int button, final boolean buttonPressed,
			final boolean isCtrl, final boolean isShift) {

		// Do it before the mouse position is newly set (in super.internalMouseMove)
		if (keystoneMode) {
			final int selectedCorner = getRenderer().getKeystoneHelper().getCornerSelected();
			if (selectedCorner != -1) {
				final GamaPoint origin = getNormalizedCoordinates(getMousePosition().x, getMousePosition().y);
				GamaPoint p = getNormalizedCoordinates(x, y);
				final GamaPoint translation = origin.minus(p).yNegated();
				p = getRenderer().getKeystoneHelper().getKeystoneCoordinates(selectedCorner).plus(-translation.x,
						translation.y, 0);
				getRenderer().getKeystoneHelper().setKeystoneCoordinates(selectedCorner, p);
			} else {
				final int cornerSelected = hoverOnKeystone(x, y);
				getRenderer().getKeystoneHelper().setCornerHovered(cornerSelected);
			}
			mousePosition.x = x;
			mousePosition.y = y;
			setCtrlPressed(isCtrl);
			setShiftPressed(isShift);
			return;
		}
		mousePosition.x = x;
		mousePosition.y = y;
		setCtrlPressed(isCtrl);
		setShiftPressed(isShift);

		if (!buttonPressed) return;
		final GamaPoint newPoint = new GamaPoint(x, y);
		if (!data.isLocked() && isCtrl) {
			final int horizMovement = (int) (newPoint.x - lastMousePressedPosition.x);
			final int vertMovement = (int) (newPoint.y - lastMousePressedPosition.y);
			// if (flipped) {
			// horizMovement = -horizMovement;
			// vertMovement = -vertMovement;
			// }

			final double horizMovement_real = horizMovement;
			final double vertMovement_real = vertMovement;

			lastMousePressedPosition.setLocation(newPoint);
			theta = theta - horizMovement_real * getSensivity();

			if (flipped) {
				if (vertMovement_real > 0) {
					// down drag : phi increase
					if (phi + vertMovement_real * getSensivity() < 180) {
						phi += vertMovement_real * getSensivity();
					} else {
						phi = +360 + phi - vertMovement_real * getSensivity();
						flipped = !flipped;
						theta += 180;
					}
				} else // up drag : phi decrease
				if (phi - -vertMovement_real * getSensivity() > 0) {
					phi -= -vertMovement_real * getSensivity();
				} else {
					phi = -phi + -vertMovement_real * getSensivity();
					flipped = !flipped;
					theta += 180;
				}
			} else if (vertMovement_real > 0) {
				// down drag : phi decrease
				if (phi - vertMovement_real * getSensivity() > 0) {
					phi -= vertMovement_real * getSensivity();
				} else {
					phi = -phi + vertMovement_real * getSensivity();
					flipped = !flipped;
					theta += 180;
				}
			} else // up drag : phi increase
			if (phi + -vertMovement_real * getSensivity() < 180) {
				phi += -vertMovement_real * getSensivity();
			} else {
				phi = +360 + phi - vertMovement_real * getSensivity();
				flipped = !flipped;
				theta += 180;
			}

			// phi = phi - vertMovement_real * get_sensivity();
			updateCartesianCoordinatesFromAngles();
		} else if (shiftPressed && isViewInXYPlan()) {
			getMousePosition().x = x;
			getMousePosition().y = y;
			getRenderer().getOpenGLHelper().defineROI(
					new GamaPoint(firstMousePressedPosition.x, firstMousePressedPosition.y),
					new GamaPoint(getMousePosition().x, getMousePosition().y));
		} else if (getRenderer().getOpenGLHelper()
				.mouseInROI(new GamaPoint(getMousePosition().x, getMousePosition().y))) {
			GamaPoint p = getRenderer().getRealWorldPointFromWindowPoint(getMousePosition());
			p = p.minus(getRenderer().getOpenGLHelper().getROIEnvelope().centre());
			getRenderer().getOpenGLHelper().getROIEnvelope().translate(p.x, p.y);
		} else if (!data.isLocked()) {
			int horizMovement = (int) (x - lastMousePressedPosition.x);
			int vertMovement = (int) (y - lastMousePressedPosition.y);
			if (flipped) {
				horizMovement = -horizMovement;
				vertMovement = -vertMovement;
			}

			final double horizMovement_real = horizMovement;
			final double vertMovement_real = vertMovement;

			translateCameraFromScreenPlan(horizMovement_real, vertMovement_real);

			lastMousePressedPosition.setLocation(newPoint);
		}

	}

	/**
	 * Method mouseDoubleClick()
	 *
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseDoubleClick(final org.eclipse.swt.events.MouseEvent e) {
		// Already taken in charge by the ZoomListener in the view
		if (keystoneMode) {
			final int x = autoScaleUp(e.x);
			final int y = autoScaleUp(e.y);
			final int corner = clickOnKeystone(x, y);
			if (corner != -1) { getRenderer().getKeystoneHelper().resetCorner(corner); }
		}
	}

	@Override
	public final void mouseClicked(final com.jogamp.newt.event.MouseEvent e) {
		if (e.getClickCount() == 2) {
			if (keystoneMode) {
				final int x = autoScaleUp(e.getX());
				final int y = autoScaleUp(e.getY());
				final int corner = clickOnKeystone(x, y);
				if (corner != -1) { getRenderer().getKeystoneHelper().resetCorner(corner); }
			} else {
				invokeOnGLThread(drawable -> {
					getRenderer().getSurface().zoomFit();
					return false;
				});
			}
		}
	}

	/**
	 * Method mouseDown()
	 *
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseDown(final org.eclipse.swt.events.MouseEvent e) {
		invokeOnGLThread(drawable -> {
			final int x = autoScaleUp(e.x);
			final int y = autoScaleUp(e.y);
			internalMouseDown(x, y, e.button, GamaKeyBindings.ctrl(e), GamaKeyBindings.shift(e));
			return false;
		});

	}

	@Override
	public final void mousePressed(final com.jogamp.newt.event.MouseEvent e) {
		invokeOnGLThread(drawable -> {
			final int x = autoScaleUp(e.getX());
			final int y = autoScaleUp(e.getY());
			internalMouseDown(x, y, e.getButton(), isControlDown(e), e.isShiftDown());
			return false;
		});
	}

	/**
	 * Gets the normalized coordinates.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the normalized coordinates
	 */
	protected GamaPoint getNormalizedCoordinates(final double x, final double y) {
		final double xCoordNormalized = x / getRenderer().getWidth();
		double yCoordNormalized = y / getRenderer().getHeight();
		if (!renderer.useShader()) { yCoordNormalized = 1 - yCoordNormalized; }
		return new GamaPoint(xCoordNormalized, yCoordNormalized);
	}

	/**
	 * Click on keystone.
	 *
	 * @param e
	 *            the e
	 * @return the int
	 */
	private int clickOnKeystone(final int x, final int y) {
		return renderer.getKeystoneHelper().cornerSelected(new GamaPoint(x, y));
	}

	/**
	 * Hover on keystone.
	 *
	 * @param e
	 *            the e
	 * @return the int
	 */
	protected int hoverOnKeystone(final int x, final int y) {
		// return the number of the corner clicked. Return -1 if no click on
		// keystone. Return 10 if click on the center.
		// final GamaPoint p = getNormalizedCoordinates(e);
		return renderer.getKeystoneHelper().cornerHovered(new GamaPoint(x, y));
	}

	/**
	 * Internal mouse down.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	final void internalMouseDown(final int x, final int y, final int button, final boolean isCtrl,
			final boolean isShift) {

		if (firsttimeMouseDown) {
			firstMousePressedPosition.setLocation(x, y, 0);
			firsttimeMouseDown = false;
		}
		if (keystoneMode) {
			if (getRenderer().getKeystoneHelper().getCornerSelected() != -1) {
				getRenderer().getKeystoneHelper().setCornerSelected(-1);
				return;
			}
			final int cornerSelected = clickOnKeystone(x, y);
			if (cornerSelected != -1) { getRenderer().getKeystoneHelper().setCornerSelected(cornerSelected); }
		}

		lastMousePressedPosition.setLocation(x, y, 0);
		// Activate Picking when press and right click
		if (button == 3 && !keystoneMode) {
			if (renderer.getOpenGLHelper().mouseInROI(lastMousePressedPosition)) {
				renderer.getSurface().selectionIn(renderer.getOpenGLHelper().getROIEnvelope());
			} else if (renderer.getSurface().canTriggerContextualMenu()) {
				renderer.getPickingHelper().setPicking(true);
			}
		} else if (button == 2 && !data.isLocked()) { // mouse wheel
			resetPivot();
		} else if (isShift && isViewInXYPlan()) { startROI(); }
		// else {
		// renderer.getPickingState().setPicking(false);
		// }
		getMousePosition().x = x;
		getMousePosition().y = y;

		setMouseLeftPressed(button == 1);
		setCtrlPressed(isCtrl);
		setShiftPressed(isShift);

	}

	/**
	 * Method mouseUp()
	 *
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseUp(final org.eclipse.swt.events.MouseEvent e) {

		invokeOnGLThread(drawable -> {
			// if (cameraInteraction) {
			internalMouseUp(e.button, GamaKeyBindings.shift(e));
			// }
			return false;
		});

	}

	@Override
	public final void mouseReleased(final com.jogamp.newt.event.MouseEvent e) {
		invokeOnGLThread(drawable -> {
			// if (cameraInteraction) {
			internalMouseUp(e.getButton(), e.isShiftDown());
			// }
			return false;
		});
	}

	/**
	 * Internal mouse up.
	 *
	 * @param e
	 *            the e
	 */
	protected void internalMouseUp(final int button, final boolean isShift) {
		firsttimeMouseDown = true;
		if (isViewInXYPlan() && isShift) { finishROISelection(); }
		if (button == 1) { setMouseLeftPressed(false); }

	}

	/**
	 * Start ROI.
	 *
	 * @param e
	 *            the e
	 */
	private void startROI() {
		renderer.getOpenGLHelper().defineROI(new GamaPoint(firstMousePressedPosition), new GamaPoint(mousePosition));
		ROICurrentlyDrawn = true;
	}

	/**
	 * Finish ROI selection.
	 */
	void finishROISelection() {
		if (ROICurrentlyDrawn) {
			final Envelope3D env = renderer.getOpenGLHelper().getROIEnvelope();
			if (env != null) { renderer.getSurface().selectionIn(env); }
		}
	}

	//
	// protected void dump() {
	// DEBUG.LOG("xPos:" + position.x + " yPos:" + position.y + "
	// zPos:" + position.z);
	// DEBUG.LOG("xLPos:" + target.x + " yLPos:" + target.y + " zLPos:"
	// + target.z);
	// DEBUG.LOG("_phi " + phi + " _theta " + theta);
	// }

	@Override
	public GamaPoint getMousePosition() { return mousePosition; }

	/**
	 * Checks if is view in XY plan.
	 *
	 * @return true, if is view in XY plan
	 */
	public boolean isViewInXYPlan() {
		return true;
		// return phi > 170 || phi < 10;// && theta > -5 && theta < 5;
	}

	@Override
	public GamaPoint getLastMousePressedPosition() { return lastMousePressedPosition; }

	/**
	 * Gets the keyboard sensivity.
	 *
	 * @return the keyboard sensivity
	 */
	protected double getKeyboardSensivity() { return _keyboardSensivity; }

	/**
	 * Gets the sensivity.
	 *
	 * @return the sensivity
	 */
	protected double getSensivity() { return _sensivity; }

	/**
	 * Checks if is forward.
	 *
	 * @return true, if is forward
	 */
	protected boolean isForward() { return goesForward; }

	/**
	 * Checks if is backward.
	 *
	 * @return true, if is backward
	 */
	protected boolean isBackward() { return goesBackward; }

	/**
	 * Checks if is strafe left.
	 *
	 * @return true, if is strafe left
	 */
	protected boolean isStrafeLeft() { return strafeLeft; }

	/**
	 * Checks if is strafe right.
	 *
	 * @return true, if is strafe right
	 */
	protected boolean isStrafeRight() { return strafeRight; }

	/**
	 * Gets the renderer.
	 *
	 * @return the renderer
	 */
	public IOpenGLRenderer getRenderer() { return renderer; }

	/**
	 * Method keyPressed()
	 *
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public final void keyPressed(final org.eclipse.swt.events.KeyEvent e) {

		invokeOnGLThread(drawable -> {
			if (!keystoneMode) {
				boolean cameraInteraction = !data.isLocked();
				switch (e.keyCode) {
					case SWT.ARROW_LEFT:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction) { AbstractCamera.this.strafeLeft = true; }
						break;
					case SWT.ARROW_RIGHT:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction) { AbstractCamera.this.strafeRight = true; }
						break;
					case SWT.ARROW_UP:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction) { AbstractCamera.this.goesForward = true; }
						break;
					case SWT.ARROW_DOWN:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction) { AbstractCamera.this.goesBackward = true; }
						break;
					case SWT.SPACE:
						if (cameraInteraction) { resetPivot(); }
						break;
					case SWT.CTRL:
						setCtrlPressed(!firsttimeMouseDown);
						break;
					case SWT.COMMAND:
						setCtrlPressed(!firsttimeMouseDown);
						break;
				}
				switch (e.character) {
					case '+':
						if (cameraInteraction) { zoom(true); }
						break;
					case '-':
						if (cameraInteraction) { zoom(false); }
						break;
					case '4':
						if (cameraInteraction && useNumKeys) { quickLeftTurn(); }
						break;
					case '6':
						if (cameraInteraction && useNumKeys) { quickRightTurn(); }
						break;
					case '8':
						if (cameraInteraction && useNumKeys) { quickUpTurn(); }
						break;
					case '2':
						if (cameraInteraction && useNumKeys) { quickDownTurn(); }
						break;
					case 'k':
						if (!GamaKeyBindings.ctrl(e)) { activateKeystoneMode(); }
						break;
					default:
						return true;
				}
			} else if (e.character == 'k' && !GamaKeyBindings.ctrl(e)) { activateKeystoneMode(); }
			return true;
		});
	}

	@Override
	public final void keyPressed(final com.jogamp.newt.event.KeyEvent e) {

		invokeOnGLThread(drawable -> {
			if (!keystoneMode) {
				boolean cameraInteraction = !data.isLocked();
				switch (e.getKeySymbol()) {
					// We need to register here all the keystrokes used in the Workbench and on the view, as they might
					// be caught by the NEWT key listener. Those dedicated to modelling are left over for the moment
					// (like CTRL+SHIFT+H)
					// First the global keystrokes
					case com.jogamp.newt.event.KeyEvent.VK_ESCAPE:
						WorkbenchHelper.toggleFullScreenMode();
						break;
					case 'p':
					case 'P':
						if (isControlDown(e)) {
							if (e.isShiftDown()) {
								GAMA.stepFrontmostExperiment();
							} else {
								GAMA.startPauseFrontmostExperiment();
							}
						}
						break;
					case 'R':
					case 'r':
						if (isControlDown(e)) {
							if (e.isShiftDown()) {
								GAMA.relaunchFrontmostExperiment();
							} else {
								GAMA.reloadFrontmostExperiment();
							}
						}
						break;
					case 'X':
					case 'x':
						if (isControlDown(e) && e.isShiftDown()) { GAMA.closeAllExperiments(true, false); }
						break;

					case com.jogamp.newt.event.KeyEvent.VK_SPACE:
						if (cameraInteraction) { resetPivot(); }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_CONTROL:
						// The press and release of these keys does not seem to work. Caught after
						setCtrlPressed(!firsttimeMouseDown);
						break;
					case com.jogamp.newt.event.KeyEvent.VK_META:
						// The press and release of these keys does not seem to work. Caught after
						setCtrlPressed(!firsttimeMouseDown);
						break;
				}
				switch (e.getKeyCode()) {
					// Finally the keystrokes for the display itself
					case com.jogamp.newt.event.KeyEvent.VK_LEFT:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction) { AbstractCamera.this.strafeLeft = true; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_RIGHT:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction) { AbstractCamera.this.strafeRight = true; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_UP:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction) { AbstractCamera.this.goesForward = true; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_DOWN:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction) { AbstractCamera.this.goesBackward = true; }
						break;
				}
				switch (e.getKeyChar()) {
					case 0:
						setCtrlPressed(e.isControlDown() || PlatformHelper.isMac() && e.isMetaDown());
						setShiftPressed(e.isShiftDown());
						break;
					case '+':
						if (cameraInteraction) { zoom(true); }
						break;
					case '-':
						if (cameraInteraction) { zoom(false); }
						break;
					case '4':
						if (cameraInteraction && useNumKeys) { quickLeftTurn(); }
						break;
					case '6':
						if (cameraInteraction && useNumKeys) { quickRightTurn(); }
						break;
					case '8':
						if (cameraInteraction && useNumKeys) { quickUpTurn(); }
						break;
					case '2':
						if (cameraInteraction && useNumKeys) { quickDownTurn(); }
						break;
					case 'k':
						if (!isControlDown(e)) { activateKeystoneMode(); }
						break;
					default:
						return true;
				}
			} else if (e.getKeyChar() == 'k' && !isControlDown(e)) { activateKeystoneMode(); }
			return true;
		});
	}

	/**
	 * Reset pivot.
	 */
	protected void resetPivot() {
		// final LayeredDisplayData data = data;
		// final double envWidth = data.getEnvWidth();
		// final double envHeight = data.getEnvHeight();
		// GamaPoint position = getDefinition().getLocation();
		// GamaPoint target = getDefinition().getTarget();
		// final double translate_x = target.x - envWidth / 2d;
		// final double translate_y = target.y + envHeight / 2d;
		// final double translate_z = target.z;
		// setTarget(envWidth / 2d, -envHeight / 2d, 0);
		// setPosition(position.x - translate_x, position.y - translate_y, position.z - translate_z);
		data.resetCamera();
		updateSphericalCoordinatesFromLocations();
	}

	/**
	 * Quick left turn.
	 */
	protected void quickLeftTurn() {
		theta -= 30;
		updateCartesianCoordinatesFromAngles();
	}

	/**
	 * Quick right turn.
	 */
	protected void quickRightTurn() {
		theta += 30;
		updateCartesianCoordinatesFromAngles();
	}

	/**
	 * Quick up turn.
	 */
	protected void quickUpTurn() {
		if (flipped) {
			if (phi + 30 < 180) {
				phi += 30;
			} else {
				phi = 360 - phi - 30;
				flipped = false;
				theta += 180;
			}
		} else if (phi - 30 > 0) {
			phi -= 30;
		} else {
			phi = -phi + 30;
			flipped = true;
			theta += 180;
		}
		updateCartesianCoordinatesFromAngles();
	}

	/**
	 * Quick down turn.
	 */
	protected void quickDownTurn() {
		if (flipped) {
			if (phi - 30 > 0) {
				phi -= 30;
			} else {
				phi = -phi + 30;
				flipped = false;
				theta += 180;
			}
		} else if (phi + 30 < 180) {
			phi += 30;
		} else {
			phi = 360 - phi - 30;
			flipped = true;
			theta += 180;
		}
		updateCartesianCoordinatesFromAngles();
	}

	/**
	 * Activate keystone mode.
	 */
	protected final void activateKeystoneMode() {
		if (!keystoneMode) {
			getRenderer().getSurface().zoomFit();
			getRenderer().getKeystoneHelper().startDrawHelper();
		} else {
			getRenderer().getKeystoneHelper().stopDrawHelper();
		}
		keystoneMode = !keystoneMode;
	}

	/**
	 * Method keyReleased()
	 *
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public final void keyReleased(final org.eclipse.swt.events.KeyEvent e) {

		invokeOnGLThread(drawable -> {
			if (!keystoneMode) {
				boolean cameraInteraction = !data.isLocked();
				switch (e.keyCode) {
					case SWT.ARROW_LEFT: // turns left (scene rotates right)
						if (cameraInteraction) { strafeLeft = false; }
						break;
					case SWT.ARROW_RIGHT: // turns right (scene rotates left)
						if (cameraInteraction) { strafeRight = false; }
						break;
					case SWT.ARROW_UP:
						if (cameraInteraction) { goesForward = false; }
						break;
					case SWT.ARROW_DOWN:
						if (cameraInteraction) { goesBackward = false; }
						break;
					case SWT.CTRL:
						setCtrlPressed(false);
						break;
					case SWT.COMMAND:
						setCtrlPressed(false);
						break;
					case SWT.SHIFT:
						setShiftPressed(false);
						finishROISelection();
						break;
					default:
						return true;
				}
			}
			return false;
		});
	}

	@Override
	public final void keyReleased(final com.jogamp.newt.event.KeyEvent e) {

		invokeOnGLThread(drawable -> {
			if (!keystoneMode) {
				if (e.getKeyChar() == 0) {
					setCtrlPressed(!isControlDown(e));
					setShiftPressed(!e.isShiftDown());
					return true;
				}
				boolean cameraInteraction = !data.isLocked();
				switch (e.getKeyCode()) {

					case com.jogamp.newt.event.KeyEvent.VK_LEFT: // turns left (scene rotates right)
						if (cameraInteraction) { strafeLeft = false; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_RIGHT: // turns right (scene rotates left)
						if (cameraInteraction) { strafeRight = false; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_UP:
						if (cameraInteraction) { goesForward = false; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_DOWN:
						if (cameraInteraction) { goesBackward = false; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_CONTROL:
						setCtrlPressed(false);
						break;
					case com.jogamp.newt.event.KeyEvent.VK_META:
						setCtrlPressed(false);
						break;
					case com.jogamp.newt.event.KeyEvent.VK_SHIFT:
						setShiftPressed(false);
						finishROISelection();
						break;
					default:
						return true;
				}
			}
			return false;
		});
	}

	@Override
	public Double zoomLevel() {
		return getRenderer().getMaxEnvDim() * data.getDistanceCoefficient() / data.getDistance();
	}

	@Override
	public void zoom(final double level) {
		data.setDistance(getRenderer().getMaxEnvDim() * data.getDistanceCoefficient() / level);
		updateCartesianCoordinatesFromAngles();
		/**
		 * Zoom.
		 *
		 * @param in
		 *            the in
		 */
	}

	/**
	 * Zoom.
	 *
	 * @param in
	 *            the in
	 */
	// @Override
	public void zoom(final boolean in) {
		if (keystoneMode) return;
		Double distance = data.getDistance();
		final double step = distance != 0d ? distance / 10d * GamaPreferences.Displays.OPENGL_ZOOM.getValue() : 0.1d;
		data.setDistance(distance + (in ? -step : step));
		// zoom(zoomLevel());
		data.setZoomLevel(zoomLevel(), true, false);
	}

	@Override
	public void zoomFocus(final Envelope3D env) {

		// REDO it entirely
		final double extent = env.maxExtent();
		if (extent == 0) {
			data.setDistance(env.getMaxZ() + getRenderer().getMaxEnvDim() / 10);
		} else {
			data.setDistance(extent * 1.5);
		}
		final GamaPoint centre = env.centre();
		// we suppose y is already negated
		data.setCameraTarget(new GamaPoint(centre.x, centre.y, centre.z));
		data.setZoomLevel(zoomLevel(), true, false);
		/**
		 * Draw rotation helper.
		 */
	}

	/**
	 * Draw rotation helper.
	 */
	protected void drawRotationHelper() {
		renderer.getOpenGLHelper().setRotationMode(ctrlPressed && !data.isLocked());
		/**
		 * Sets the distance.
		 *
		 * @param distance
		 *            the new distance
		 */
	}

	/**
	 * Sets the distance.
	 *
	 * @param distance
	 *            the new distance
	 */
	// @Override
	// public void setDistance(final double distance) {
	// data.setDistance(distance);
	// }

}

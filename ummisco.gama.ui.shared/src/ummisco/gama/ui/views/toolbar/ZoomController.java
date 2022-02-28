/*******************************************************************************************************
 *
 * ZoomController.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.toolbar;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.GestureListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolItem;

import ummisco.gama.ui.menus.GamaMenu;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * Class ZoomController.
 *
 * @author drogoul
 * @since 9 févr. 2015
 *
 */
public class ZoomController {

	/** The including scrolling. */
	// Fix for Issue #1291
	final boolean includingScrolling;

	/** The view. */
	final IToolbarDecoratedView.Zoomable view;

	/** The camera locked. */
	ToolItem cameraLocked;

	/**
	 * @param view
	 */
	public ZoomController(final IToolbarDecoratedView.Zoomable view) {
		this.view = view;
		this.includingScrolling = view.zoomWhenScrolling();
	}

	/**
	 * @param tb
	 */
	public void install(final GamaToolbar2 tb) {
		final GestureListener gl = ge -> {
			if (ge.detail == SWT.GESTURE_MAGNIFY) {
				if (ge.magnification > 1.0) {
					view.zoomIn();
				} else if (ge.magnification < 1.0) { view.zoomOut(); }
			}

		};

		final MouseListener ml = new MouseAdapter() {

			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				if (e.button == 1) { view.zoomFit(); }
			}
		};

		final MouseWheelListener mw = e -> {
			if (e.count < 0) {
				view.zoomOut();
			} else {
				view.zoomIn();
			}
		};

		tb.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				final Control[] controls = view.getZoomableControls();
				for (final Control c : controls) {
					if (c != null) {
						c.addGestureListener(gl);
						c.addMouseListener(ml);
						if (includingScrolling) { c.addMouseWheelListener(mw); }
						// once installed the listener removes itself from the
						// toolbar
					}
				}
				tb.removeControlListener(this);
			}

		});
		// ⊘ CIRCLED DIVISION SLASH Unicode: U+2298, UTF-8: E2 8A 98
		//   NO-BREAK SPACE Unicode: U+00A0, UTF-8: C2 A0
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_ZOOMIN, "Zoom in", "Zoom in", e -> view.zoomIn(), SWT.RIGHT);
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_ZOOMFIT, "Zoom fit", "Zoom to fit view", e -> view.zoomFit(), SWT.RIGHT);
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_ZOOMOUT, "Zoom out", "Zoom out", e -> view.zoomOut(), SWT.RIGHT);
		tb.sep(SWT.RIGHT);
		if (view.hasCameras()) {
			tb.menu(IGamaIcons.DISPLAY_TOOLBAR_CAMERA, "", "Choose a camera...", trigger -> {
				final GamaMenu menu = new GamaMenu() {

					@Override
					protected void fillMenu() {
						final Collection<String> cameras = view.getCameraNames();

						for (final String p : cameras) {
							action((p.equals(view.getCameraName()) ? "\u2713 " : " \u00A0 ") + p,
									new SelectionAdapter() {

										@Override
										public void widgetSelected(final SelectionEvent e) {
											view.setCameraName(p);
											cameraLocked.setSelection(view.isCameraLocked());
										}

									}, null);
						}
					}
				};
				menu.open(tb.getToolbar(SWT.RIGHT), trigger, tb.height, 96);
			}, SWT.RIGHT);
			cameraLocked = tb.check("population.lock2", "Lock/unlock", "Lock/unlock camera",
					e -> { view.toggleCamera(); }, SWT.RIGHT);
			cameraLocked.setSelection(view.isCameraLocked());
		}

	}

}

/*******************************************************************************************************
 *
 * SimulationSpeedContributionItem.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Maths;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.interfaces.ISpeedDisplayer;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * The class SimulationSpeedContributionItem.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 * @modification now obeys a cubic power law from 0 to BASE_UNIT milliseconds
 *
 */
public class SimulationSpeedContributionItem extends WorkbenchWindowControlContribution implements ISpeedDisplayer {

	static {
		DEBUG.OFF();
	}

	/** The instance. */
	private static SimulationSpeedContributionItem instance;

	/** The max. */
	static double max = 1000;

	/** slop factor for the logarithmic slider. */
	static double lambda = 0.3;

	/** The Constant sliderColor. */
	protected final static GamaUIColor sliderColor = IGamaColors.GRAY_LABEL;

	/** The Constant widthSize. */
	public final static int widthSize = 100;

	/** The Constant marginWidth. */
	public final static int marginWidth = 16;

	/** The Constant heightSize. */
	public final static int heightSize = 16;

	/** The sliders. */
	protected static List<SimpleSlider> sliders = new ArrayList<>();

	/**
	 *
	 * @param v
	 *            in millisecondsmax
	 * @return
	 */
	public static double positionFromValue(final double v) {
		// returns a percentage between 0 and 1 (0 -> max milliseconds; 1 -> 0
		// milliseconds).
		if (GamaPreferences.Runtime.CORE_SLIDER_TYPE.getValue()) return 1 - v / max;
		return 1 - lambda * Math.log(v / max * (Math.exp(1 / lambda) - 1) + 1);
	}

	@Override
	protected int computeWidth(final Control control) {
		return control.computeSize(widthSize, SWT.DEFAULT, true).x;
	}

	/**
	 * p between 0 and 1. Returns a value in milliseconds
	 *
	 * @param p
	 * @return
	 */
	public static double valueFromPosition(final double p) {
		if (GamaPreferences.Runtime.CORE_SLIDER_TYPE.getValue()) return max - p * max;
		return (Math.exp((1 - p) / lambda) - 1) / (Math.exp(1 / lambda) - 1) * max;
	}

	/**
	 * Instantiates a new simulation speed contribution item.
	 */
	public SimulationSpeedContributionItem() {
		instance = this;
	}

	/**
	 * Total width.
	 *
	 * @return the int
	 */
	public static int totalWidth() {
		return widthSize + 2 * marginWidth;
	}

	@Override
	public Control createControl(final Composite parent) {
		return create(parent);
	}

	/**
	 * Creates the.
	 *
	 * @param parent
	 *            the parent
	 * @return the control
	 */
	public static Control create(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.DOUBLE_BUFFERED | SWT.INHERIT_DEFAULT);
		GamaColors.setBackground(parent.getBackground(), composite);
		final GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = marginWidth;
		composite.setLayout(layout);
		// composite.setBackground(parent.getBackground());
		// final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.widthHint = widthSize;
		data.minimumWidth = widthSize;
		final SimpleSlider slider =
				new SimpleSlider(composite, sliderColor.color(), sliderColor.color(), IGamaColors.BLUE.color(), true);
		slider.setLayoutData(data);
		slider.setSize(widthSize, heightSize);
		slider.addPositionChangeListener(POSITION_LISTENER);
		slider.updateSlider(getInitialValue(), false);
		slider.setBackground(parent.getBackground());
		slider.addDisposeListener(e -> { sliders.remove(slider); });
		sliders.add(slider);
		return composite;

	}

	/**
	 * Gets the initial value.
	 *
	 * @return the initial value
	 */
	protected static double getInitialValue() {
		final ExperimentAgent a = GAMA.getExperiment() == null ? null : GAMA.getExperiment().getAgent();
		double value = 0d;
		double maximum = 1000d;
		if (a != null) {
			value = a.getMinimumDuration() * 1000;
			maximum = a.getMaximumDuration() * 1000;
		}
		max = maximum;
		// if (maximum > max) { max = maximum; }
		return positionFromValue(value);
	}

	/*
	 * Parameter in milliseconds
	 */
	@Override
	public void setInit(final double i, final boolean notify) {
		if (i > max) { max = i; }
		for (final SimpleSlider slider : sliders) {
			if (slider == null || slider.isDisposed()) { continue; }
			slider.updateSlider(i, notify);
		}
	}

	@Override
	public void setMaximum(Double i) {
		if (i <= 0) { i = 1d; }
		max = i;
		for (final SimpleSlider slider : sliders) {
			if (slider == null || slider.isDisposed()) { continue; }
			slider.updateSlider(i, false);
		}
	}

	/** The position listener. */
	static IPositionChangeListener POSITION_LISTENER = (s, position) -> {

		GAMA.getExperiment().getAgent().setMinimumDurationExternal(valueFromPosition(position) / 1000);
		for (final SimpleSlider slider2 : sliders) {
			slider2.setToolTipText(
					"Minimum duration of a cycle " + Maths.opTruncate(valueFromPosition(position) / 1000, 3) + " s");
			if (slider2 == s) { continue; }
			slider2.updateSlider(position, false);
		}

	};

	/**
	 * Gets the single instance of SimulationSpeedContributionItem.
	 *
	 * @return single instance of SimulationSpeedContributionItem
	 */
	public static SimulationSpeedContributionItem getInstance() { return instance; }

}

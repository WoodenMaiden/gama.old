/*******************************************************************************************************
 *
 * PreferencesHandler.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ummisco.gama.ui.views.GamaPreferencesView;

/**
 * The Class PreferencesHandler.
 */
public class PreferencesHandler extends AbstractHandler {

	// GamaPreferencesView view;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GamaPreferencesView.show();
		return null;
	}

}

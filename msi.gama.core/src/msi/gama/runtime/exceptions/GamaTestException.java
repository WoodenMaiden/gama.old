/*******************************************************************************************************
 *
 * msi.gama.runtime.exceptions.GamaTestException.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.exceptions;

import msi.gama.runtime.IScope;

public class GamaTestException extends GamaRuntimeException {

	public GamaTestException(final IScope scope, final String s, final boolean warning) {
		super(scope, s, warning);
	}

}

/*******************************************************************************************************
 *
 * CreateFromGeometriesDelegate.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.create;

import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.IAddressableContainer;
import msi.gama.util.IList;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.CreateStatement;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class CreateFromDatabaseDelegate.
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class CreateFromGeometriesDelegate implements ICreateDelegate {

	/**
	 * Method acceptSource()
	 *
	 * @see msi.gama.common.interfaces.ICreateDelegate#acceptSource(IScope, java.lang.Object)
	 */
	@Override
	public boolean acceptSource(final IScope scope, final Object source) {
		// THIS CONDITION MUST BE CHECKED : bypass a condition that belong to
		// the case createFromDatabase
		return source instanceof GamaGeometryFile
				|| source instanceof IList il && Types.GEOMETRY.isAssignableFrom(il.getGamlType().getContentType());
	}

	/**
	 * Method createFrom() Method used to read initial values and attributes from a CSV values describing a synthetic
	 * population
	 *
	 * @author Alexis Drogoul
	 * @since 04-09-2012
	 * @see msi.gama.common.interfaces.ICreateDelegate#createFrom(msi.gama.runtime.IScope, java.util.List, int,
	 *      java.lang.Object)
	 */
	@Override
	public boolean createFrom(final IScope scope, final List<Map<String, Object>> inits, final Integer max,
			final Object input, final Arguments init, final CreateStatement statement) {
		final IAddressableContainer<Integer, IShape, Integer, IShape> container =
				(IAddressableContainer<Integer, IShape, Integer, IShape>) input;
		final int num = max == null ? container.length(scope) : Math.min(container.length(scope), max);
		for (int i = 0; i < num; i++) {
			IShape g = container.get(scope, i);
			if (g instanceof GamaPoint) { g = GamaGeometryType.createPoint(g); }

			final Map map = g.getAttributes(true);
			// The shape is added to the initial values
			g.setAttribute(IKeyword.SHAPE, g);
			// GIS attributes are pushed to the scope in order to be read by read/get statements
			statement.fillWithUserInit(scope, map);
			inits.add(map);
		}
		return true;
	}

	/**
	 * Method fromFacetType()
	 *
	 * @see msi.gama.common.interfaces.ICreateDelegate#fromFacetType()
	 */
	@Override
	public IType fromFacetType() {
		return Types.CONTAINER.of(Types.GEOMETRY);
	}

}

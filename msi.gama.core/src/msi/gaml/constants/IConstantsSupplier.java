/*******************************************************************************************************
 *
 * IConstantsSupplier.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.constants;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.IConstantCategory;
import msi.gaml.operators.Cast;

/**
 * The Interface IConstantsSupplier.
 */
public interface IConstantsSupplier {

	/**
	 * Supply constants to.
	 *
	 * @param acceptor
	 *            the acceptor
	 */
	void supplyConstantsTo(IConstantAcceptor acceptor);

	/**
	 * Browse.
	 *
	 * @param cc
	 *            the cc
	 * @param acceptor
	 *            the acceptor
	 */
	default void browse(final Class cc, final IConstantAcceptor acceptor) {
		String[] names = null;
		boolean isTime = false;
		String deprecated = null;
		Object value = null;
		for (final Field f : cc.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers())) {
				try {
					if (Modifier.isStatic(f.getModifiers())) { value = f.get(cc); }
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException e1) {
					e1.printStackTrace();
					continue;
				}
				final constant annotation = f.getAnnotation(constant.class);
				if (annotation != null) {
					names = annotation.altNames();
					StringBuilder documentation =
							new StringBuilder("Its value is <b>").append(Cast.toGaml(value)).append(". </b><p/>");
					final doc[] ds = annotation.doc();
					if (ds != null && ds.length > 0) {
						final doc d = ds[0];
						documentation.append(d.value());
						deprecated = d.deprecated();
						if (deprecated.isEmpty()) { deprecated = null; }
						isTime = Arrays.asList(annotation.category()).contains(IConstantCategory.TIME);
					}
					acceptor.accept(f.getName(), value, documentation.toString(), deprecated, isTime, names);
				}
			}
		}
	}

}

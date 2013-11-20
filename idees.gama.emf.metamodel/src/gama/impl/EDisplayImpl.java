/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EDisplay;
import gama.EDisplayLink;
import gama.ELayer;
import gama.GamaPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EDisplay</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EDisplayImpl#getLayers <em>Layers</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getDisplayLink <em>Display Link</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getOpengl <em>Opengl</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getRefresh <em>Refresh</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getBackground <em>Background</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getLayerList <em>Layer List</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getColor <em>Color</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getIsColorCst <em>Is Color Cst</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getColorRBG <em>Color RBG</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getGamlCode <em>Gaml Code</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EDisplayImpl extends EGamaObjectImpl implements EDisplay {
	/**
	 * The cached value of the '{@link #getLayers() <em>Layers</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLayers()
	 * @generated
	 * @ordered
	 */
	protected EList<ELayer> layers;

	/**
	 * The cached value of the '{@link #getDisplayLink() <em>Display Link</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDisplayLink()
	 * @generated
	 * @ordered
	 */
	protected EDisplayLink displayLink;

	/**
	 * The default value of the '{@link #getOpengl() <em>Opengl</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOpengl()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean OPENGL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOpengl() <em>Opengl</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOpengl()
	 * @generated
	 * @ordered
	 */
	protected Boolean opengl = OPENGL_EDEFAULT;

	/**
	 * The default value of the '{@link #getRefresh() <em>Refresh</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefresh()
	 * @generated
	 * @ordered
	 */
	protected static final String REFRESH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRefresh() <em>Refresh</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefresh()
	 * @generated
	 * @ordered
	 */
	protected String refresh = REFRESH_EDEFAULT;

	/**
	 * The default value of the '{@link #getBackground() <em>Background</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBackground()
	 * @generated
	 * @ordered
	 */
	protected static final String BACKGROUND_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBackground() <em>Background</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBackground()
	 * @generated
	 * @ordered
	 */
	protected String background = BACKGROUND_EDEFAULT;

	/**
	 * The cached value of the '{@link #getLayerList() <em>Layer List</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLayerList()
	 * @generated
	 * @ordered
	 */
	protected EList<String> layerList;

	/**
	 * The default value of the '{@link #getColor() <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected static final String COLOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getColor() <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected String color = COLOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getIsColorCst() <em>Is Color Cst</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsColorCst()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean IS_COLOR_CST_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getIsColorCst() <em>Is Color Cst</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsColorCst()
	 * @generated
	 * @ordered
	 */
	protected Boolean isColorCst = IS_COLOR_CST_EDEFAULT;

	/**
	 * The cached value of the '{@link #getColorRBG() <em>Color RBG</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColorRBG()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> colorRBG;

	/**
	 * The default value of the '{@link #getGamlCode() <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGamlCode()
	 * @generated
	 * @ordered
	 */
	protected static final String GAML_CODE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getGamlCode() <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGamlCode()
	 * @generated
	 * @ordered
	 */
	protected String gamlCode = GAML_CODE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EDisplayImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EDISPLAY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ELayer> getLayers() {
		if (layers == null) {
			layers = new EObjectResolvingEList<ELayer>(ELayer.class, this, GamaPackage.EDISPLAY__LAYERS);
		}
		return layers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplayLink getDisplayLink() {
		if (displayLink != null && displayLink.eIsProxy()) {
			InternalEObject oldDisplayLink = (InternalEObject)displayLink;
			displayLink = (EDisplayLink)eResolveProxy(oldDisplayLink);
			if (displayLink != oldDisplayLink) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.EDISPLAY__DISPLAY_LINK, oldDisplayLink, displayLink));
			}
		}
		return displayLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplayLink basicGetDisplayLink() {
		return displayLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDisplayLink(EDisplayLink newDisplayLink) {
		EDisplayLink oldDisplayLink = displayLink;
		displayLink = newDisplayLink;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__DISPLAY_LINK, oldDisplayLink, displayLink));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getOpengl() {
		return opengl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOpengl(Boolean newOpengl) {
		Boolean oldOpengl = opengl;
		opengl = newOpengl;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__OPENGL, oldOpengl, opengl));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRefresh() {
		return refresh;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRefresh(String newRefresh) {
		String oldRefresh = refresh;
		refresh = newRefresh;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__REFRESH, oldRefresh, refresh));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBackground() {
		return background;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBackground(String newBackground) {
		String oldBackground = background;
		background = newBackground;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__BACKGROUND, oldBackground, background));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getLayerList() {
		if (layerList == null) {
			layerList = new EDataTypeUniqueEList<String>(String.class, this, GamaPackage.EDISPLAY__LAYER_LIST);
		}
		return layerList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getColor() {
		return color;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setColor(String newColor) {
		String oldColor = color;
		color = newColor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__COLOR, oldColor, color));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getIsColorCst() {
		return isColorCst;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsColorCst(Boolean newIsColorCst) {
		Boolean oldIsColorCst = isColorCst;
		isColorCst = newIsColorCst;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__IS_COLOR_CST, oldIsColorCst, isColorCst));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Integer> getColorRBG() {
		if (colorRBG == null) {
			colorRBG = new EDataTypeEList<Integer>(Integer.class, this, GamaPackage.EDISPLAY__COLOR_RBG);
		}
		return colorRBG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getGamlCode() {
		return gamlCode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGamlCode(String newGamlCode) {
		String oldGamlCode = gamlCode;
		gamlCode = newGamlCode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__GAML_CODE, oldGamlCode, gamlCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EDISPLAY__LAYERS:
				return getLayers();
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				if (resolve) return getDisplayLink();
				return basicGetDisplayLink();
			case GamaPackage.EDISPLAY__OPENGL:
				return getOpengl();
			case GamaPackage.EDISPLAY__REFRESH:
				return getRefresh();
			case GamaPackage.EDISPLAY__BACKGROUND:
				return getBackground();
			case GamaPackage.EDISPLAY__LAYER_LIST:
				return getLayerList();
			case GamaPackage.EDISPLAY__COLOR:
				return getColor();
			case GamaPackage.EDISPLAY__IS_COLOR_CST:
				return getIsColorCst();
			case GamaPackage.EDISPLAY__COLOR_RBG:
				return getColorRBG();
			case GamaPackage.EDISPLAY__GAML_CODE:
				return getGamlCode();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case GamaPackage.EDISPLAY__LAYERS:
				getLayers().clear();
				getLayers().addAll((Collection<? extends ELayer>)newValue);
				return;
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				setDisplayLink((EDisplayLink)newValue);
				return;
			case GamaPackage.EDISPLAY__OPENGL:
				setOpengl((Boolean)newValue);
				return;
			case GamaPackage.EDISPLAY__REFRESH:
				setRefresh((String)newValue);
				return;
			case GamaPackage.EDISPLAY__BACKGROUND:
				setBackground((String)newValue);
				return;
			case GamaPackage.EDISPLAY__LAYER_LIST:
				getLayerList().clear();
				getLayerList().addAll((Collection<? extends String>)newValue);
				return;
			case GamaPackage.EDISPLAY__COLOR:
				setColor((String)newValue);
				return;
			case GamaPackage.EDISPLAY__IS_COLOR_CST:
				setIsColorCst((Boolean)newValue);
				return;
			case GamaPackage.EDISPLAY__COLOR_RBG:
				getColorRBG().clear();
				getColorRBG().addAll((Collection<? extends Integer>)newValue);
				return;
			case GamaPackage.EDISPLAY__GAML_CODE:
				setGamlCode((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case GamaPackage.EDISPLAY__LAYERS:
				getLayers().clear();
				return;
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				setDisplayLink((EDisplayLink)null);
				return;
			case GamaPackage.EDISPLAY__OPENGL:
				setOpengl(OPENGL_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__REFRESH:
				setRefresh(REFRESH_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__BACKGROUND:
				setBackground(BACKGROUND_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__LAYER_LIST:
				getLayerList().clear();
				return;
			case GamaPackage.EDISPLAY__COLOR:
				setColor(COLOR_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__IS_COLOR_CST:
				setIsColorCst(IS_COLOR_CST_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__COLOR_RBG:
				getColorRBG().clear();
				return;
			case GamaPackage.EDISPLAY__GAML_CODE:
				setGamlCode(GAML_CODE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case GamaPackage.EDISPLAY__LAYERS:
				return layers != null && !layers.isEmpty();
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				return displayLink != null;
			case GamaPackage.EDISPLAY__OPENGL:
				return OPENGL_EDEFAULT == null ? opengl != null : !OPENGL_EDEFAULT.equals(opengl);
			case GamaPackage.EDISPLAY__REFRESH:
				return REFRESH_EDEFAULT == null ? refresh != null : !REFRESH_EDEFAULT.equals(refresh);
			case GamaPackage.EDISPLAY__BACKGROUND:
				return BACKGROUND_EDEFAULT == null ? background != null : !BACKGROUND_EDEFAULT.equals(background);
			case GamaPackage.EDISPLAY__LAYER_LIST:
				return layerList != null && !layerList.isEmpty();
			case GamaPackage.EDISPLAY__COLOR:
				return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(color);
			case GamaPackage.EDISPLAY__IS_COLOR_CST:
				return IS_COLOR_CST_EDEFAULT == null ? isColorCst != null : !IS_COLOR_CST_EDEFAULT.equals(isColorCst);
			case GamaPackage.EDISPLAY__COLOR_RBG:
				return colorRBG != null && !colorRBG.isEmpty();
			case GamaPackage.EDISPLAY__GAML_CODE:
				return GAML_CODE_EDEFAULT == null ? gamlCode != null : !GAML_CODE_EDEFAULT.equals(gamlCode);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (opengl: ");
		result.append(opengl);
		result.append(", refresh: ");
		result.append(refresh);
		result.append(", background: ");
		result.append(background);
		result.append(", layerList: ");
		result.append(layerList);
		result.append(", color: ");
		result.append(color);
		result.append(", isColorCst: ");
		result.append(isColorCst);
		result.append(", colorRBG: ");
		result.append(colorRBG);
		result.append(", gamlCode: ");
		result.append(gamlCode);
		result.append(')');
		return result.toString();
	}

} //EDisplayImpl

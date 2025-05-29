package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.products;

import java.util.Arrays;
import java.util.List;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;

/**
 * Enum representing different product families with their respective details.
 *
 * @author Kevin / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum ProductFamily {
	UNDEFINED("UNDEFINED", Constant.NONE, ProductType.NONE, ProductSubType.NONE),
	MATRIX_G3("MATRIX_G3", "Matrix G3", ProductType.LCD, ProductSubType.NONE),
	DLX("DLX", "DLX", ProductType.LED, ProductSubType.TWO_K_PIXEL),
	TWA("TWA", "TWA", ProductType.LED, ProductSubType.OPEN_PIXEL),
	TWS("TWS", "TWS", ProductType.LED, ProductSubType.OPEN_PIXEL),
	TVF("TVF", "TVF", ProductType.LED, ProductSubType.OPEN_PIXEL),
	DIRECT_LIGHT_ULTRA("DLU", "DirectLight Ultra", ProductType.LED, ProductSubType.ENHANCED_OPEN_PIXEL),
	DIRECT_LIGHT_PRO("DLPro", "DirectLight Pro", ProductType.LED, ProductSubType.ENHANCED_OPEN_PIXEL),
	DIRECT_LIGHT_SLIM("DLSlim", "DirectLight Slim", ProductType.LED, ProductSubType.ENHANCED_OPEN_PIXEL);

	private static final List<String> MATRIX_G3_MODELS = Arrays.asList(
			"LX46S", "LX46U-3D", "LX46U", "LX46X", "MX46U", "MX46X", "LX55U", "LX55X", "MX55U", "MX55X",
			"LX55M", "MX55M", "MX65U-4K", "LX55X2", "MX55X2", "LX55M2", "MX55M2", "LX55X3", "MX55X3"
	);
	private static final List<String> DLX_MODELS = Arrays.asList("DLX-0.7", "DLX-0.9", "DLX-1.2", "DLX-1.5", "DLX-1.8");
	private static final List<String> TWA_MODELS = Arrays.asList("TWA-0.9", "TWA-1.2", "TWA-1.8");
	private static final List<String> TWS_MODELS = Arrays.asList("TWS-0.9", "TWS-1.2", "TWS-1.5", "TWS-1.8");
	private static final List<String> TVF_MODELS = Arrays.asList("TVF-0.9", "TVF-1.2", "TVF-1.5", "TVF-1.8", "TVF-2.5");
	private static final List<String> DLU_MODELS = Arrays.asList("DLU-0.6", "DLU-0.7", "DLU-0.9", "DLU-1.2");
	private static final List<String> DLP_MODELS = Arrays.asList("DLPro-0.9", "DLPro-1.2", "DLPro-1.2M", "DLPro-1.5", "DLPro-1.8");
	private static final List<String> DLS_MODELS = Arrays.asList("DLSlim-0.9", "DLSlim-1.2", "DLSlim-1.2M", "DLSlim-1.5", "DLSlim-1.8");

	private final String family;
	private final String name;
	private final ProductType type;
	private final ProductSubType subType;

	ProductFamily(String family, String name, ProductType type, ProductSubType subType) {
		this.family = family;
		this.name = name;
		this.type = type;
		this.subType = subType;
	}

	/**
	 * Retrieves {@link #family}
	 *
	 * @return value of {@link #family}
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #type}
	 *
	 * @return value of {@link #type}
	 */
	public ProductType getType() {
		return type;
	}

	/**
	 * Retrieves {@link #subType}
	 *
	 * @return value of {@link #subType}
	 */
	public ProductSubType getSubType() {
		return subType;
	}

	public static ProductFamily getByFamily(String family) {
		return Arrays.stream(values()).filter(p -> p.family.equals(family)).findFirst().orElse(UNDEFINED);
	}

	/**
	 * Checks if the given product belongs to the LCD product family.
	 *
	 * @param productFamily the product family to check
	 * @return true if the product is an LCD, false otherwise
	 */
	public static boolean isLCDProduct(String productFamily) {
		return productFamily != null
				&& Arrays.stream(values()).anyMatch(p -> p.family.equals(productFamily) && p.type == ProductType.LCD);
	}

	public static boolean hasModel(String productFamily, String productModel) {
		if (productFamily == null || productModel == null) {
			return false;
		}
		ProductFamily family = getByFamily(productFamily);
		switch (family) {
			case MATRIX_G3: {
				return MATRIX_G3_MODELS.contains(productModel);
			}
			case DLX: {
				return DLX_MODELS.contains(productModel);
			}
			case TWA: {
				return TWA_MODELS.contains(productModel);
			}
			case TWS: {
				return TWS_MODELS.contains(productModel);
			}
			case TVF: {
				return TVF_MODELS.contains(productModel);
			}
			case DIRECT_LIGHT_ULTRA: {
				return DLU_MODELS.contains(productModel);
			}
			case DIRECT_LIGHT_PRO: {
				return DLP_MODELS.contains(productModel);
			}
			case DIRECT_LIGHT_SLIM: {
				return DLS_MODELS.contains(productModel);
			}
			default:
				return false;
		}
	}
}

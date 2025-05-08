package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.products;

import java.util.Arrays;

/**
 * Enum representing different product families with their respective details.
 *
 * @author Kevin / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum ProductFamily {
	MATRIX_G3("MATRIX_G3", "Matrix G3", ProductType.LCD, ProductSubType.NONE),
	DLX("DLX", "DLX", ProductType.LED, ProductSubType.TWO_K_PIXEL),
	TWA("TWA", "TWA", ProductType.LED, ProductSubType.OPEN_PIXEL),
	TWS("TWS", "TWS", ProductType.LED, ProductSubType.OPEN_PIXEL),
	TWF("TWF", "TWF", ProductType.LED, ProductSubType.OPEN_PIXEL),
	DIRECT_LIGHT_ULTRA("DLU", "DirectLight Ultra", ProductType.LED, ProductSubType.ENHANCED_OPEN_PIXEL),
	DIRECT_LIGHT_PRO("DLPro", "DirectLight Pro", ProductType.LED, ProductSubType.ENHANCED_OPEN_PIXEL),
	DIRECT_LIGHT_SLIM("DLSlim", "DirectLight Slim", ProductType.LED, ProductSubType.ENHANCED_OPEN_PIXEL);

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
		return Arrays.stream(values()).filter(p -> p.family.equals(family)).findFirst().orElse(null);
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
}

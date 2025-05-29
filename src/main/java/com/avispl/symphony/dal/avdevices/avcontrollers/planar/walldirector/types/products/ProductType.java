package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.products;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;

/**
 * Enum representing different product types with their respective details.
 *
 * @author Kevin / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum ProductType {
	NONE(Constant.NONE),
	LCD("LCD"),
	LED("LED");

	private final String name;

	ProductType(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}

package com.dhisco.common.api.pa.distribute.request;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(Include.NON_EMPTY)
@ApiModel(description = "This Model represents Product Change Distribution Request.")
public class ProductRequest extends ChangeDistributionRequest {

	@ApiModelProperty(value = "Must be Present in Configuration Service. ", required = true, position = 3)
	@NotBlank(message = "Brand cannot be Blank.")
	private String brand;

	@ApiModelProperty(value = "Must be Present in Configuration Service. ", required = true, position = 4)
	@NotBlank(message = "Property cannot be Blank.")
	private String property;

	@ApiModelProperty(value = "Must be Present in Configuration Service. ", required = true, position = 5)
	@NotBlank(message = "DpPropertyCode cannot be Blank.")
	private String dpPropertyCode;

	@ApiModelProperty(value = "Must be Present in Configuration Service. ", required = true, position = 6)
	@NotBlank(message = "Inventory cannot be Blank.")
	private String inventory;

	@ApiModelProperty(value = "Must be Present in Configuration Service. ", required = true, position = 7)
	@NotBlank(message = "DpInventory cannot be Blank.")
	private String dpInventoryCode;

	@ApiModelProperty(value = "Must be Present in Configuration Service. ", required = true, position = 8)
	@NotBlank(message = "Rateplan cannot be Blank.")
	private String ratePlan;

	@ApiModelProperty(value = "Must be Present in Configuration Service. ", required = true, position = 9)
	@NotBlank(message = "DpRateplan cannot be Blank.")
	private String dpRatePlanCode;

	@ApiModelProperty(position = 10)
	private String description;

	@ApiModelProperty(position = 11)
	private Set<String> amenityCodes;

	@NotEmpty(message = "Cancel Policies cannot be null . Atleast one policy must be specified .")
	private List<@NotNull CancelPolicy> cancelPolicies;

	public ProductRequest() {
	}

	public ProductRequest(String demandPartner, String eventType) {
		super(demandPartner, eventType);
	}

	public String getBrand() {
		return brand;
	}

	public ProductRequest setBrand(String brand) {
		this.brand = brand;
		return this;
	}

	public String getProperty() {
		return property;
	}

	public ProductRequest setProperty(String property) {
		this.property = property;
		return this;
	}

	public String getDpPropertyCode() {
		return dpPropertyCode;
	}

	public void setDpPropertyCode(String dpPropertyCode) {
		this.dpPropertyCode = dpPropertyCode;
	}

	public String getInventory() {
		return inventory;
	}

	public ProductRequest setInventory(String inventory) {
		this.inventory = inventory;
		return this;
	}

	public String getDpInventoryCode() {
		return dpInventoryCode;
	}

	public void setDpInventoryCode(String dpInventoryCode) {
		this.dpInventoryCode = dpInventoryCode;
	}

	public String getRatePlan() {
		return ratePlan;
	}

	public ProductRequest setRatePlan(String ratePlan) {
		this.ratePlan = ratePlan;
		return this;
	}

	public String getDpRatePlanCode() {
		return dpRatePlanCode;
	}

	public void setDpRatePlanCode(String dpRatePlanCode) {
		this.dpRatePlanCode = dpRatePlanCode;
	}

	public String getDescription() {
		return description;
	}

	public ProductRequest setDescription(String description) {
		this.description = description;
		return this;
	}

	public Set<String> getAmenityCodes() {
		return amenityCodes;
	}

	public ProductRequest setAmenityCodes(Set<String> amenityCodes) {
		this.amenityCodes = amenityCodes;
		return this;
	}

	public static class CancelPolicy {
		@NotNull(message = "Demand Partner Cancel Policy Code cannot be blank.")
		private String dpCancelPolicyCode;
		@NotEmpty(message = "Cancel Policy Date Ranges cannot be null or empty.")
		private List<@NotNull LocalDate[]> dateRanges;

		public static CancelPolicy of(String dpCancelPolicyCode, List<LocalDate[]> dateRanges) {
			CancelPolicy cp = new CancelPolicy();
			cp.setDpCancelPolicyCode(dpCancelPolicyCode);
			cp.setDateRanges(dateRanges);
			return cp;
		}

		public CancelPolicy() {
		}

		public String getDpCancelPolicyCode() {
			return dpCancelPolicyCode;
		}

		public List<LocalDate[]> getDateRanges() {
			return dateRanges;
		}

		public void setDpCancelPolicyCode(String dpCancelPolicyCode) {
			this.dpCancelPolicyCode = dpCancelPolicyCode;
		}

		public void setDateRanges(List<LocalDate[]> dateRanges) {
			this.dateRanges = dateRanges;
		}

	}

}

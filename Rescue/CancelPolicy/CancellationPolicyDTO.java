package com.dhisco.product.discovery.beans;

public class CancellationPolicyDTO {

	private short deadlineHoursMin;
	private short deadlineHoursMax;
	private double penaltyPercentageMin;
	private double penaltyPercentageMax;
	private short penaltNights;
	private boolean isFullyRefundable;
	private boolean isNonRefundable;
	private String otaPolicyCode;

	public CancellationPolicyDTO() {
	}

	public CancellationPolicyDTO(short deadlineHoursMin, short deadlineHoursMax, double penaltyPercentageMin,
			double penaltyPercentageMax, short penaltNights, boolean isFullyRefundable, boolean isNonRefundable,
			String otaPolicyCode) {
		super();
		this.deadlineHoursMin = deadlineHoursMin;
		this.deadlineHoursMax = deadlineHoursMax;
		this.penaltyPercentageMin = penaltyPercentageMin;
		this.penaltyPercentageMax = penaltyPercentageMax;
		this.penaltNights = penaltNights;
		this.isFullyRefundable = isFullyRefundable;
		this.isNonRefundable = isNonRefundable;
		this.otaPolicyCode = otaPolicyCode;
	}

	public short getDeadlineHoursMin() {
		return deadlineHoursMin;
	}

	public short getDeadlineHoursMax() {
		return deadlineHoursMax;
	}

	public double getPenaltyPercentageMin() {
		return penaltyPercentageMin;
	}

	public double getPenaltyPercentageMax() {
		return penaltyPercentageMax;
	}

	public short getPenaltNights() {
		return penaltNights;
	}

	public String getOtaPolicyCode() {
		return otaPolicyCode;
	}

	public boolean isFullyRefundable() {
		return isFullyRefundable;
	}

	public boolean isNonRefundable() {
		return isNonRefundable;
	}

	public void setDeadlineHoursMin(short deadlineHoursMin) {
		this.deadlineHoursMin = deadlineHoursMin;
	}

	public void setDeadlineHoursMax(short deadlineHoursMax) {
		this.deadlineHoursMax = deadlineHoursMax;
	}

	public void setPenaltyPercentageMin(double penaltyPercentageMin) {
		this.penaltyPercentageMin = penaltyPercentageMin;
	}

	public void setPenaltyPercentageMax(double penaltyPercentageMax) {
		this.penaltyPercentageMax = penaltyPercentageMax;
	}

	public void setPenaltNights(short penaltNights) {
		this.penaltNights = penaltNights;
	}

	public void setOtaPolicyCode(String otaPolicyCode) {
		this.otaPolicyCode = otaPolicyCode;
	}

	public void setFullyRefundable(boolean isFullyRefundable) {
		this.isFullyRefundable = isFullyRefundable;
	}

	public void setNonRefundable(boolean isNonRefundable) {
		this.isNonRefundable = isNonRefundable;
	}

	@Override
	public String toString() {
		return "CancellationPolicyDTO [deadlineHoursMin=" + deadlineHoursMin + ", deadlineHoursMax=" + deadlineHoursMax
				+ ", penaltyPercentageMin=" + penaltyPercentageMin + ", penaltyPercentageMax=" + penaltyPercentageMax
				+ ", penaltNights=" + penaltNights + ", isFullyRefundable=" + isFullyRefundable + ", isNonRefundable="
				+ isNonRefundable + ", otaPolicyCode=" + otaPolicyCode + "]";
	}

}
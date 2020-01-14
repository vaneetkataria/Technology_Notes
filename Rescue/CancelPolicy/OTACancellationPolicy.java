package com.dhisco.product.discovery.beans;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

/***
 * This class {@link OTACancellationPolicy} is supposed to hold cancellation
 * policy mapping between a Property and an OTA .
 * 
 * @author vaneet.kataria
 */

public class OTACancellationPolicy implements CancellationPolicy {

	private static final long serialVersionUID = 1L;

	private String policyCodeFullyRefundable;
	private RangeMap<Short, DeadlineHoursWisePolicy> deadlineHoursWisePolicyRangeMap = TreeRangeMap.create();

	public static OTACancellationPolicy of(List<CancellationPolicyDTO> cancellationPolicy) {
		return new OTACancellationPolicy(cancellationPolicy);
	}

	private OTACancellationPolicy(List<CancellationPolicyDTO> policy) {
		Objects.requireNonNull(policy, "Cannot load Cancellation policy from input null");
		if (policy.isEmpty())
			throw new IllegalArgumentException("Cannot load Cancellation policy from an empty list.");
		load(policy);
	}

	private void load(List<CancellationPolicyDTO> policy) {
		policy.stream().map(this::validatedPolicy).forEach(this::loadDeadlineHoursWise);
	}

	private CancellationPolicyDTO validatedPolicy(CancellationPolicyDTO dto) {
		if (StringUtils.isBlank(dto.getOtaPolicyCode()))
			throw new IllegalArgumentException(MessageFormat.format(
					"Could not load Cancellation policy as OTA cancellation policy is undefined for Cancellation Policy {0} ",
					dto.toString()));
		return dto;
	}

	private void loadDeadlineHoursWise(CancellationPolicyDTO dto) {
		if (dto.isFullyRefundable()) {
			if (Objects.nonNull(policyCodeFullyRefundable))
				throw new IllegalArgumentException(
						"Cannot have multiple ota policy codes as fully refundable policy code .");

			policyCodeFullyRefundable = dto.getOtaPolicyCode();
			return;
		}

		Map<Range<Short>, DeadlineHoursWisePolicy> deadlineHourWisePolicyMap = deadlineHoursWisePolicyRangeMap
				.asMapOfRanges();
		Range<Short> hoursRangeKey = Range.openClosed(dto.getDeadlineHoursMin(), dto.getDeadlineHoursMax());

		DeadlineHoursWisePolicy hoursWisePolicy = deadlineHourWisePolicyMap.get(hoursRangeKey);
		if (Objects.isNull(hoursWisePolicy)) {
			hoursWisePolicy = new DeadlineHoursWisePolicy();
			deadlineHoursWisePolicyRangeMap.put(hoursRangeKey, hoursWisePolicy);
		}
		hoursWisePolicy.add(dto);
	}

	@Override
	public Optional<String> getFullyRefundablePolicy() {
		return Optional.ofNullable(policyCodeFullyRefundable);
	}

	@Override
	public Optional<String> getPolicyForPenaltyPercent(short deadlineHours, double penaltyPercentage) {
		if (deadlineHours == 0)
			return getFullyRefundablePolicy();

		String policyCode = null;
		DeadlineHoursWisePolicy policy = deadlineHoursWisePolicyRangeMap.get(deadlineHours);
		if (Objects.nonNull(policy))
			policyCode = policy.getPercentageWisePenalty(penaltyPercentage);

		return Optional.ofNullable(policyCode);
	}

	@Override
	public Optional<String> getPolicyForPenaltyNights(short deadlineHours, short penaltyNights) {
		if (deadlineHours == 0)
			return getFullyRefundablePolicy();

		String policyCode = null;
		DeadlineHoursWisePolicy policy = deadlineHoursWisePolicyRangeMap.get(deadlineHours);
		if (Objects.nonNull(policy) && penaltyNights >= 1)
			policyCode = penaltyNights > 1 ? policy.getMultiNightPenalty() : policy.getSingleNightPenalty();

		return Optional.ofNullable(policyCode);
	}

	private static class DeadlineHoursWisePolicy {
		Predicate<CancellationPolicyDTO> isNightWisePenalty = dto -> dto.getPenaltyPercentageMin() < 0
				&& dto.getPenaltyPercentageMax() < 0 && dto.getPenaltNights() > 0;

		String singleNightPnltyCode;
		String multiNightPnltyCode;
		RangeMap<Double, String> prcntgWisePenaltyRangeMap = TreeRangeMap.create();

		DeadlineHoursWisePolicy add(CancellationPolicyDTO dto) {
			if (isNightWisePenalty.test(dto)) {
				if (dto.getPenaltNights() == 1)
					singleNightPnltyCode = dto.getOtaPolicyCode();
				else
					multiNightPnltyCode = dto.getOtaPolicyCode();
				return this;
			}

			prcntgWisePenaltyRangeMap.put(
					Range.openClosed(dto.getPenaltyPercentageMin(), dto.getPenaltyPercentageMax()),
					dto.getOtaPolicyCode());

			return this;
		}

		String getPercentageWisePenalty(double prcnt) {
			return prcntgWisePenaltyRangeMap.get(prcnt);
		}

		String getSingleNightPenalty() {
			return singleNightPnltyCode;
		}

		String getMultiNightPenalty() {
			return multiNightPnltyCode;
		}

		@Override
		public String toString() {
			return "DeadlineHourWisePolicy [isNightWisePenalty=" + isNightWisePenalty + ", singleNightPenalty="
					+ singleNightPnltyCode + ", multiNightPenalty=" + multiNightPnltyCode
					+ ", percntgWisePenaltyRangeMap=" + prcntgWisePenaltyRangeMap + "]";
		}

	}

	@Override
	public String toString() {
		return "OTACancellationPolicy [policyCodeFullyRefundable=" + policyCodeFullyRefundable
				+ ", deadlineHourWisePolicyRangeMap=" + deadlineHoursWisePolicyRangeMap + "]";
	}

}

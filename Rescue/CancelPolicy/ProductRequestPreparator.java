package com.dhisco.product.discovery.consumer;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.dhisco.common.api.pa.distribute.request.ProductRequest;
import com.dhisco.p2d.stp.model.StpRenderedShop;
import com.dhisco.stp.commons.model.RenderedShop;
import com.dhisco.stp.commons.model.dis2pr.CancelPolicy;
import com.dhisco.stp.commons.model.dis2pr.PenaltyPercentageCostQualifier;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ProductRequestPreparator {

	private static final Logger LOG = LoggerFactory.getLogger(ProductRequestPreparator.class);

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Preares final ProductRequest to be sent to Product Automation to
	 * create/update product on OTA.
	 * 
	 * @param dpPropertyCode
	 * @param dpRoomCode
	 * @param dpRateCode
	 * @param renderedShops
	 * @return
	 */

	public ProductRequest prepare(String dpPropertyCode, String dpRoomCode, String dpRateCode,
			List<StpRenderedShop> stpRenderedShops) {
		try {
			/** Validate inputs */
			Assert.isTrue(StringUtils.isNotBlank(dpPropertyCode),
					"DpPropertyCode cannot be null or blank. Cannot prepare Product Request.");
			Assert.isTrue(StringUtils.isNotBlank(dpRoomCode),
					"DpRoomCode cannot be null or blank. Cannot prepare Product Request.");
			Assert.isTrue(StringUtils.isNotBlank(dpRateCode),
					"DpRateCode cannot be null or blank. Cannot prepare Product Request.");
			Assert.notEmpty(stpRenderedShops,
					"Stp Rendered shop list cannot be null or empty. Cannot prepare Product Request.");

			/** Prepate Product Request */
			ProductRequest request = new ProductRequest();
			StpRenderedShop shop = stpRenderedShops.get(0);
			request.setBrand(shop.getStpRenderedShopKey().getBrand());
			request.setProperty(shop.getStpRenderedShopKey().getPid());
			request.setDpPropertyCode(dpPropertyCode);
			request.setInventory(shop.getStpRenderedShopKey().getRtc());
			request.setDpInventoryCode(dpRoomCode);
			request.setRatePlan(shop.getStpRenderedShopKey().getRpc());
			request.setDpRatePlanCode(dpRateCode);
			/** Prepare Consolidated Cancel Policy from multiple days shopped */
			List<MergedCancelPolicy> mergedPolicies = CancelPolicyConsolidator.of()
					.consolidate(stpRenderedShops.stream().map(this::mapToRenderedShop).collect(Collectors.toList()));
			request.setCancelPolicies(mergedPolicies.stream().map(mp -> CancelPolicy.of(mp.getDpCancelPolicyCode() , mp.getDateRanges())).collect(Collectors.toList())));
		} catch (Exception e) {
			throw e;
		}

		return request;
	}

	private RenderedShop mapToRenderedShop(StpRenderedShop stpRenderedShop) {
		Assert.isTrue(Objects.nonNull(stpRenderedShop.getShop()) && StringUtils.isNotBlank(stpRenderedShop.getShop()),
				"Rendered shop Json cannot be null or empty.");
		try {
			return objectMapper.readValue(stpRenderedShop.getShop(), RenderedShop.class);
		} catch (Exception e) {
			LOG.error("Exception occured while Deserialising Rendred shop Json {} to Rendeed shop object",
					stpRenderedShop.getShop());
			throw new RuntimeException(MessageFormat.format(
					"Exception occured while Deserialising Rendred shop Json {0} to Rendeed shop object",
					stpRenderedShop.getShop()), e);
		}
	}

	/***
	 * 
	 * Stores multiday shopped Cancel policy of a product and consolidates them
	 * into OTA acceptable generic format cancel policies.
	 * 
	 * @author vaneet.kataria
	 *
	 */

	private static class CancelPolicyConsolidator {

		public static CancelPolicyConsolidator of() {
			return new CancelPolicyConsolidator();
		}

		/**
		 * Indicates if some day among shopped days is present at which Cancel
		 * policy was not found . Those days will finally be assigned more
		 * restrictive cancellation policy between its left nearest and right
		 * nearest day cancel policies.
		 */
		private boolean isUnknownPolicyDaysPresent;
		/**
		 * Holds List of Indexes of days for which cancel policy was unknown.
		 */
		private List<Integer> unknownPolicyIndexes;
		/**
		 * Latest Known policy
		 */
		private SimplifiedCancelPolicy latestKnownPolicy;
		/**
		 * Holds genrally 365 days Cancel policies of 365 days shopped
		 * responses.
		 */
		private List<SimplifiedCancelPolicy> dayWisePolicyList;

		/**
		 * Map of Cancel Policy ID vs Occurance object
		 */
		private Map<String, MergedCancelPolicy> policyOccuranceMap;

		public List<MergedCancelPolicy> consolidate(List<RenderedShop> renderedShops) {
			renderedShops.stream().forEach(this::add);
			assignPolicyToUnknownPolicyDays();
			buildOccuranceMap();
			return categoriseCancelPolicies();
		}

		private void add(RenderedShop shop) {
			Optional<SimplifiedCancelPolicy> simplifiedPolicy = CancelPolicySimplifier.of().simplify(shop);
			if (simplifiedPolicy.isPresent())
				addWhenPolicyKnown(simplifiedPolicy.get());
			else
				addWhenPolicyUnknown();
		}

		private void addWhenPolicyKnown(SimplifiedCancelPolicy simplifiedPolicy) {
			if (!isUnknownPolicyDaysPresent) {
				dayWisePolicyList.add(simplifiedPolicy);
			} else {
				dayWisePolicyList.add(simplifiedPolicy);
				SimplifiedCancelPolicy restrictivePolicy = chooseRestrictive(latestKnownPolicy, simplifiedPolicy);
				assignPolicyToUnknownPolicyDays(restrictivePolicy);
			}
			latestKnownPolicy = simplifiedPolicy;
		}

		private void assignPolicyToUnknownPolicyDays() {
			/**
			 * Case when Not even a single cancel policy was present among all
			 * shopped days .
			 */
			if (isUnknownPolicyDaysPresent) {
				if (Objects.isNull(latestKnownPolicy))
					throw new RuntimeException("No cancellation policy found among all shopped resonses.");
				/**
				 * Case when some last days remained with unknown policy.
				 */
				assignPolicyToUnknownPolicyDays(latestKnownPolicy);
			}
		}

		private void assignPolicyToUnknownPolicyDays(SimplifiedCancelPolicy policy) {
			unknownPolicyIndexes.forEach(index -> dayWisePolicyList.add(index, policy));
			isUnknownPolicyDaysPresent = false;
			unknownPolicyIndexes.clear();
		}

		private SimplifiedCancelPolicy chooseRestrictive(SimplifiedCancelPolicy cancelPolicyX,
				SimplifiedCancelPolicy cancelPolicyY) {
			// TODO
			return cancelPolicyX;
		}

		private void addWhenPolicyUnknown() {
			isUnknownPolicyDaysPresent = true;
			dayWisePolicyList.add(null);
			unknownPolicyIndexes.add(dayWisePolicyList.size() - 1);
		}

		private void buildOccuranceMap() {
			policyOccuranceMap = dayWisePolicyList.stream()
					.collect(Collectors.toMap(SimplifiedCancelPolicy::getDpCancelPolicyCode, MergedCancelPolicy::of,
							MergedCancelPolicy::merge, HashMap::new));
		}

		private List<MergedCancelPolicy> categoriseCancelPolicies() {
			/** Sort Policies based on occurances */
			return policyOccuranceMap.entrySet().stream().map(Entry::getValue)
					.sorted(Comparator.comparing(MergedCancelPolicy::getCount)).collect(Collectors.toList());
		}

	}

	/***
	 * Simplifies Rendered shop object as SimplifiedCancelPolicy object.
	 * 
	 * @author vaneet.kataria
	 *
	 */
	private static class CancelPolicySimplifier {

		private static CancelPolicySimplifier of() {
			return new CancelPolicySimplifier();
		}

		private static final short NON_REFUNDABLE_DEADLINE_HOURS = 8760;
		private static final double FULL_PENTALTY_PERECENT = 100d;

		private Predicate<CancelPolicy> isDateTimeBasedPolicy = cp -> Objects.nonNull(cp.getCancellationDate())
				|| Objects.nonNull(cp.getCancellationTime());

		private Predicate<CancelPolicy> isNightWisePenalty = cp -> Objects.nonNull(cp.getPenaltyNights())
				&& cp.getPenaltyNights() > 0 && Objects.isNull(cp.getPenaltyPercentage());

		private Optional<SimplifiedCancelPolicy> simplify(RenderedShop shop) {
			if (Objects.isNull(shop.getCancelPolicy()))
				return Optional.empty();
			/** Generate simplied View of Cancel Policy */
			SimplifiedCancelPolicy simplifiedPolicy = SimplifiedCancelPolicy.of();
			simplifiedPolicy.setDeadlineHours(calculateDeadLineHours(shop));
			setPenalty(simplifiedPolicy, shop);
			simplifiedPolicy.setDpCancelPolicyCode("153"); // TODO
			simplifiedPolicy.setNonRefundable(false); // TODO
			simplifiedPolicy.setCancelPolicy(shop.getCancelPolicy());
			simplifiedPolicy.setShopDate(shop.getDate());
			return Optional.of(simplifiedPolicy);
		}

		private short calculateDeadLineHours(RenderedShop shop) {
			int deadlineHours = -1;
			CancelPolicy cancelpolicy = shop.getCancelPolicy();

			if (isDateTimeBasedPolicy.test(cancelpolicy)) {
				LocalDate cancellationDate = cancelpolicy.getCancellationDate();
				LocalTime cancellationTime = cancelpolicy.getCancellationTime();
				if (Objects.isNull(cancellationDate))
					cancellationDate = shop.getDate();
				if (Objects.isNull(cancellationTime))
					cancellationTime = LocalTime.MAX;
			}

			else if (false) {
				// <RatePlan Description = "NonRefundable"> case.
				// TODO
			}

			else {
				switch (cancelpolicy.getOffsetIndicator()) {
				case BA:
					switch (cancelpolicy.getIntervalUnit()) {
					case H:
						deadlineHours = cancelpolicy.getTimeInterval();
						break;
					case D:
						deadlineHours = cancelpolicy.getTimeInterval() * 24;
						break;
					case W:
						deadlineHours = cancelpolicy.getTimeInterval() * 24 * 7;
						break;
					case M:
						// TODO
						break;
					}
					break;
				case AB:
					deadlineHours = NON_REFUNDABLE_DEADLINE_HOURS;
				}
			}
			return (short) deadlineHours;
		}

		private void setPenalty(SimplifiedCancelPolicy simplifiedPolicy, RenderedShop shop) {
			CancelPolicy cancelpolicy = shop.getCancelPolicy();
			if (isNightWisePenalty.test(cancelpolicy))
				simplifiedPolicy.setPenaltyNights(cancelpolicy.getPenaltyNights().byteValue());

			else if (PenaltyPercentageCostQualifier.F.equals(cancelpolicy.getPercentageQualifier())
					|| PenaltyPercentageCostQualifier.N.equals(cancelpolicy.getPercentageQualifier())
					|| PenaltyPercentageCostQualifier.L.equals(cancelpolicy.getPercentageQualifier()))
				simplifiedPolicy.setPenaltyPrcnt(FULL_PENTALTY_PERECENT);

			else
				simplifiedPolicy.setPenaltyPrcnt(cancelpolicy.getPenaltyPercentage().doubleValue());
		}

	}

	private static class SimplifiedCancelPolicy {
		protected short deadlineHours;
		protected byte penaltyNights;
		protected double penaltyPrcnt;
		protected String dpCancelPolicyCode;
		protected LocalDate shopDate;
		protected CancelPolicy cancelPolicy;
		protected boolean isNonRefundable;

		public static SimplifiedCancelPolicy of() {
			return new SimplifiedCancelPolicy();
		}

		public String getDpCancelPolicyCode() {
			return dpCancelPolicyCode;
		}

		public LocalDate getShopDate() {
			return shopDate;
		}

		public boolean isNonRefundable() {
			return isNonRefundable;
		}

		public void setCancelPolicy(CancelPolicy cancelPolicy) {
			this.cancelPolicy = cancelPolicy;
		}

		public void setDeadlineHours(short deadlineHours) {
			this.deadlineHours = deadlineHours;
		}

		public void setPenaltyNights(byte penaltyNights) {
			this.penaltyNights = penaltyNights;
		}

		public void setPenaltyPrcnt(double penaltyPrcnt) {
			this.penaltyPrcnt = penaltyPrcnt;
		}

		public void setDpCancelPolicyCode(String dpCancelPolicyCode) {
			this.dpCancelPolicyCode = dpCancelPolicyCode;
		}

		public void setShopDate(LocalDate shopDate) {
			this.shopDate = shopDate;
		}

		public void setNonRefundable(boolean isNonRefundable) {
			this.isNonRefundable = isNonRefundable;
		}

	}

	private static class MergedCancelPolicy extends SimplifiedCancelPolicy {

		public static MergedCancelPolicy of(SimplifiedCancelPolicy policy) {
			MergedCancelPolicy merged = new MergedCancelPolicy();
			merged.deadlineHours = policy.deadlineHours;
			merged.penaltyPrcnt = policy.penaltyPrcnt;
			merged.penaltyNights = policy.penaltyNights;
			merged.cancelPolicy = policy.cancelPolicy;
			merged.dpCancelPolicyCode = policy.dpCancelPolicyCode;
			merged.isNonRefundable = policy.isNonRefundable;
			merged.dateRanges = new ArrayList<>();
			merged.addNewDateRange(policy.getShopDate());
			return merged;
		}

		private void addNewDateRange(LocalDate shopDate) {
			this.dateRanges.add(Stream.of(shopDate, shopDate).toArray(LocalDate[]::new));

		}

		private int count = 1;
		private List<LocalDate[]> dateRanges;

		public int getCount() {
			return count;
		}

		public List<LocalDate[]> getDateRanges() {
			return dateRanges;
		}

		public void setDateRanges(List<LocalDate[]> dateRanges) {
			this.dateRanges = dateRanges;
		}

		public MergedCancelPolicy merge(MergedCancelPolicy m2) {
			count++;
			mergePolicyDateRange(m2);
			return this;
		}

		private void mergePolicyDateRange(MergedCancelPolicy m2) {
			/***
			 * Getting last known Date Range.
			 */
			LocalDate[] lastKnownRange = this.dateRanges.get(this.dateRanges.size() - 1);

			/***
			 * If new Shop date is next date to the latest known date range
			 * End-date then expand latest known date range by one day by
			 * replacing new shop date with latest known date range End-date .
			 * 
			 * For example if <1 Jan , 3 Jan > is present as date range and new
			 * shop date 4 jan is to be merged then set date range to <1 Jan , 4
			 * Jan >
			 * 
			 * else if new shop date 5 jan is to be merged then add a new date
			 * range <5 jan , 5 jan> to date ranges .
			 */
			if (lastKnownRange[1].plusDays(1).equals(m2.getShopDate()))
				lastKnownRange[1] = m2.getShopDate();
			else
				addNewDateRange(m2.getShopDate());
		}

	}

}

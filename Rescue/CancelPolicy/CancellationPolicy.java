package com.dhisco.product.discovery.beans;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author vaneet.kataria
 *
 */
public interface CancellationPolicy extends Serializable {

	/**
	 * @return
	 * 
	 *         <Pre>
	 * Implementing client needs to return OTA mapped Cancellation policy ID 
	 * for fully Refundable stay .
	 *         </Pre>
	 * 
	 * 
	 */

	Optional<CancellationPolicyDTO> getFullyRefundablePolicy();

	/**
	 * 
	 * @param deadlineHours
	 * @param penaltyPercentage
	 * 
	 * @return
	 * 
	 *         <pre>
	 *  Implementing client needs to return OTA mapped Cancellation policy ID 
	 *  for stays which charge input penaltyPercentage after input deadlineHours
	 * 
	 *         </pre>
	 */

	Optional<CancellationPolicyDTO> getPolicyForPenaltyPercent(short deadlineHours, double penaltyPercentage);

	/**
	 * 
	 * @param deadlineHours
	 * @param penaltyNights
	 * 
	 * @return
	 * 
	 *         <pre>
	 *  Implementing client needs to return OTA mapped Cancellation policy ID 
	 *  for stays which charge input penaltyNights after input deadlineHours
	 * 
	 *         </pre>
	 */

	Optional<CancellationPolicyDTO> getPolicyForPenaltyNights(short deadlineHours, short penaltyNights);

}

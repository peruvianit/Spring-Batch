package it.peruvianit.polices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

/**
 * The Class FileVerificationSkipper.
 *
 * @author ashraf
 */
public class VerificationSkipper implements SkipPolicy {
	
	private static final Logger logger = LoggerFactory.getLogger(VerificationSkipper.class);

	@Override
	public boolean shouldSkip(Throwable exception, int skipCount) throws SkipLimitExceededException {
		logger.debug("shouldSkip:" + exception.getMessage());
		
		return true;
	}
}
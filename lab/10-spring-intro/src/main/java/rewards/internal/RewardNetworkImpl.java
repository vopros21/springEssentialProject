package rewards.internal;

import common.money.MonetaryAmount;
import rewards.AccountContribution;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;
import rewards.internal.account.Account;
import rewards.internal.account.AccountRepository;
import rewards.internal.restaurant.Restaurant;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.RewardRepository;

/**
 * Rewards an Account for Dining at a Restaurant.
 * <p>
 * The sole Reward Network implementation. This object is an application-layer service responsible for coordinating with
 * the domain-layer to carry out the process of rewarding benefits to accounts for dining.
 * <p>
 * Said in other words, this class implements the "reward account for dining" use case.
 * <p>
 * In this lab, you are going to exercise the following:
 * - Understanding internal operations that need to be performed to implement
 * "rewardAccountFor" method of the "RewardNetworkImpl" class
 * - Writing test code using stub implementations of dependencies
 * - Writing both target code and test code without using Spring framework
 */
public class RewardNetworkImpl implements RewardNetwork {

    private AccountRepository accountRepository;

    private RestaurantRepository restaurantRepository;

    private RewardRepository rewardRepository;

    /**
     * Creates a new reward network.
     *
     * @param accountRepository    the repository for loading accounts to reward
     * @param restaurantRepository the repository for loading restaurants that determine how much to reward
     * @param rewardRepository     the repository for recording a record of successful reward transactions
     */
    public RewardNetworkImpl(AccountRepository accountRepository, RestaurantRepository restaurantRepository,
                             RewardRepository rewardRepository) {
        this.accountRepository = accountRepository;
        this.restaurantRepository = restaurantRepository;
        this.rewardRepository = rewardRepository;
    }

    public RewardConfirmation rewardAccountFor(Dining dining) {
        String creditCardNumber = dining.getCreditCardNumber();
        String merchantNumber = dining.getMerchantNumber();
        Account account = accountRepository.findByCreditCard(creditCardNumber);
        Restaurant restaurant = restaurantRepository.findByMerchantNumber(merchantNumber);
        MonetaryAmount amount = restaurant.calculateBenefitFor(account, dining);
        AccountContribution contribution = account.makeContribution(amount);
        accountRepository.updateBeneficiaries(account);

        return rewardRepository.confirmReward(contribution, dining);
    }
}
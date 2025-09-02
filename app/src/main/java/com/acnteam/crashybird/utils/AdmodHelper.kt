package com.acnteam.crashybird.utils

import android.app.Activity
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.AdError

object AdMobHelper {
    private var rewardedAd: RewardedAd? = null
    private var rewardEarned: RewardItem? = null

    fun loadRewardedAd(
        activity: Activity,
        adUnitId: String,
        onLoaded: (() -> Unit)? = null
    ) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            activity,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                    Toast.makeText(activity, "Load quảng cáo thất bại", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    fun showRewardedAd(
        activity: Activity,
        onReward: (RewardItem) -> Unit,
        onAdDismissed: () -> Unit
    ) {
        rewardedAd?.let { ad ->
            rewardEarned = null

            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    rewardEarned?.let { earned ->
                        onReward(earned)
                    }
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Toast.makeText(activity, "Không thể hiển thị quảng cáo", Toast.LENGTH_SHORT).show()
                    rewardedAd = null
                    onAdDismissed()
                }
            }

            ad.show(activity) { rewardItem ->
                rewardEarned = rewardItem
            }
        } ?: run {
            Toast.makeText(activity, "Quảng cáo chưa sẵn sàng", Toast.LENGTH_SHORT).show()
            onAdDismissed()
        }
    }
}

package com.example.googlepaytest1

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.wallet.WalletConstants
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.common.api.ApiException
//import android.support.test.orchestrator.junit.BundleJUnitUtils.getResult
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters
import java.util.Arrays.asList
import com.google.android.gms.wallet.CardRequirements
import com.google.android.gms.wallet.TransactionInfo
import com.google.android.gms.wallet.PaymentDataRequest
import java.util.Arrays
import com.google.android.gms.wallet.AutoResolveHelper
import android.app.Activity
import android.media.session.MediaSession
import android.util.Log
import android.widget.Toast
import com.google.android.gms.identity.intents.model.UserAddress
import com.google.android.gms.wallet.CardInfo
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentMethodToken


class MainActivity : AppCompatActivity() {

    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 7123

    private lateinit var btnSetupGooglePay: Button

    private lateinit var paymentsClient: PaymentsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paymentsClient = Wallet.getPaymentsClient(
            this,
            Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build()
        )

        btnSetupGooglePay = findViewById(R.id.btnSetupGooglePay)

        isReadyToPay()




        btnSetupGooglePay.setOnClickListener {

            val request = createPaymentDataRequest()
            if (request != null) {
                AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    this,
                    LOAD_PAYMENT_DATA_REQUEST_CODE
                )
            }

//            val launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.walletnfcrel");
//            if (launchIntent != null) {
//                startActivity(launchIntent);//null pointer check in case package name was not found
//            } else {
//                val intent = Intent(Intent.ACTION_VIEW).apply {
//                    data = Uri.parse(
//                        "https://play.google.com/store/apps/details?id=com.google.android.apps.walletnfcrel")
//                    setPackage("com.android.vending")
//                }
//                startActivity(intent)
//            }
        }
    }

    private fun isReadyToPay() {
        val request = IsReadyToPayRequest.newBuilder()
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
            .build()
        paymentsClient.isReadyToPay(request).addOnCompleteListener(
            OnCompleteListener<Boolean> { task ->
                try {
                    val result = task.getResult(ApiException::class.java)!!
                    if (result == true) {
                        btnSetupGooglePay.visibility = View.VISIBLE
                    } else {
                        btnSetupGooglePay.visibility = View.INVISIBLE
                    }
                } catch (exception: ApiException) {
                }
            }
        )
    }

    private fun createTokenizationParameters(): PaymentMethodTokenizationParameters {
        return PaymentMethodTokenizationParameters.newBuilder()
            .setPaymentMethodTokenizationType(
                WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY
            )
            .addParameter("gateway", "stripe")
            .addParameter(
                "stripe:publishableKey",
                "pk_test_TYooMQauvdEDq54NiTphI7jx"
            )
            .addParameter("stripe:version", "2018-11-08")
            .build()
    }

    private fun createPaymentDataRequest(): PaymentDataRequest {
        return PaymentDataRequest.newBuilder()
            .setTransactionInfo(
                TransactionInfo.newBuilder()
                    .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                    .setTotalPrice("10.00")
                    .setCurrencyCode("USD")
                    .build()
            )
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
            .setCardRequirements(
                CardRequirements.newBuilder()
                    .addAllowedCardNetworks(
                        Arrays.asList(
                            WalletConstants.CARD_NETWORK_AMEX,
                            WalletConstants.CARD_NETWORK_DISCOVER,
                            WalletConstants.CARD_NETWORK_VISA,
                            WalletConstants.CARD_NETWORK_MASTERCARD
                        )
                    )
                    .build()
            )
            .setPaymentMethodTokenizationParameters(createTokenizationParameters())
            .build()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val paymentData = PaymentData.getFromIntent(data!!)

                        // You can get some data on the user's card, such as the
                        // brand and last 4 digits
                        val info = paymentData!!.cardInfo
                        // You can also pull the user address from the
                        // PaymentData object.
                        val address = paymentData.shippingAddress
                        // This is the raw JSON string version of your Stripe token.
                        val rawToken = paymentData.paymentMethodToken.token
                        Toast.makeText(this, "Stripe token raw: $rawToken", Toast.LENGTH_LONG).show()
                        Log.d("Google_Pay", "rokenizationData is: ")
                        Log.d("Google_Pay", "raw token is: $rawToken")
                        Log.d("Google_Pay", "paymentData: $paymentData")
                        Log.d("Google_Pay", "cardInfo: ${paymentData.cardInfo}")
                        Log.d("Google_Pay", "paymentMethodToken: ${paymentData.paymentMethodToken}")

                        Log.d("Google_Pay", "paymentData.ExtraData: ${paymentData.extraData}")


//                         Now that you have a Stripe token object,
//                         charge that by using the id
//                        Token is a Stripe library library
                        val stripeToken = Token.fromString(rawToken)  // this uses a Stripe Token class
                        if (stripeToken != null) {
//                             This chargeToken function is a call to your own
//                             server, which should then connect to Stripe's
//                             API to finish the charge.
//                            chargeToken(stripeToken!!.getId())

                            Log.d("Google_Pay", "stripe Token: $stripeToken")
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                    }
                    AutoResolveHelper.RESULT_ERROR -> {
                        val status = AutoResolveHelper.getStatusFromIntent(data)
                    }// Log the status for debugging
                    // Generally there is no need to show an error to
                    // the user as the Google Payment API will do that
                    else -> {
                        // Do nothing.
                    }
                }
            }// Breaks the case LOAD_PAYMENT_DATA_REQUEST_CODE
            // Handle any other startActivityForResult calls you may have made.
            else -> {
                // Do nothing.
            }
        }
    }
}
